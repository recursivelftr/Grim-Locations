package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.ui.viewmodel.state.container.toPMContainer
import io.grimlocations.ui.viewmodel.state.container.toPMDContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateDifficultyAsync(
    name: String,
    pmContainer: PMContainer,
    skipOrderCreation: Boolean = false
): Deferred<DifficultyDTO?> =
    withContext(Dispatchers.IO) {
        async {
            try {
                var d = newSuspendedTransaction {
                    Difficulty.find { DifficultyTable.name eq name }.singleOrNull()
                }

                if (d == null) {
                    d = modifyDatabase {
                        Difficulty.new {
                            this.name = name
                        }
                    }
                }

                val pmdContainer = pmContainer.toPMDContainer(d.toDTO())

                if (!skipOrderCreation && isDifficultyDetachedFromModAsync(pmdContainer).await()) {
                    modifyDatabase {
                        val profileOrder =
                            ProfileOrder.find { ProfileOrderTable.profile eq pmdContainer.profile.id }.single()
                        val modOrder = ModOrder.find {
                            ModOrderTable.profileOrder eq profileOrder.id and
                                    (ModOrderTable.mod eq pmdContainer.mod.id)
                        }.single()
                        val highestDifficultyOrder = getHighestDifficultyOrderAsync(pmdContainer).await() ?: 0


                        DifficultyOrder.new {
                            this.modOrder = modOrder
                            this.difficulty = d
                            this.order = highestDifficultyOrder + 1
                        }
                    }
                }

                pmdContainer.difficulty
            } catch (e: Exception) {
                logger.error("", e)
                null
            }
        }
    }

suspend fun SqliteRepository.isDifficultyDetachedFromModAsync(pmdContainer: PMDContainer) =
    withContext(Dispatchers.IO) {
        async {
            newSuspendedTransaction {
                val profileOrder =
                    ProfileOrder.find { ProfileOrderTable.profile eq pmdContainer.profile.id }.single()
                val modOrder = ModOrder.find {
                    ModOrderTable.profileOrder eq profileOrder.id and
                            (ModOrderTable.mod eq pmdContainer.mod.id)
                }.single()

                DifficultyOrder.find {
                    (DifficultyOrderTable.modOrder eq modOrder.id) and
                            (DifficultyOrderTable.difficulty eq pmdContainer.difficulty.id)
                }.singleOrNull() == null
            }
        }
    }

suspend fun SqliteRepository.modifyOrCreateDifficultyAsync(
    name: String,
    pmdContainer: PMDContainer
): Deferred<DifficultyDTO?> =
    withContext(Dispatchers.IO) {
        async {
            try {

                val difficultyOrder = newSuspendedTransaction {
                    DifficultyOrder.find { DifficultyOrderTable.difficulty eq pmdContainer.difficulty.id }.single()
                }

                val d =
                    findOrCreateDifficultyAsync(name, pmdContainer.toPMContainer(), skipOrderCreation = true).await()!!

                modifyDatabase {
                    val difficulty = Difficulty.find { DifficultyTable.id eq d.id }.single()
                    difficultyOrder.difficulty = difficulty

                    Location.find {
                        (LocationTable.profile eq pmdContainer.profile.id) and
                                (LocationTable.mod eq pmdContainer.mod.id) and
                                (LocationTable.difficulty eq pmdContainer.difficulty.id)
                    }.forEach {
                        it.difficulty = difficulty
                    }
                }
                d
            } catch (e: Exception) {
                logger.error("", e)
                null
            }
        }
    }


suspend fun SqliteRepository.getHighestDifficultyOrderAsync(pmdContainer: PMDContainer) =
    withContext(Dispatchers.IO) {
        async {
            newSuspendedTransaction {
                val profileOrder =
                    ProfileOrder.find { ProfileOrderTable.profile eq pmdContainer.profile.id }.single()
                val modOrder = ModOrder.find {
                    ModOrderTable.profileOrder eq profileOrder.id and
                            (ModOrderTable.mod eq pmdContainer.mod.id)
                }.single()

                DifficultyOrderTable.slice(DifficultyOrderTable.order).select {
                    (DifficultyOrderTable.modOrder eq modOrder.id) and
                            (DifficultyOrderTable.difficulty eq pmdContainer.difficulty.id)
                }.map { it[DifficultyOrderTable.order] }.maxOrNull()
            }
        }
    }

