package io.grimlocations.data.repo

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.*
import io.grimlocations.framework.data.repo.Repository
import io.grimlocations.framework.util.FourTuple
import io.grimlocations.framework.util.FiveTuple
import io.grimlocations.util.extension.glDatabaseDir
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
            withContext(Dispatchers.IO) { createRollingBackup(MAX_BACKUPS) }
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

        val dbDoesntExist = !suspendedTransactionAsync { MetaTable.exists() }.await()

        if (dbDoesntExist) {
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

            createDefaultEntities()
            createReservedEntities()

            logger.info("Database created")
        }

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

        val (newchar_loc_profile, /*reddit_loc_profile,*/ no_mods_mod, no_difficulties_difficulty) = getReservedEntities()
        RESERVED_PROFILES = listOf(newchar_loc_profile/*, reddit_loc_profile*/)
        RESERVED_NO_MODS_INDICATOR = no_mods_mod
        RESERVED_NO_DIFFICULTIES_INDICATOR = no_difficulties_difficulty

        if (dbDoesntExist) {
            createLocationsFromFile(
                file = File("./external/new_character_locations.csv"),
                profileDTO = newchar_loc_profile,
                modDTO = no_mods_mod,
                difficultyDTO = no_difficulties_difficulty,
            )?.let { error(it) }

//            createLocationsFromFile(
//                file = File("./external/reddit_locations.csv"),
//                profileDTO = reddit_loc_profile,
//                modDTO = no_mods_mod,
//                difficultyDTO = no_difficulties_difficulty,
//            )?.let { error(it) }
        }
    }

    private suspend fun createDefaultEntities() {
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

            newSuspendedTransaction {
                mod.difficulties = SizedCollection(diffList)
            }
        } catch (e: Exception) {
            logger.error("Issue creating the default entities.")
            throw e
        }
    }

    private suspend fun createReservedEntities() {
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
//            val profile2 = newSuspendedTransaction {
//                Profile.new {
//                    name = RESERVED_PROFILE_REDDIT_LOCATIONS_NAME
//                }
//            }

            newSuspendedTransaction {
                mod.difficulties = SizedCollection(listOf(difficulty))
                profile1.mods = SizedCollection(listOf(mod))
//                profile2.mods = SizedCollection(listOf(mod))
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

    private suspend fun getReservedEntities(): Triple<ProfileDTO, ModDTO, DifficultyDTO> =
        try {
            newSuspendedTransaction {
                Triple(
                    Profile.find { ProfileTable.name eq RESERVED_PROFILE_GI_LOCATIONS_NAME }.single().toDTO(),
//                    Profile.find { ProfileTable.name eq RESERVED_PROFILE_REDDIT_LOCATIONS_NAME }.single().toDTO(),
                    Mod.find { ModTable.name eq RESERVED_NO_MODS_INDICATOR_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq RESERVED_NO_DIFFICULTIES_INDICATOR_NAME }.single().toDTO()
                )
            }
        } catch (e: Exception) {
            logger.error("Issue getting the reserved entities.")
            throw e
        }
}

