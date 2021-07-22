package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
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

                val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id }.single()
                val highestModOrder = getHighestModOrderAsync(m.toDTO()).await() ?: 0

                newSuspendedTransaction {
                    ModOrder.new {
                        this.profileOrder = profileOrder
                        this.mod = m
                        this.order = highestModOrder + 1
                    }
                }

            }

            m.toDTO()
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }

suspend fun SqliteRepository.getHighestModOrderAsync(mod: ModDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        ModOrderTable.slice(ModOrderTable.order).select {
            (ModOrderTable.mod eq mod.id)
        }.map { it[ModOrderTable.order] }.maxOrNull()
    }