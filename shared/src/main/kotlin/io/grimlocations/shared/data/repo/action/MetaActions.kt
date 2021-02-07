package io.grimlocations.shared.data.repo.action

import io.grimlocations.shared.data.domain.Meta
import io.grimlocations.shared.data.domain.MetaTable
import io.grimlocations.shared.data.repo.SqliteRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun SqliteRepository.persistMetaInstallAndSavePathAsync(installPath: String, savePath: String): Deferred<Unit> =
    modifyDatabaseAsync {
        val meta = Meta.wrapRow(MetaTable.selectAll().single())
        meta.installLocation = installPath
        meta.saveLocation = savePath
    }

suspend fun SqliteRepository.getMetaAsync(): Deferred<Meta> = suspendedTransactionAsync(Dispatchers.IO) {
    Meta.wrapRow(MetaTable.selectAll().single())
}

suspend fun SqliteRepository.arePropertiesSet(): Deferred<Boolean> = suspendedTransactionAsync(Dispatchers.IO) {
    val meta = Meta.wrapRow(MetaTable.selectAll().single())
    meta.installLocation != null && meta.saveLocation != null
}