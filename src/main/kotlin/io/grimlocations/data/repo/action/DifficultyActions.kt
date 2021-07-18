package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.Difficulty
import io.grimlocations.data.domain.DifficultyTable
import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateDifficultyAsync(name: String): Deferred<DifficultyDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {
            var p = newSuspendedTransaction {
                Difficulty.find { DifficultyTable.name eq name }.singleOrNull()
            }
            if (p == null) {
                p = newSuspendedTransaction {
                    Difficulty.new {
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