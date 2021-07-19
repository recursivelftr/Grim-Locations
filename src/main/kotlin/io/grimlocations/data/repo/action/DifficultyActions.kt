package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.Difficulty
import io.grimlocations.data.domain.DifficultyTable
import io.grimlocations.data.domain.Mod
import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateDifficultyAsync(name: String, modDTO: ModDTO): Deferred<DifficultyDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {
            var d = newSuspendedTransaction {
                Difficulty.find { DifficultyTable.name eq name }.singleOrNull()
            }

            if (d == null) {
                d = newSuspendedTransaction {
                    Difficulty.new {
                        this.name = name
                    }
                }
            }

            newSuspendedTransaction {
                val m = Mod.findById(modDTO.id)!!

                if (!d.mods.contains(m)) {
                    d.mods = SizedCollection(d.mods.toMutableSet().apply { add(m) })
                }
                d.toDTO()
            }

        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }