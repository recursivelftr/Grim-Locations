package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
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
                    o = getHighestOrderAsync(pmdContainer).await() ?: 0
                } else {
                    o = otherSelectedLocations.maxOf { it.order }

                    val l = getLocationsAboveOrderAsync(pmdContainer, o).await()

                    if (l.isNotEmpty()) {
                        modifyDatabaseAsync {
                            l.reversed().forEach {
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
                logger.info("No selected locations.")
                null
            }
        } catch (e: Exception) {
            val msg = "Could not copy locations."
            logger.error(msg, e)
            msg
        }
    }
}

suspend fun SqliteRepository.getHighestOrderAsync(pmdContainer: PMDContainer) =
    getHighestOrderAsync(pmdContainer.profile, pmdContainer.mod, pmdContainer.difficulty)

suspend fun SqliteRepository.getHighestOrderAsync(p: ProfileDTO, m: ModDTO, d: DifficultyDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        LocationTable.slice(LocationTable.order).select {
            (LocationTable.profile eq p.id) and
                    (LocationTable.mod eq m.id) and
                    (LocationTable.difficulty eq d.id)
        }.map { it[LocationTable.order] }.maxOrNull()
    }

suspend fun SqliteRepository.incrementLocationsOrderAsync(
    pmdContainer: PMDContainer,
    locations: Set<LocationDTO>
): Deferred<Unit> = withContext(Dispatchers.IO) {
    async {
        try {
            if (locations.isNotEmpty()) {
                val highestOrder = getHighestOrderAsync(pmdContainer).await()!!
                val locs = locations.sortedBy { it.order }

                if (locs.last().order != highestOrder) {
                    val loc = modifyDatabaseAsync {
                        Location.find {
                            (LocationTable.profile eq pmdContainer.profile.id) and
                                    (LocationTable.mod eq pmdContainer.mod.id) and
                                    (LocationTable.difficulty eq pmdContainer.difficulty.id) and
                                    (LocationTable.order eq (locs.last().order + 1))
                        }.single().apply {
                            order = -1
                        }
                    }.await()

                    modifyDatabaseAsync {
                        locs.reversed().forEach {
                            val l = Location.findById(it.id)!!
                            l.order = l.order + 1
                        }
                    }.await()

                    modifyDatabaseAsync {
                        loc.order = locs.first().order
                    }.await()
                }

            }
        } catch (e: Exception) {
            logger.error("Could not increment locations' order.", e)
        }
    }
}

suspend fun SqliteRepository.decrementLocationsOrderAsync(
    pmdContainer: PMDContainer,
    locations: Set<LocationDTO>
): Deferred<Unit> = withContext(Dispatchers.IO) {
    async {
        try {
            if (locations.isNotEmpty()) {
                val locs = locations.sortedBy { it.order }

                if (locs.first().order != 1) {
                    val loc = modifyDatabaseAsync {
                        Location.find {
                            (LocationTable.profile eq pmdContainer.profile.id) and
                                    (LocationTable.mod eq pmdContainer.mod.id) and
                                    (LocationTable.difficulty eq pmdContainer.difficulty.id) and
                                    (LocationTable.order eq (locs.first().order - 1))
                        }.single().apply {
                            order = -1
                        }
                    }.await()

                    modifyDatabaseAsync {
                        locs.forEach {
                            val l = Location.findById(it.id)!!
                            l.order = l.order - 1
                        }
                    }.await()

                    modifyDatabaseAsync {
                        loc.order = locs.last().order
                    }.await()
                }
            }
        } catch (e: Exception) {
            logger.error("Could not decrement locations' order.", e)
        }
    }
}

suspend fun SqliteRepository.deleteLocationsAsync(pmdContainer: PMDContainer, locations: Set<LocationDTO>) =
    modifyDatabaseAsync {
        try {
            locations.forEach {
                Location.findById(it.id)!!.delete()
            }

            Location.wrapRows(
                LocationTable.select {
                    (LocationTable.profile eq pmdContainer.profile.id) and
                            (LocationTable.mod eq pmdContainer.mod.id) and
                            (LocationTable.difficulty eq pmdContainer.difficulty.id)
                }.orderBy(LocationTable.order)
            ).forEachIndexed { i, loc ->
                loc.order = i + 1
            }
        } catch (e: Exception) {
            logger.error("Could not delete locations", e)
        }
    }

suspend fun SqliteRepository.updateLocationAsync(location: LocationDTO) =
    modifyDatabaseAsync {
        try {
            val loc = Location.findById(location.id)!!
            loc.name = location.name
            null
        } catch (e: Exception) {
            val message = "Could not update location: $location"
            logger.error(message, e)
            message
        }
    }

private suspend fun SqliteRepository.getLocationsAboveOrderAsync(pmdContainer: PMDContainer, o: Int) =
    suspendedTransactionAsync(Dispatchers.IO) {
        Location.find {
            (LocationTable.profile eq pmdContainer.profile.id) and
                    (LocationTable.mod eq pmdContainer.mod.id) and
                    (LocationTable.difficulty eq pmdContainer.difficulty.id) and
                    (LocationTable.order greater o)
        }.toSet()
    }

private suspend fun SqliteRepository.getLocationsBelowOrderAsync(pmdContainer: PMDContainer, o: Int) =
    suspendedTransactionAsync(Dispatchers.IO) {
        Location.find {
            (LocationTable.profile eq pmdContainer.profile.id) and
                    (LocationTable.mod eq pmdContainer.mod.id) and
                    (LocationTable.difficulty eq pmdContainer.difficulty.id) and
                    (LocationTable.order less o)
        }.toSet()
    }