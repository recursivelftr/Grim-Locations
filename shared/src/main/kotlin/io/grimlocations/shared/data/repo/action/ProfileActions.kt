package io.grimlocations.shared.data.repo.action

import io.grimlocations.shared.data.domain.Profile
import io.grimlocations.shared.data.domain.ProfileTable
import io.grimlocations.shared.data.dto.ProfileDTO
import io.grimlocations.shared.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

suspend fun SqliteRepository.loadProfilesAsync(): Deferred<List<ProfileDTO>> = suspendedTransactionAsync(Dispatchers.IO) {
    Profile.wrapRows(ProfileTable.selectAll()).map { it.toDTO() }
}