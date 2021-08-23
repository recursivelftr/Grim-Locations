package io.grimlocations.data.repo.action

import io.grimlocations.data.domain.*
import io.grimlocations.data.dto.MetaDTO
import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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

suspend fun SqliteRepository.persistActivePMDAsync(pmd: PMDContainer) =
    modifyDatabaseAsync {
        val meta = Meta.wrapRow(MetaTable.selectAll().single())
        meta.activeProfile = Profile.findById(pmd.profile.id)
        meta.activeMod = Mod.findById(pmd.mod.id)
        meta.activeDifficulty = Difficulty.findById(pmd.difficulty.id)
    }

suspend fun SqliteRepository.clearActivePMD() {
    modifyDatabase {
        val meta = Meta.wrapRow(MetaTable.selectAll().single())
        meta.activeProfile = null
        meta.activeMod = null
        meta.activeDifficulty = null
    }
}