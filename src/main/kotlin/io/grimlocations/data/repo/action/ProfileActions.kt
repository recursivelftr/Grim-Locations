package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.*
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.framework.data.dto.containsId
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.getProfilesAsync(includeReservedProfiles: Boolean = true): Deferred<Set<ProfileDTO>> =
    suspendedTransactionAsync(Dispatchers.IO) {
        val rows = ProfileOrder.wrapRows(
            ProfileOrderTable.selectAll()
                .orderBy(ProfileOrderTable.order)
        )

        if (includeReservedProfiles) {
            rows.map { it.profile.toDTO() }.toSet()
        } else {
            rows.filter { !RESERVED_PROFILES.containsId(it.id.value) }.map { it.profile.toDTO() }.toSet()
        }
    }

suspend fun SqliteRepository.findOrCreateProfileAsync(
    name: String,
    skipOrderCreation: Boolean = false
): Deferred<ProfileDTO?> =
    withContext(Dispatchers.IO) {
        async {
            try {
                var p = newSuspendedTransaction {
                    Profile.find { ProfileTable.name eq name }.singleOrNull()
                }
                if (p == null) {
                    p = modifyDatabase {
                        Profile.new {
                            this.name = name
                        }
                    }
                }

                if (!skipOrderCreation && isProfileDetachedAsync(p.toDTO()).await()) {
                    val highestProfileOrder = getHighestProfileOrderAsync().await() ?: 0
                    modifyDatabase {
                        ProfileOrder.new {
                            this.profile = p
                            this.order = highestProfileOrder + 1
                        }
                    }
                }
                p.toDTO()
            } catch (e: Exception) {
                logger.error("", e)
                null
            }
        }
    }

suspend fun SqliteRepository.isProfileDetachedAsync(profileDTO: ProfileDTO): Deferred<Boolean> =
    suspendedTransactionAsync(Dispatchers.IO) {
        ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id }.singleOrNull() == null
    }

suspend fun SqliteRepository.modifyOrCreateProfileAsync(name: String, profileDTO: ProfileDTO): Deferred<ProfileDTO?> =
    withContext(Dispatchers.IO) {
        async {
            try {
                val profileOrder = newSuspendedTransaction {
                    ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id }.single()
                }

                val p = findOrCreateProfileAsync(name, skipOrderCreation = true).await()!!

                modifyDatabase {
                    val profile = Profile.find { ProfileTable.id eq p.id }.single()
                    profileOrder.profile = profile

                    Location.find { LocationTable.profile eq profileDTO.id }.forEach {
                        it.profile = profile
                    }
                }
                p
            } catch (e: Exception) {
                logger.error("", e)
                null
            }
        }
    }

suspend fun SqliteRepository.getHighestProfileOrderAsync() =
    suspendedTransactionAsync(Dispatchers.IO) {
        ProfileOrderTable.slice(ProfileOrderTable.order).selectAll()
            .map { it[ProfileOrderTable.order] }.maxOrNull()
    }

suspend fun SqliteRepository.getProfilesModsDifficultiesAsync(
    includeReservedProfiles: Boolean = true
): Deferred<ProfileModDifficultyMap> =
    suspendedTransactionAsync(Dispatchers.IO) {
        val map: MutableProfileModDifficultyMap = mutableMapOf()

        ProfileOrder.wrapRows(
            ProfileOrderTable.selectAll()
                .orderBy(ProfileOrderTable.order)
        ).forEach { profileOrder ->

            val profile = profileOrder.profile

            if (includeReservedProfiles || !RESERVED_PROFILES.containsId(profile.id.value)) {
                val mmap: MutableModDifficultyMap

                if(profileOrder.modOrders.empty()) {
                    mmap = NO_MODS_OR_DIFFICULTIES_MAP as MutableModDifficultyMap
                } else {
                    mmap = mutableMapOf()
                    profileOrder.modOrders.sortedBy { it.order }.forEach { m ->
                        if(m.difficultyOrders.empty()) {
                            mmap[m.mod.toDTO()] = NO_DIFFICULTIES_LIST as MutableList<DifficultyDTO>
                        } else {
                            mmap[m.mod.toDTO()] = m.difficultyOrders.sortedBy { it.order }
                                .map { d -> d.difficulty.toDTO() } as MutableList<DifficultyDTO>
                        }
                    }
                }


                map[profileOrder.profile.toDTO()] = mmap
            }
        }

        map
    }

suspend fun SqliteRepository.decrementProfilesOrder(profiles: Set<ProfileDTO>) = withContext(Dispatchers.IO) {
    if (profiles.isNotEmpty()) {
        val profileOrders = newSuspendedTransaction {
            ProfileOrder.find { ProfileOrderTable.profile inList profiles.map { it.id } }.sortedBy { it.order }
        }

        val po = modifyDatabase {
            ProfileOrder.find { ProfileOrderTable.order eq profileOrders.first().order - 1 }.single().apply {
                this.order = -1
            }
        }

        val lastOrder = newSuspendedTransaction { profileOrders.last().order }

        modifyDatabase {
            profileOrders.forEach {
                it.order = it.order - 1
            }
        }

        modifyDatabase {
            po.order = lastOrder
        }
    }
}

suspend fun SqliteRepository.incrementProfilesOrder(profiles: Set<ProfileDTO>) = withContext(Dispatchers.IO) {
    if (profiles.isNotEmpty()) {
        val profileOrders = newSuspendedTransaction {
            ProfileOrder.find { ProfileOrderTable.profile inList profiles.map { it.id } }.sortedBy { it.order }
        }

        val po = modifyDatabase {
            ProfileOrder.find { ProfileOrderTable.order eq profileOrders.last().order + 1 }.single().apply {
                this.order = -1
            }
        }

        val firstOrder = newSuspendedTransaction { profileOrders.first().order }

        modifyDatabase {
            profileOrders.forEach {
                it.order = it.order + 1
            }
        }

        modifyDatabase {
            po.order = firstOrder
        }

    }
}

suspend fun SqliteRepository.deleteProfiles(profiles: Set<ProfileDTO>) = withContext(Dispatchers.IO) {
    modifyDatabase {
        try {
            val profileOrders = ProfileOrder.find { ProfileOrderTable.profile inList profiles.map { it.id } }

            profileOrders.forEach { p ->
                Location.find {
                    LocationTable.profile eq p.profile.id
                }.forEach {
                    it.delete()
                }

                p.modOrders.forEach { m ->
                    m.difficultyOrders.forEach { d ->
                        d.delete()
                    }
                    m.delete()
                }
                p.delete()
            }
        } catch (e: Exception) {
            logger.error("", e)
        }
    }
}
