package io.grimlocations.shared.data.repo

import io.grimlocations.shared.data.domain.*
import io.grimlocations.shared.data.dto.*
import io.grimlocations.shared.framework.data.repo.Repository
import io.grimlocations.shared.framework.util.FourTuple
import io.grimlocations.shared.framework.util.FiveTuple
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
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
}