suspend fun SqliteRepository.decrementDifficultiesOrder(difficulties: Set<DifficultyDTO>, pmContainer: PMContainer) =
    withContext(Dispatchers.IO) {
        val modOrder = newSuspendedTransaction {
            val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq pmContainer.profile.id }.single()

            ModOrder.find {
                ModOrderTable.profileOrder eq profileOrder.id and
                        (ModOrderTable.mod eq pmContainer.mod.id)
            }.single()
        }

        val difficultyOrders = newSuspendedTransaction {
            DifficultyOrder.find {
                DifficultyOrderTable.difficulty inList difficulties.map { it.id } and
                        (DifficultyOrderTable.modOrder eq modOrder.id)
            }
        }

        val dio = modifyDatabase {
            DifficultyOrder.find { DifficultyOrderTable.order eq difficultyOrders.first().order - 1 and (DifficultyOrderTable.modOrder eq modOrder.id) }
                .single().apply {
                this.order = -1
            }
        }

        val lastOrder = newSuspendedTransaction { difficultyOrders.last().order }

        modifyDatabase {
            difficultyOrders.forEach {
                it.order = it.order - 1
            }
        }

        modifyDatabase {
            dio.order = lastOrder
        }
    }

suspend fun SqliteRepository.incrementDifficultiesOrder(difficulties: Set<DifficultyDTO>, pmContainer: PMContainer) =
    withContext(Dispatchers.IO) {
        val modOrder = newSuspendedTransaction {
            val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq pmContainer.profile.id }.single()

            ModOrder.find {
                ModOrderTable.profileOrder eq profileOrder.id and
                        (ModOrderTable.mod eq pmContainer.mod.id)
            }.single()
        }

        val difficultyOrders = newSuspendedTransaction {
            DifficultyOrder.find {
                DifficultyOrderTable.difficulty inList difficulties.map { it.id } and
                        (DifficultyOrderTable.modOrder eq modOrder.id)
            }
        }

        val dio = modifyDatabase {
            DifficultyOrder.find { DifficultyOrderTable.order eq difficultyOrders.last().order + 1 and (DifficultyOrderTable.modOrder eq modOrder.id) }
                .single().apply {
                    this.order = -1
                }
        }

        val firstOrder = newSuspendedTransaction { difficultyOrders.first().order }

        modifyDatabase {
            difficultyOrders.forEach {
                it.order = it.order + 1
            }
        }

        modifyDatabase {
            dio.order = firstOrder
        }
    }

suspend fun SqliteRepository.deleteDifficulties(difficulties: Set<DifficultyDTO>, pmContainer: PMContainer) =
    withContext(Dispatchers.IO) {
        modifyDatabase {
            val profileOrder =
                ProfileOrder.find { ProfileOrderTable.profile eq pmContainer.profile.id }.single()
            val modOrder = ModOrder.find {
                ModOrderTable.profileOrder eq profileOrder.id and
                        (ModOrderTable.mod eq pmContainer.mod.id)
            }.single()

            val meta = Meta.wrapRow(MetaTable.selectAll().single())

            difficulties.forEach {
                val difficultyOrder = DifficultyOrder.find {
                    (DifficultyOrderTable.modOrder eq modOrder.id) and
                            (DifficultyOrderTable.difficulty eq it.id)
                }.single()

                if (profileOrder.profile == meta.activeProfile
                    && modOrder.mod == meta.activeMod
                    && difficultyOrder.difficulty == meta.activeDifficulty
                ) {
                    meta.activeProfile = null
                    meta.activeMod = null
                    meta.activeDifficulty = null
                }

                Location.find {
                    LocationTable.profile eq profileOrder.profile.id and
                            (LocationTable.mod eq modOrder.mod.id) and
                            (LocationTable.difficulty eq difficultyOrder.difficulty.id)
                }.forEach {
                    it.delete()
                }

                difficultyOrder.delete()
            }
        }
    }