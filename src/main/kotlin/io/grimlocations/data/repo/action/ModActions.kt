package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger = LogManager.getLogger()

suspend fun SqliteRepository.findOrCreateModAsync(
    name: String,
    profileDTO: ProfileDTO,
    skipOrderCreation: Boolean = false
): Deferred<ModDTO?> =
    withContext(Dispatchers.IO) {
        async {
            try {
                var m = newSuspendedTransaction {
                    Mod.find { ModTable.name eq name }.singleOrNull()
                }

                if (m == null) {
                    m = modifyDatabase {
                        Mod.new {
                            this.name = name
                        }
                    }
                }

                if (!skipOrderCreation && isModDetachedFromProfileAsync(m.toDTO(), profileDTO).await()) {
                    modifyDatabase {
                        val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id }.single()
                        val highestModOrder = getHighestModOrderAsync(profileDTO).await() ?: 0

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
    }

suspend fun SqliteRepository.isModDetachedFromProfileAsync(modDTO: ModDTO, profileDTO: ProfileDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id }.single()
        ModOrder.find {
            ModOrderTable.profileOrder eq profileOrder.id and
                    (ModOrderTable.mod eq modDTO.id)

        }.singleOrNull() == null
    }

suspend fun SqliteRepository.modifyOrCreateModAsync(name: String, pmContainer: PMContainer): Deferred<ModDTO?> =
    withContext(Dispatchers.IO) {
        async {
            try {
                val modOrder = newSuspendedTransaction {
                    val profileOrder =
                        ProfileOrder.find { ProfileOrderTable.profile eq pmContainer.profile.id }.single()
                    ModOrder.find {
                        ModOrderTable.mod eq pmContainer.mod.id and
                                (ModOrderTable.profileOrder eq profileOrder.id)
                    }.single()
                }

                val m = findOrCreateModAsync(name, pmContainer.profile, skipOrderCreation = true).await()!!

                modifyDatabase {
                    val mod = Mod.find { ModTable.id eq m.id }.single()
                    modOrder.mod = mod

                    Location.find {
                        (LocationTable.profile eq pmContainer.profile.id) and
                                (LocationTable.mod eq pmContainer.mod.id)
                    }.forEach {
                        it.mod = mod
                    }
                }
                m
            } catch (e: Exception) {
                logger.error("", e)
                null
            }
        }
    }

suspend fun SqliteRepository.getHighestModOrderAsync(profileDTO: ProfileDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq profileDTO.id }.single()

        ModOrderTable.slice(ModOrderTable.order).select {
            (ModOrderTable.profileOrder eq profileOrder.id)
        }.map { it[ModOrderTable.order] }.maxOrNull()
    }

suspend fun SqliteRepository.decrementModsOrder(mods: Set<ModDTO>, profile: ProfileDTO) = withContext(Dispatchers.IO) {
    if (mods.isNotEmpty()) {
        val profileOrder = newSuspendedTransaction {
            ProfileOrder.find { ProfileOrderTable.profile eq profile.id }.single()
        }

        val modOrders = newSuspendedTransaction {
            ModOrder.find { ModOrderTable.mod inList mods.map { it.id } and (ModOrderTable.profileOrder eq profileOrder.id) }
        }

        val mo = modifyDatabase {
            ModOrder.find { ModOrderTable.profileOrder eq profileOrder.id and (ModOrderTable.order eq modOrders.first().order - 1) }
                .single().apply {
                    this.order = -1
                }
        }

        val lastOrder = newSuspendedTransaction { modOrders.last().order }

        modifyDatabase {
            modOrders.forEach {
                it.order = it.order - 1
            }
        }

        modifyDatabase {
            mo.order = lastOrder
        }
    }
}

suspend fun SqliteRepository.incrementModsOrder(mods: Set<ModDTO>, profile: ProfileDTO) = withContext(Dispatchers.IO) {
    if (mods.isNotEmpty()) {
        val profileOrder = newSuspendedTransaction {
            ProfileOrder.find { ProfileOrderTable.profile eq profile.id }.single()
        }

        val modOrders = newSuspendedTransaction {
            ModOrder.find { ModOrderTable.mod inList mods.map { it.id } and (ModOrderTable.profileOrder eq profileOrder.id) }
        }

        val mo = modifyDatabase {
            ModOrder.find { ModOrderTable.profileOrder eq profileOrder.id and (ModOrderTable.order eq modOrders.last().order + 1) }
                .single().apply {
                    this.order = -1
                }
        }

        val firstOrder = newSuspendedTransaction { modOrders.first().order }

        modifyDatabase {
            modOrders.forEach {
                it.order = it.order + 1
            }
        }

        modifyDatabase {
            mo.order = firstOrder
        }
    }
}

suspend fun SqliteRepository.deleteMods(mods: Set<ModDTO>, profile: ProfileDTO) = withContext(Dispatchers.IO) {
    modifyDatabase {
        val profileOrder = ProfileOrder.find { ProfileOrderTable.profile eq profile.id }.single()

        mods.forEach {
            val modOrder = ModOrder.find {
                ModOrderTable.profileOrder eq profileOrder.id and
                        (ModOrderTable.mod eq it.id)

            }.single()

            Location.find {
                LocationTable.profile eq profileOrder.profile.id and
                        (LocationTable.mod eq modOrder.mod.id)
            }.forEach {
                it.delete()
            }

            modOrder.difficultyOrders.forEach { d ->
                d.delete()
            }
            modOrder.delete()
        }
    }
}