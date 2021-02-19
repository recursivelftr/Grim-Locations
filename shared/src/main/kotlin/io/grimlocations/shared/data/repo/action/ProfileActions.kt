package io.grimlocations.shared.data.repo.action

import io.grimlocations.shared.data.domain.Profile
import io.grimlocations.shared.data.domain.ProfileTable
import io.grimlocations.shared.data.dto.ProfileDTO
import io.grimlocations.shared.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

typealias ProfileModDifficultyMap = Map<ProfileDTO, ModDifficultyMap>

suspend fun SqliteRepository.getProfilesAsync(): Deferred<List<ProfileDTO>> = suspendedTransactionAsync(Dispatchers.IO) {
    Profile.wrapRows(ProfileTable.selectAll()).map { it.toDTO() }
}

suspend fun SqliteRepository.getProfilesModsDifficultiesAsync(): Deferred<ProfileModDifficultyMap> = suspendedTransactionAsync(Dispatchers.IO) {
    val map: ProfileModDifficultyMap = mapOf()
    ProfileTable.selectAll().forEach {
        val p = Profile.wrapRow(it)
        val mmap: ModDifficultyMap = mapOf()

        p.mods.forEach { m ->
            mmap.toMutableMap()[m.toDTO()] = m.difficulties.map { d -> d.toDTO() }
        }
        map.toMutableMap()[p.toDTO()] = mmap
    }
    map
}