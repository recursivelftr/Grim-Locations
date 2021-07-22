package io.grimlocations.data.repo

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.*
import io.grimlocations.framework.data.repo.Repository
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

    //DB v0 = GL 0.1.0
    //DB v1 = GL 0.2.0
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

            createReservedEntities()

            logger.info("Database created")
        }

        val version = try {
            newSuspendedTransaction {
                val v = MetaTable.slice(MetaTable.version).selectAll().single()[MetaTable.version]
                logger.info("Database version: $v")
                v
            }
        } catch (e: Exception) {
            logger.error("Issue getting the Meta record.")
            throw e
        }

        if (version < 1) { //Addition changes for v1 below
            deleteAdditionalLocations()
            createActAndOtherReservedEntities()
        }

        val (newchar_loc_profile, no_mods_mod, no_difficulties_difficulty) = getReservedEntities()
        val actsAndOtherMap = getActAndOtherReservedEntities()
        RESERVED_PROFILES = listOf(
            newchar_loc_profile,
            *actsAndOtherMap.values.take(10).toTypedArray(),
        )
        RESERVED_NO_MODS_INDICATOR = no_mods_mod
        RESERVED_NO_DIFFICULTIES_INDICATOR = no_difficulties_difficulty

        if (dbDoesntExist) { //need to keep this check to support older versions
            createLocationsFromFile(
                file = File("./external/new_character_locations.csv"),
                profileDTO = newchar_loc_profile,
                modDTO = no_mods_mod,
                difficultyDTO = no_difficulties_difficulty,
            )?.let { error(it) }
        }

        if (version < 1) {
            createActAndOtherLocationsFromFile(
                map = actsAndOtherMap,
                mod = no_mods_mod,
                difficulty = no_difficulties_difficulty,
            )
        }

        if(version < 2) {
            newSuspendedTransaction {
                SchemaUtils.create(ProfileOrderTable)
                SchemaUtils.create(ModOrderTable)
                SchemaUtils.create(DifficultyOrderTable)
            }

            populateOrderTables()

            newSuspendedTransaction {
                val meta = Meta.wrapRow(MetaTable.selectAll().single())
                meta.version = 2
            }
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

            newSuspendedTransaction {
                mod.difficulties = SizedCollection(listOf(difficulty))
                profile1.mods = SizedCollection(listOf(mod))
            }
        } catch (e: Exception) {
            logger.error("Issue creating the reserved entities.")
            throw e
        }
    }

    private suspend fun createActAndOtherReservedEntities() {
        try {
            val profile2 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_1_LOCATIONS_NAME
                }
            }
            val profile3 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_2_LOCATIONS_NAME
                }
            }
            val profile4 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_3_LOCATIONS_NAME
                }
            }
            val profile5 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_4_LOCATIONS_NAME
                }
            }
            val profile6 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_5_LOCATIONS_NAME
                }
            }
            val profile7 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_6_LOCATIONS_NAME
                }
            }
            val profile8 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_ACT_7_LOCATIONS_NAME
                }
            }
            val profile9 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILES_MONSTER_TOTEM_LOCATIONS_NAME
                }
            }
            val profile10 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILES_NEMESIS_LOCATIONS_NAME
                }
            }
            val profile11 = newSuspendedTransaction {
                Profile.new {
                    name = RESERVED_PROFILE_OTHER_LOCATIONS_NAME
                }
            }

            newSuspendedTransaction {
                val mod = Mod.find { ModTable.name eq RESERVED_NO_MODS_INDICATOR_NAME }.single()
                profile2.mods = SizedCollection(listOf(mod))
                profile3.mods = SizedCollection(listOf(mod))
                profile4.mods = SizedCollection(listOf(mod))
                profile5.mods = SizedCollection(listOf(mod))
                profile6.mods = SizedCollection(listOf(mod))
                profile7.mods = SizedCollection(listOf(mod))
                profile8.mods = SizedCollection(listOf(mod))
                profile9.mods = SizedCollection(listOf(mod))
                profile10.mods = SizedCollection(listOf(mod))
                profile11.mods = SizedCollection(listOf(mod))
            }
        } catch (e: Exception) {
            logger.error("Issue creating the Act reserved entities.")
            throw e
        }
    }

    private suspend fun getReservedEntities(): Triple<ProfileDTO, ModDTO, DifficultyDTO> =
        try {
            newSuspendedTransaction {
                Triple(
                    Profile.find { ProfileTable.name eq RESERVED_PROFILE_GI_LOCATIONS_NAME }.single().toDTO(),
                    Mod.find { ModTable.name eq RESERVED_NO_MODS_INDICATOR_NAME }.single().toDTO(),
                    Difficulty.find { DifficultyTable.name eq RESERVED_NO_DIFFICULTIES_INDICATOR_NAME }.single().toDTO()
                )
            }
        } catch (e: Exception) {
            logger.error("Issue getting the reserved entities.")
            throw e
        }

    private suspend fun getActAndOtherReservedEntities(): Map<String, ProfileDTO> =
        try {
            newSuspendedTransaction {
                mapOf(
                    RESERVED_PROFILE_ACT_1_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_1_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_ACT_2_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_2_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_ACT_3_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_3_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_ACT_4_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_4_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_ACT_5_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_5_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_ACT_6_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_6_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_ACT_7_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_ACT_7_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILES_MONSTER_TOTEM_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILES_MONSTER_TOTEM_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILES_NEMESIS_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILES_NEMESIS_LOCATIONS_NAME }
                        .single().toDTO(),
                    RESERVED_PROFILE_OTHER_LOCATIONS_NAME to Profile.find { ProfileTable.name eq RESERVED_PROFILE_OTHER_LOCATIONS_NAME }
                        .single().toDTO(),
                )
            }
        } catch (e: Exception) {
            logger.error("Issue getting the reserved entities.")
            throw e
        }

    private suspend fun createActAndOtherLocationsFromFile(
        map: Map<String, ProfileDTO>,
        mod: ModDTO,
        difficulty: DifficultyDTO
    ) {
        map.forEach { (k, v) ->
            when (k) {
                RESERVED_PROFILES_MONSTER_TOTEM_LOCATIONS_NAME -> {
                    createLocationsFromFile(
                        file = File("./external/monster_totems.csv"),
                        profileDTO = v,
                        modDTO = mod,
                        difficultyDTO = difficulty,
                    )?.let { error(it) }
                }
                RESERVED_PROFILES_NEMESIS_LOCATIONS_NAME -> {
                    createLocationsFromFile(
                        file = File("./external/nemesis.csv"),
                        profileDTO = v,
                        modDTO = mod,
                        difficultyDTO = difficulty,
                    )?.let { error(it) }
                }
                RESERVED_PROFILE_OTHER_LOCATIONS_NAME -> {
                    createLocationsFromFile(
                        file = File("./external/other.csv"),
                        profileDTO = v,
                        modDTO = mod,
                        difficultyDTO = difficulty,
                    )?.let { error(it) }
                }
                else -> {
                    createLocationsFromFile(
                        file = File("./external/act_${map.keys.indexOf(k) + 1}.csv"),
                        profileDTO = v,
                        modDTO = mod,
                        difficultyDTO = difficulty,
                    )?.let { error(it) }
                }
            }
        }
    }

    private suspend fun deleteAdditionalLocations(): Unit = try {
        newSuspendedTransaction {
            Profile.find { ProfileTable.name eq "Additional Locations" }.firstOrNull()?.also { p ->
                LocationTable.deleteWhere { LocationTable.profile eq p.id }
                ProfileModIntermTable.deleteWhere { ProfileModIntermTable.profile eq p.id }
                p.delete()
            }
            Unit
        }
    } catch (e: Exception) {
        logger.error("Issue deleting additional locations.")
        throw e
    }

    private suspend fun populateOrderTables() {
        val profileOrders = newSuspendedTransaction {
            val profiles = Profile.wrapRows(ProfileTable.selectAll().orderBy(ProfileTable.id))
            profiles.mapIndexed { index, profile ->
                ProfileOrder.new {
                    this.profile = profile
                    this.order = index
                }
            }
        }

        profileOrders.forEach { profileOrder ->  
            val modOrders = newSuspendedTransaction {
                profileOrder.profile.mods.sortedBy { it.id.value }.mapIndexed { index, mod ->
                    ModOrder.new {
                        this.profileOrder = profileOrder
                        this.mod = mod
                        this.order = index
                    }
                }
            }

            modOrders.forEach { modOrder ->
                newSuspendedTransaction {
                    modOrder.mod.difficulties.sortedBy { it.id.value }.forEachIndexed { index, difficulty ->
                        DifficultyOrder.new {
                            this.modOrder = modOrder
                            this.difficulty = difficulty
                            this.order = index
                        }
                    }
                }
            }
        }
    }
}

