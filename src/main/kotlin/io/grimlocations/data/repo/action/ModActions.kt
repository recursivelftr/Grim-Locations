package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.Mod
import io.grimlocations.data.domain.ModTable
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateModAsync(name: String): Deferred<ModDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {
            var p = newSuspendedTransaction {
                Mod.find { ModTable.name eq name }.singleOrNull()
            }
            if (p == null) {
                p = newSuspendedTransaction {
                    Mod.new {
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