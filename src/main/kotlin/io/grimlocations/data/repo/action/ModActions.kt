package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.Mod
import io.grimlocations.data.domain.ModTable
import io.grimlocations.data.domain.Profile
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateModAsync(name: String, profileDTO: ProfileDTO): Deferred<ModDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {
            var m = newSuspendedTransaction {
                Mod.find { ModTable.name eq name }.singleOrNull()
            }

            if (m == null) {
                m = newSuspendedTransaction {
                    Mod.new {
                        this.name = name
                    }
                }
            }

            newSuspendedTransaction {
                val p = Profile.findById(profileDTO.id)!!

                if (!m.profiles.contains(p)) {
                    m.profiles = SizedCollection(m.profiles.toMutableSet().apply { add(p) })
                }
                m.toDTO()
            }

        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }