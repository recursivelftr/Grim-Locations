package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateModAsync(name: String, profileDTO: ProfileDTO, skipOrderCreation: Boolean = false): Deferred<ModDTO?> =
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

                if(skipOrderCreation) {
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

            }

            m.toDTO()
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }

suspend fun SqliteRepository.modifyOrCreateModAsync(name: String, pmContainer: PMContainer): Deferred<ModDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {

            val modOrder = newSuspendedTransaction {
                ModOrder.find { ModOrderTable.mod eq pmContainer.mod.id}
            }.single()

            val m = findOrCreateModAsync(name, pmContainer.profile, skipOrderCreation = true).await()!!

            newSuspendedTransaction {
                val mod = Mod.find { ModTable.id eq m.id}.single()
                modOrder.mod = mod
            }
            m
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