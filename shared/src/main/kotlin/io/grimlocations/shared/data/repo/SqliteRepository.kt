package io.grimlocations.shared.data.repo

import io.grimlocations.shared.data.domain.*
import io.grimlocations.shared.data.dto.*
import io.grimlocations.shared.framework.data.repo.Repository
import io.grimlocations.shared.util.FourTuple
import io.grimlocations.shared.util.FiveTuple
import io.grimlocations.shared.util.extension.glDatabaseBackupDir
import io.grimlocations.shared.util.extension.glDatabaseDir
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.harawata.appdirs.AppDirs
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SqliteRepository(val appDirs: AppDirs) : Repository {

    private val logger: Logger = LogManager.getLogger()

    private var wasBackupMade = false //Backup is made once per app session only if the db is changed
    private val database: Database
    private val MAX_BACKUPS = 100

    init {
        logger.info("Loading repository with AppDirs object")
        val dbPath = appDirs.glDatabaseDir
        File(dbPath).mkdirs()
        database = initDb("$dbPath${File.separator}database.db")
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

    private fun initDb(dbPath: String) = Database.connect("jdbc:sqlite:$dbPath", "org.sqlite.JDBC").also {
        it.useNestedTransactions = true

        transaction {
//            val diffTest = Difficulty.wrapRow(Difficulties.select { Difficulties.name eq "Any" }.single())
//
//            val profileTest = Profile.wrapRow(Profiles.select { Profiles.name eq "test" }.single())
//
//            println("Diff Profile: ${diffTest.profiles.single().name}")
//            println("Profile Diff: ${profileTest.difficulties.single().name}")

            if (!MetaTable.exists()) {
                logger.info("Creating database tables")
                SchemaUtils.create(ProfileTable)
                SchemaUtils.create(ModTable)
                SchemaUtils.create(DifficultyTable)
                SchemaUtils.create(CoordinateTable)
                SchemaUtils.create(LocationTable)
                SchemaUtils.create(MetaTable)
                SchemaUtils.create(ProfileModIntermTable)
                SchemaUtils.create(ModDifficultyIntermTable)

                transaction {
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


                logger.info("Database created")
            } else {
                val version = MetaTable.slice(MetaTable.version).selectAll().single()[MetaTable.version]
                logger.info("Database version: $version")

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
    }

    private fun createRollingBackup() {
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
    }

    private fun createDefaultEntities(): FiveTuple<ModDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO> {
        val diffList = transaction {
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

        val mod = transaction {
            Mod.new {
                name = DEFAULT_GAME_MOD_NAME
            }
        }

        transaction {
            mod.difficulties = SizedCollection(diffList)
        }

        return FiveTuple(
            mod.toDTO(),
            diffList[0].toDTO(),
            diffList[1].toDTO(),
            diffList[2].toDTO(),
            diffList[3].toDTO(),
        )
    }

    private fun createReservedEntities(): FourTuple<ProfileDTO, ProfileDTO, ModDTO, DifficultyDTO> {
        val difficulty = transaction {
            Difficulty.new {
                name = RESERVED_NO_DIFFICULTIES_INDICATOR_NAME
            }
        }
        val mod = transaction {
            Mod.new {
                name = RESERVED_NO_MODS_INDICATOR_NAME
            }
        }
        val profile1 = transaction {
            Profile.new {
                name = RESERVED_PROFILE_GI_LOCATIONS_NAME
            }
        }
        val profile2 = transaction {
            Profile.new {
                name = RESERVED_PROFILE_REDDIT_LOCATIONS_NAME
            }
        }

        transaction {
            mod.difficulties = SizedCollection(listOf(difficulty))
            profile1.mods = SizedCollection(listOf(mod))
            profile2.mods = SizedCollection(listOf(mod))
        }

        return FourTuple(
            profile1.toDTO(),
            profile2.toDTO(),
            mod.toDTO(),
            difficulty.toDTO()
        )
    }

    private fun getDefaultEntities(): FiveTuple<ModDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO, DifficultyDTO> =
        transaction {
            FiveTuple(
                Mod.find { ModTable.name eq DEFAULT_GAME_MOD_NAME }.single().toDTO(),
                Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_NORMAL_DIFFICULTY_NAME }.single().toDTO(),
                Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_VETERAN_DIFFICULTY_NAME }.single().toDTO(),
                Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_ELITE_DIFFICULTY_NAME }.single().toDTO(),
                Difficulty.find { DifficultyTable.name eq DEFAULT_GAME_ULTIMATE_DIFFICULTY_NAME }.single().toDTO()
            )
        }

    private fun getReservedEntities(): FourTuple<ProfileDTO, ProfileDTO, ModDTO, DifficultyDTO> = transaction {
        FourTuple(
            Profile.find { ProfileTable.name eq RESERVED_PROFILE_GI_LOCATIONS_NAME }.single().toDTO(),
            Profile.find { ProfileTable.name eq RESERVED_PROFILE_REDDIT_LOCATIONS_NAME }.single().toDTO(),
            Mod.find { ModTable.name eq RESERVED_NO_MODS_INDICATOR_NAME }.single().toDTO(),
            Difficulty.find { DifficultyTable.name eq RESERVED_NO_DIFFICULTIES_INDICATOR_NAME }.single().toDTO()
        )
    }

    private fun createLocationsFromFile(
        filename: String,
        profile: ProfileDTO,
        mod: ModDTO,
        difficulty: DifficultyDTO
    ): List<LocationDTO> {
        transaction {
            File(javaClass.getResource(filename).file).forEachLine {
                val loc = it.split(",")

                val coord = Coordinate.find {
                    (CoordinateTable.coordinate1 eq loc[1]) and
                            (CoordinateTable.coordinate2 eq loc[2]) and
                            (CoordinateTable.coordinate3 eq loc[3])
                }.singleOrNull() ?: transaction {

                }


                Location.new {

                }
            }
        }
    }

    fun loadInitialLocations(
        initialCharacterLocProfile: Profile,
        redditLocProfile: Profile,
        noModsMod: Mod,
        noDiffsDifficulty: Difficulty
    ) {

    }
}

