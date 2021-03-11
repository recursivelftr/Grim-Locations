package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.getMetaAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.data.repo.action.persistActivePMDAsync
import io.grimlocations.shared.data.repo.createLocationsFromFile
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.framework.util.awaitAll
import io.grimlocations.shared.framework.util.extension.endsWithOne
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

private val logger: Logger = LogManager.getLogger()

@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadLauncherState() {
    val (map, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(
            includeReservedProfiles = false
        ),
        repository.getMetaAsync()
    )

    setState(
        LauncherState(
            map = map,
            selected = meta.activePMD ?: map.firstContainer(),
            locationsFilePath = null,
        )
    )
}

suspend fun GLStateManager.updateLauncherState(state: LauncherState) {
    setState(state)
}

suspend fun GLStateManager.persistActivePMD() {
    val s = getState<LauncherState>()
    repository.persistActivePMDAsync(s.selected).await()
}

suspend fun GLStateManager.loadLocationsIntoSelectedProfile(filePath: String): String {
    val file = File(filePath)
    return if (file.isDirectory || !filePath.endsWithOne("csv", "txt", ignoreCase = true)) {
        "The file is not a csv or txt file.".also {
            logger.error(it)
        }
    } else {
        repository.createLocationsFromFile(
            file,
            getState<LauncherState>().selected
        )?.also {
            logger.error(it)
        } ?: "Locations successfully loaded."
    }
}