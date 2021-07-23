package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.ui.viewmodel.state.container.toPMContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateDifficultyAsync(name: String, pmContainer: PMContainer, skipOrderCreation: Boolean = false): Deferred<DifficultyDTO?> =
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

                if(skipOrderCreation) {
                    val profileOrder =
                        ProfileOrder.find { ProfileOrderTable.profile eq pmContainer.profile.id }.single()
                    val modOrder = ModOrder.find {
                        ModOrderTable.profileOrder eq profileOrder.id and
                                (ModOrderTable.mod eq pmContainer.mod.id)
                    }.single()
                    val highestDifficultyOrder = getHighestDifficultyOrderAsync(d.toDTO()).await() ?: 0

                    newSuspendedTransaction {
                        DifficultyOrder.new {
                            this.modOrder = modOrder
                            this.difficulty = d
                            this.order = highestDifficultyOrder + 1
                        }
                    }
                }
            }
            d.toDTO()
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }

suspend fun SqliteRepository.modifyOrCreateDifficultyAsync(name: String, pmdContainer: PMDContainer): Deferred<DifficultyDTO?> =
    suspendedTransactionAsync(Dispatchers.IO) {
        try {

            val difficultyOrder = newSuspendedTransaction {
                DifficultyOrder.find { DifficultyOrderTable.difficulty eq pmdContainer.difficulty.id}
            }.single()

            val d = findOrCreateDifficultyAsync(name, pmdContainer.toPMContainer(), skipOrderCreation = true).await()!!

            newSuspendedTransaction {
                val difficulty = Difficulty.find { DifficultyTable.id eq d.id}.single()
                difficultyOrder.difficulty = difficulty
            }
            d
        } catch (e: Exception) {
            logger.error("", e)
            null
        }
    }


suspend fun SqliteRepository.getHighestDifficultyOrderAsync(difficulty : DifficultyDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        DifficultyOrderTable.slice(DifficultyOrderTable.order).select {
            (DifficultyOrderTable.difficulty eq difficulty.id)
        }.map { it[DifficultyOrderTable.order] }.maxOrNull()
    }