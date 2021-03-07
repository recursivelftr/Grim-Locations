package io.grimlocations.shared.data.repo

import io.grimlocations.shared.data.domain.*
import io.grimlocations.shared.data.dto.*
import io.grimlocations.shared.framework.data.repo.Repository
import io.grimlocations.shared.framework.util.FourTuple
import io.grimlocations.shared.framework.util.FiveTuple
import io.grimlocations.shared.util.extension.glDatabaseBackupDir
import io.grimlocations.shared.util.extension.glDatabaseDir
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.harawata.appdirs.AppDirs
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SqliteRepository(val appDirs: AppDirs) : Repository {

    private val logger: Logger = LogManager.getLogger()

    private var wasBackupMade = false //Backup is made once per app session only if the db is changed
    private val database: Database
    private val MAX_BACKUPS = 100

    init {
        try {
            logger.info("Loading repository with AppDirs object")
            val dbPath = appDirs.glDatabaseDir
            File(dbPath).mkdirs()
            database = Database.connect("jdbc:sqlite:$dbPath${File.separator}database.db", "org.sqlite.JDBC")
        } catch (e: Exception) {
            logger.error("Issue creating the database.")
            throw e
        }
    }

    suspend fun <T> modifyDatabaseAsync(statement: suspend Transaction.() -> T): Deferred<T> {
        logger.info("Modify database action")
        if (!wasBackupMade) {
            logger.info("Database backup started")
            wasBackupMade = true
            withContext(Dispatchers.IO) { createRollingBackup() }
            logger.info("Database backup made")
        }

        return suspendedTransactionAsync(Dispatchers.IO, statement = statement)
    }

    suspend fun initDb() {
//            val diffTest = Difficulty.wrapRow(Difficulties.select { Difficulties.name eq "Any" }.single())
//
//            val profileTest = Profile.wrapRow(Profiles.select { Profiles.name eq "test" }.single())
//
//            println("Diff Profile: ${diffTest.profiles.single().name}")
//            println("Profile Diff: ${profileTest.difficulties.single().name}")

        if (!suspendedTransactionAsync { MetaTable.exists() }.await()) {
            logger.info("Creating database tables")
            try {
                newSuspendedTransaction {
                    SchemaUtils.create(ProfileTable)
                    SchemaUtils.create(ModTable)
                    SchemaUtils.create(DifficultyTable)
                    SchemaUtils.create(CoordinateTable)
                    SchemaUtils.create(LocationTable)
                    SchemaUtils.create(MetaTable)
                    SchemaUtils.create(ProfileModIntermTable)
                    SchemaUtils.create(ModDifficultyIntermTable)
                }
            } catch (e: Exception) {
                logger.error("Issue creating the tables.")
                throw e
            }

            newSuspendedTransaction {
                Meta.new {
                    version = 0
                }
            }

            val (base_game_mod, normal, veteran, elite, ultimate) = createDefaultEntities()
            DEFAULT_GAME_MOD = base_game_mod
            DEFAULT_GAME_NORMAL_DIFFICULTY = normal
            DEFAULT_GAME_VETERAN_DIFFICULTY = veteran
            DEFAULT_GAME_ELITE_DIFFICULTY = elite
            DEFAULT_GAME_ULTIMATE_DIFFICULTY = ultimate

            val (newchar_loc_profile, reddit_loc_profile, no_mods_mod, no_difficulties_difficulty) = createReservedEntities()
            RESERVED_PROFILES = listOf(newchar_loc_profile, reddit_loc_profile)
            RESERVED_NO_MODS_INDICATOR = no_mods_mod
            RESERVED_NO_DIFFICULTIES_INDICATOR = no_difficulties_difficulty

            createLocationsFromFile(
                file = File(javaClass.getResource("/new_character_locations.csv").toURI()),
                profileDTO = newchar_loc_profile,
                modDTO = no_mods_mod,
                difficultyDTO = no_difficulties_difficulty,
            )?.let { error(it) }

            createLocationsFromFile(
                file = File(javaClass.getResource("/reddit_locations.csv").toURI()),
                profileDTO = reddit_loc_profile,
                modDTO = no_mods_mod,
                difficultyDTO = no_difficulties_difficulty,
            )?.let { error(it) }

            logger.info("Database created")
        } else {
            try {
                newSuspendedTransaction {
                    val version = MetaTable.slice(MetaTable.version).selectAll().single()[MetaTable.version]
                    logger.info("Database version: $version")
                }
            } catch (e: Exception) {
                logger.error("Issue getting the Meta record.")
                throw e
            }

            val (base_game_mod, normal, veteran, elite, ultimate) = getDefaultEntities()
            DEFAULT_GAME_MOD = base_game_mod
            DEFAULT_GAME_NORMAL_DIFFICULTY = normal
            DEFAULT_GAME_VETERAN_DIFFICULTY = veteran
            DEFAULT_GAME_ELITE_DIFFICULTY = elite
            DEFAULT_GAME_ULTIMATE_DIFFICULTY = ultimate

            val (newchar_loc_profile, reddit_loc_profile, no_mods_mod, no_difficulties_difficulty) = getReservedEntities()
            RESERVED_PROFILES = listOf(newchar_loc_profile, reddit_loc_profile)
            RESERVED_NO_MODS_INDICATOR = no_mods_mod
            RESERVED_NO_DIFFICULTIES_INDICATOR = no_difficulties_difficulty
        }
    }

    private fun createRollingBackup() {
        try {
            var backupNumber = 1
            val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val backupDirPath = appDirs.glDatabaseBackupDir
            val backupDir = File(backupDirPath)
            backupDir.mkdirs()
            val files = backupDir.listFiles { it: File -> it.isFile }!!

            if (files.isNotEmpty()) {
                val todaysFiles =
                    files.filter { f -> f.name.contains(date) }.sortedByDescending { it.lastModified() }

                if (todaysFiles.isNotEmpty()) {
                    val name = todaysFiles[0].name
                    val start = name.lastIndexOf("-") + 1
                    val end = name.lastIndexOf(".")
                    backupNumber = name.substring(start, end).toInt() + 1
                }

                if (files.size == MAX_BACKUPS) {
                    files.sortedBy { it.lastModified() }[0].delete()
                }
            }

            val currentDb = File("${appDirs.glDatabaseDir + File.separator}database.db")
            currentDb.copyTo(File("$backupDirPath${File.separator}database-$date-$backupNumber.db"))
        } catch (e: Exception) {
            logger.error("Issue creating the rolling backup.")
            throw e
        }
    }

    private suspend fun createDefaultEntities(): FiveTuple<ModDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO> {
        try {
            val diffList = newSuspendedTransaction {
                listOf(
                    Difficulty.new {
                        name = DEFAULT_GAME_NORMAL_DIFFICULTY_NAME
                    },
                    Difficulty.new {
                        name = DEFAULT_GAME_VETERAN_DIFFICULTY_NAME
                    },
                    Difficulty.new {
                        name = DEFAULT_GAME_ELITE_DIFFICULTY_NAME
                    },
                    Difficulty.new {
                        name = DEFAULT_GAME_ULTIMATE_DIFFICULTY_NAME
                    }
                )
            }

            val mod = newSuspendedTransaction {
                Mod.new {
                    name = DEFAULT_GAME_MOD_NAME
                }
            }

            return newSuspendedTransaction {
                mod.difficulties = SizedCollection(diffList)
                FiveTuple(
                    mod.toDTO(),
                    diffList[0].toDTO(),
                    diffList[1].toDTO(),
                    diffList[2].toDTO(),
                    diffList[3].toDTO(),
                )
            }
        } catch (e: Exception) {
            logger.error("Issue creating the default entities.")
            throw e
        }
    }

    private suspend fun createReservedEntities(): FourTuple<ProfileDTO, ProfileDTO, ModDTO, DifficultyDTO> {
        try {
            val difficulty = newSuspendedTransaction {
                Difficulty.new {
                    name = RESERVED_NO_DIFFICULTIES_INDICATOR_NAME
                }
            }
            val mod = newSuspendedTransaction {
                Mod.new {
                    name = RESERVED_NO_MODS_INDICATOR_NAME
                }
            }
            val profile1 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_GI_LOCATIONS_NAME
                }
            }
            val profile2 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_REDDIT_LOCATIONS_NAME
                }
            }

            return newSuspendedTransaction {
                mod.difficulties = SizedCollection(listOf(difficulty))
                profile1.mods = SizedCollection(listOf(mod))
                profile2.mods = SizedCollection(listOf(mod))

                FourTuple(
                    profile1.toDTO(),
                    profile2.toDTO(),
                    mod.toDTO(),
                    difficulty.toDTO()
                )
            }
        } catch (e: Exception) {
            logger.error("Issue creating the reserved entities.")
            throw e
        }
    }

    private suspend fun getDefaultEntities(): FiveTuple<ModDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO> =
        try {
            newSuspendedTransaction {
                FiveTuple(
                    Mod.find { ModTable.name eq DEFAULT_GAME_MOD_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_NORMAL_DIFFICULTY_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_VETERAN_DIFFICULTY_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_ELITE_DIFFICULTY_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_ULTIMATE_DIFFICULTY_NAME }.single().toDTO()
                )
            }
        } catch (e: Exception) {
            logger.error("Issue getting the default entities.")
            throw e
        }

    private suspend fun getReservedEntities(): FourTuple<ProfileDTO, ProfileDTO, ModDTO, DifficultyDTO> =
        try {
            newSuspendedTransaction {
                FourTuple(
                    Profile.find { ProfileTable.name eq RESERVED_PROFILE_GI_LOCATIONS_NAME }.single().toDTO(),
                    Profile.find { ProfileTable.name eq RESERVED_PROFILE_REDDIT_LOCATIONS_NAME }.single().toDTO(),
                    Mod.find { ModTable.name eq RESERVED_NO_MODS_INDICATOR_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq RESERVED_NO_DIFFICULTIES_INDICATOR_NAME }.single().toDTO()
                )
            }
        } catch (e: Exception) {
            logger.error("Issue getting the reserved entities.")
            throw e
        }

    //The string returned is the error string, if everything went well then it will return null
    suspend fun createLocationsFromFile(
        file: File,
        profileDTO: ProfileDTO,
        modDTO: ModDTO,
        difficultyDTO: DifficultyDTO
    ): String? {
        logger.info("Loading locations from ${file.name}")

        val locList = mutableListOf<LocationDTO>()
        val time = LocalDateTime.now()
        var errorString: String? = null

        file.forEachLine {
            if (errorString != null) //lazy man's way of breaking from the loop (performs a continue on every item when theres an error)
                return@forEachLine

            if (it.isNotBlank()) {
                val loc = it.split(",")
                if (loc.size != 4) {
                    errorString =
                        "The csv file is not in the correct format. " +
                                "The required format for each line is Name,Coordinate1,Coordinate2,Coordinate3.\n" +
                                "The line in question is: $it"
                    logger.error(errorString)
                    return@forEachLine
                }

                val name = loc[0].trim()
                if (name.isBlank()) {
                    errorString = "The name of the location cannot be blank.\n" +
                            "The line in question is: $it"
                    logger.error(errorString)
                    return@forEachLine
                }

                val coord1 = loc[1].trim()
                try {
                    BigDecimal(coord1)
                } catch (e: Exception) {
                    errorString = "The first coordinate is not a number.\n" +
                            "The line in question is: $it"
                    logger.error(errorString, e)
                    return@forEachLine
                }

                val coord2 = loc[2].trim()
                try {
                    BigDecimal(coord2)
                } catch (e: Exception) {
                    errorString = "The second coordinate is not a number.\n" +
                            "The line in question is: $it"
                    logger.error(errorString, e)
                    return@forEachLine
                }

                val coord3 = loc[3].trim()
                try {
                    BigDecimal(coord3)
                } catch (e: Exception) {
                    errorString = "The third coordinate is not a number.\n" +
                            "The line in question is: $it"
                    logger.error(errorString, e)
                    return@forEachLine
                }

                val coordDTO = CoordinateDTO(-1, time, time, coord1, coord2, coord3)
                locList.add(LocationDTO(-1, time, time, name, coordDTO))
            }
        }

        if (errorString != null)
            return errorString

        //create the coordinate if it doesn't exist
        try {
            newSuspendedTransaction {
                locList.forEach {

                    Coordinate.find {
                        (CoordinateTable.coordinate1 eq it.coordinate.coordinate1) and
                                (CoordinateTable.coordinate2 eq it.coordinate.coordinate2) and
                                (CoordinateTable.coordinate3 eq it.coordinate.coordinate3)
                    }.singleOrNull() ?: Coordinate.new {
                        coordinate1 = it.coordinate.coordinate1
                        coordinate2 = it.coordinate.coordinate2
                        coordinate3 = it.coordinate.coordinate3
                    }

                }
            }
        } catch (e: Exception) {
            errorString = "Issue creating coordinates for file ${file.name}"
            logger.error(errorString, e)
        }

        if (errorString != null)
            return errorString

        //create the locations (theres probably a better way to do all this)
        try {
            newSuspendedTransaction {
                val _profile = Profile.findById(profileDTO.id)!!
                val _mod = Mod.findById(modDTO.id)!!
                val _difficulty = Difficulty.findById(difficultyDTO.id)!!

                locList.forEach {
                    val coord = Coordinate.find {
                        (CoordinateTable.coordinate1 eq it.coordinate.coordinate1) and
                                (CoordinateTable.coordinate2 eq it.coordinate.coordinate2) and
                                (CoordinateTable.coordinate3 eq it.coordinate.coordinate3)
                    }.single()

                    Location.find {
                        (LocationTable.profile eq _profile.id) and
                                (LocationTable.mod eq _mod.id) and
                                (LocationTable.difficulty eq _difficulty.id) and
                                (LocationTable.coordinate eq coord.id)
                    }.singleOrNull() ?: Location.new {
                        name = it.name
                        profile = _profile
                        mod = _mod
                        difficulty = _difficulty
                        coordinate = coord
                    }
                }
            }
        } catch (e: Exception) {
            errorString = "Issue creating locations for file ${file.name}"
            logger.error(errorString, e)
        }

        return errorString
    }
}

