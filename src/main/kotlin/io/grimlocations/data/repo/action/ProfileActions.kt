package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.Profile
import io.grimlocations.data.domain.ProfileTable
import io.grimlocations.data.dto.*
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.framework.data.dto.containsId
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.getProfilesAsync(): Deferred<List<ProfileDTO>> =
    suspendedTransactionAsync(Dispatchers.IO) {
        Profile.wrapRows(ProfileTable.selectAll()).map { it.toDTO() }
    }

suspend fun SqliteRepository.findOrCreateProfileAsync(name: String): Deferred<ProfileDTO?> =
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
            }
            p.toDTO()
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }

suspend fun SqliteRepository.getProfilesModsDifficultiesAsync(
    includeReservedProfiles: Boolean = true
): Deferred<ProfileModDifficultyMap> =
    suspendedTransactionAsync(Dispatchers.IO) {
        val map: MutableProfileModDifficultyMap = mutableMapOf()
        val reservedMap: MutableProfileModDifficultyMap = mutableMapOf()
        val regularMap: MutableProfileModDifficultyMap = mutableMapOf()

        ProfileTable.selectAll().forEach {
            val p = Profile.wrapRow(it)
            if (includeReservedProfiles && RESERVED_PROFILES.containsId(p.id.value)) {
                val mmap: MutableModDifficultyMap = mutableMapOf()

                p.mods.forEach { m ->
                    mmap[m.toDTO()] = m.difficulties.map { d -> d.toDTO() } as MutableList<DifficultyDTO>
                }
                reservedMap[p.toDTO()] = mmap
            } else if (!RESERVED_PROFILES.containsId(p.id.value)) {
                val mmap: MutableModDifficultyMap = mutableMapOf()

                p.mods.forEach { m ->
                    mmap[m.toDTO()] = m.difficulties.map { d -> d.toDTO() } as MutableList<DifficultyDTO>
                }
                regularMap[p.toDTO()] = mmap
            }
        }
        map.putAll(reservedMap)
        map.putAll(regularMap)
        map
    }

