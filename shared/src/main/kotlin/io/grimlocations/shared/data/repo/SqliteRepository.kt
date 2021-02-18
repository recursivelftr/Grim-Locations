package io.grimlocations.shared.data.repo

import io.grimlocations.shared.data.domain.*
import io.grimlocations.shared.data.dto.RESERVED_NO_DIFFICULTIES_INDICATOR_NAME
import io.grimlocations.shared.data.dto.RESERVED_NO_MODS_INDICATOR_NAME
import io.grimlocations.shared.data.dto.RESERVED_PROFILE_GI_LOCATIONS_NAME
import io.grimlocations.shared.data.dto.RESERVED_PROFILE_REDDIT_LOCATIONS_NAME
import io.grimlocations.shared.framework.data.repo.Repository
import io.grimlocations.shared.util.Quadruple
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

                val mod = transaction {
                    Mod.new {
                        name = "None"
                    }
                }

                val difficulties = transaction {
                    listOf(
                        Difficulty.new {
                            name = "Normal"
                        },
                        Difficulty.new {
                            name = "Elite"
                        },
                        Difficulty.new {
                            name = "Ultimate"
                        }
                    )
                }

                transaction {
                    mod.difficulties = SizedCollection(difficulties)
                }

                logger.info("Database created")
            } else {
                val version = MetaTable.slice(MetaTable.version).selectAll().single()[MetaTable.version]
                logger.info("Database version: $version")
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

    fun createDefaultEntities() {
        val diffList = transaction {
            listOf(
                Difficulty.new {
                    name = "Normal"
                },
                Difficulty.new {
                    name = "Veteran"
                },
                Difficulty.new {
                    name = "Elite"
                },
                Difficulty.new {
                    name = "Ultimate"
                }
            )
        }

        transaction {
            Mod.new {
                name = "None"
                difficulties = SizedCollection(diffList)
            }
        }
    }

    fun createReservedEntities(): Quadruple<Profile, Profile, Mod, Difficulty> {
        val difficulty = transaction {
            Difficulty.new {
                name = RESERVED_NO_DIFFICULTIES_INDICATOR_NAME
            }
        }
        val mod = transaction {
            Mod.new {
                name = RESERVED_NO_MODS_INDICATOR_NAME
                difficulties = SizedCollection(listOf(difficulty))
            }
        }
        val profile1 = transaction {
            Profile.new {
                name = RESERVED_PROFILE_GI_LOCATIONS_NAME
                mods = SizedCollection(listOf(mod))
            }
        }
        val profile2 = transaction {
            Profile.new {
                name = RESERVED_PROFILE_REDDIT_LOCATIONS_NAME
                mods = SizedCollection(listOf(mod))
            }
        }

        return Quadruple(
            profile1,
            profile2,
            mod,
            difficulty
        )
    }

    fun loadReservedEntities(): Quadruple<Profile, Profile, Mod, Difficulty> = transaction {
        Quadruple(
            Profile.find { ProfileTable.name eq RESERVED_PROFILE_GI_LOCATIONS_NAME }.single(),
            Profile.find { ProfileTable.name eq RESERVED_PROFILE_REDDIT_LOCATIONS_NAME }.single(),
            Mod.find { ModTable.name eq RESERVED_NO_MODS_INDICATOR_NAME }.single(),
            Difficulty.find { DifficultyTable.name eq RESERVED_NO_DIFFICULTIES_INDICATOR_NAME }.single()
        )
    }

    fun loadInitialLocations(
        initialCharacterLocProfile: Profile,
        redditLocProfile: Profile,
        noModsMod: Mod,
        noDiffsDifficulty: Difficulty
    ) {

    }
}

