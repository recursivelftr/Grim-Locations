package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.dto.firstContainer
import io.grimlocations.data.repo.action.getMetaAsync
import io.grimlocations.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.data.repo.action.persistActivePMDAsync
import io.grimlocations.data.repo.createLocationsFromFile
import io.grimlocations.data.repo.writeLocationsToFile
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.awaitAll
import io.grimlocations.framework.util.extension.endsWithOne
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.ActiveChooserState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

private val logger: Logger = LogManager.getLogger()

@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadActiveChooserState() {
    val (map, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(
            includeReservedProfiles = false
        ),
        repository.getMetaAsync()
    )

    setState(
        ActiveChooserState(
            map = map,
            selected = meta.activePMD ?: map.firstContainer(),
        )
    )
}

suspend fun GLStateManager.updateActiveChooserState(state: ActiveChooserState) {
    setState(state)
}

suspend fun GLStateManager.persistActivePMD() {
    val s = getState<ActiveChooserState>()
    repository.persistActivePMDAsync(s.selected).await()
}


suspend fun GLStateManager.writeToLocationsFile(): String? {
    val meta = repository.getMetaAsync().await()
    meta.installLocation?.also {
        val s = getState<ActiveChooserState>()
        return if (it.endsWithOne("/", "\\"))
            repository.writeLocationsToFile(File(it + "GrimInternals_TeleportList.txt"), s.selected)
        else
            repository.writeLocationsToFile(File(it + File.separator + "GrimInternals_TeleportList.txt"), s.selected)
    }

    return "GD install location needs to be set."
}