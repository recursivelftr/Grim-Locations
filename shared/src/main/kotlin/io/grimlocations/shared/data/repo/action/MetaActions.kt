package io.grimlocations.shared.data.repo.action

import io.grimlocations.shared.data.domain.Meta
import io.grimlocations.shared.data.domain.MetaTable
import io.grimlocations.shared.data.dto.MetaDTO
import io.grimlocations.shared.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

suspend fun SqliteRepository.persistMetaInstallAndSavePathAsync(installPath: String, savePath: String): Deferred<Unit> =
    modifyDatabaseAsync {
        val meta = Meta.wrapRow(MetaTable.selectAll().single())
        meta.installLocation = installPath
        meta.saveLocation = savePath
    }

suspend fun SqliteRepository.getMetaAsync(): Deferred<MetaDTO> = suspendedTransactionAsync(Dispatchers.IO) {
    Meta.wrapRow(MetaTable.selectAll().single()).toDTO()
}

suspend fun SqliteRepository.arePropertiesSetAsync(): Deferred<Boolean> = suspendedTransactionAsync(Dispatchers.IO) {
    val meta = Meta.wrapRow(MetaTable.selectAll().single())
    meta.installLocation != null && meta.saveLocation != null
}