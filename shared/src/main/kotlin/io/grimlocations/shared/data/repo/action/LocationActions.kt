package io.grimlocations.shared.data.repo.action

import io.grimlocations.shared.data.domain.*
import io.grimlocations.shared.data.dto.DifficultyDTO
import io.grimlocations.shared.data.dto.LocationDTO
import io.grimlocations.shared.data.dto.ModDTO
import io.grimlocations.shared.data.dto.ProfileDTO
import io.grimlocations.shared.data.repo.SqliteRepository
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

private val logger: Logger = LogManager.getLogger()

suspend fun SqliteRepository.getLocationsAsync(profile: ProfileDTO, mod: ModDTO, difficulty: DifficultyDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        Location.wrapRows(
            LocationTable.select {
                LocationTable.profile eq profile.id and
                        (LocationTable.mod eq mod.id) and
                        (LocationTable.difficulty eq difficulty.id)
            }.orderBy(LocationTable.order)
        ).map { it.toDTO() }.toSet()
    }

suspend fun SqliteRepository.getLocationsAsync(container: PMDContainer) =
    getLocationsAsync(container.profile, container.mod, container.difficulty)

suspend fun SqliteRepository.copyLocationsToPMD(
    pmdContainer: PMDContainer,
    selectedLocations: Set<LocationDTO>,
    otherSelectedLocations: Set<LocationDTO>,
): Deferred<String?> = withContext(Dispatchers.IO) {
    async {
        try {
            if (selectedLocations.isNotEmpty()) {
                var o: Int
                if (otherSelectedLocations.isEmpty()) {
                    o = getHighestOrder(pmdContainer).await() ?: 0
                } else {
                    o = otherSelectedLocations.last().order

                    val l = newSuspendedTransaction {
                        Location.find {
                            (LocationTable.profile eq pmdContainer.profile.id) and
                                    (LocationTable.mod eq pmdContainer.mod.id) and
                                    (LocationTable.difficulty eq pmdContainer.difficulty.id) and
                                    (LocationTable.order greater o)
                        }
                    }

                    if (!l.empty()) {
                        modifyDatabaseAsync {
                            l.forEach {
                                it.order = it.order + selectedLocations.size
                            }
                        }.await()
                    }
                }

                modifyDatabaseAsync {
                    val p = Profile.findById(pmdContainer.profile.id)!!
                    val m = Mod.findById(pmdContainer.mod.id)!!
                    val d = Difficulty.findById(pmdContainer.difficulty.id)!!

                    selectedLocations.forEach {
                        val c = Coordinate.findById(it.coordinate.id)!!
                        Location.new {
                            name = it.name
                            profile = p
                            mod = m
                            difficulty = d
                            coordinate = c
                            order = ++o
                        }
                    }
                    null
                }.await()
            } else {
                null
            }
        } catch (e: Exception) {
            val msg = "Could not copy locations."
            logger.error(msg, e)
            msg
        }
    }
}

suspend fun SqliteRepository.getHighestOrder(pmdContainer: PMDContainer) =
    getHighestOrder(pmdContainer.profile, pmdContainer.mod, pmdContainer.difficulty)

suspend fun SqliteRepository.getHighestOrder(p: ProfileDTO, m: ModDTO, d: DifficultyDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        LocationTable.slice(LocationTable.order).select {
            (LocationTable.profile eq p.id) and
                    (LocationTable.mod eq m.id) and
                    (LocationTable.difficulty eq d.id)
        }.map { it[LocationTable.order] }.maxOrNull()
    }