package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.*
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.framework.data.dto.containsId
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.select
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

        if(includeReservedProfiles) {
            rows.map { it.profile.toDTO() }.toSet()
        } else {
            rows.filter { !RESERVED_PROFILES.containsId(it.id.value) }.map { it.profile.toDTO() }.toSet()
        }
    }

suspend fun SqliteRepository.findOrCreateProfileAsync(name: String, skipOrderCreation: Boolean = false): Deferred<ProfileDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {
            var p = newSuspendedTransaction {
                Profile.find { ProfileTable.name eq name }.singleOrNull()
            }
            if (p == null) {
                p = newSuspendedTransaction {
                    Profile.new {
                        this.name = name
                    }
                }
                if(skipOrderCreation) {
                    val highestProfileOrder = getHighestProfileOrderAsync(p.toDTO()).await() ?: 0
                    newSuspendedTransaction {
                        ProfileOrder.new {
                            this.profile = p
                            this.order = highestProfileOrder + 1
                        }
                    }
                }
            }
            p.toDTO()
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }

suspend fun SqliteRepository.modifyOrCreateProfileAsync(name: String, profileDTO: ProfileDTO): Deferred<ProfileDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {
            val profileOrder = newSuspendedTransaction {
                ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id}
            }.single()

            val p = findOrCreateProfileAsync(name, skipOrderCreation = true).await()!!

            newSuspendedTransaction {
                val profile = Profile.find { ProfileTable.id eq p.id}.single()
                profileOrder.profile = profile
            }
            p
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }

suspend fun SqliteRepository.getHighestProfileOrderAsync(profile: ProfileDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        ProfileOrderTable.slice(ProfileOrderTable.order).select {
            (ProfileOrderTable.profile eq profile.id)
        }.map { it[ProfileOrderTable.order] }.maxOrNull()
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
                val mmap: MutableModDifficultyMap = mutableMapOf()

                profileOrder.modOrders.sortedBy { it.order }.forEach { m ->
                    mmap[m.mod.toDTO()] = m.difficultyOrders.sortedBy { it.order }.map { d -> d.difficulty.toDTO() } as MutableList<DifficultyDTO>
                }
                map[profileOrder.profile.toDTO()] = mmap
            }
        }

        map
    }

