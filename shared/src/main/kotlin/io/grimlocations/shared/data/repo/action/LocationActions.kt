package io.grimlocations.shared.data.repo.action

import io.grimlocations.shared.data.domain.Location
import io.grimlocations.shared.data.domain.LocationTable
import io.grimlocations.shared.data.dto.DifficultyDTO
import io.grimlocations.shared.data.dto.ModDTO
import io.grimlocations.shared.data.dto.ProfileDTO
import io.grimlocations.shared.data.repo.SqliteRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

suspend fun SqliteRepository.getLocationsAsync(profile: ProfileDTO, mod: ModDTO, difficulty: DifficultyDTO) =
    suspendedTransactionAsync(Dispatchers.IO) {
        Location.wrapRows(
            LocationTable.select {
                LocationTable.profile eq profile.id and
                        (LocationTable.mod eq mod.id) and
                        (LocationTable.difficulty eq difficulty.id)
            }
        ).map { it.toDTO() }
    }