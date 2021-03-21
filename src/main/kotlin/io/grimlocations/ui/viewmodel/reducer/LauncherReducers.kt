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
import io.grimlocations.ui.viewmodel.state.LauncherState
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
            installPath = meta.installLocation!!,
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

suspend fun GLStateManager.writeToLocationsFile(): String? {
    val meta = repository.getMetaAsync().await()
    meta.installLocation?.also {
        val s = getState<LauncherState>()
        return if (it.endsWithOne("/", "\\"))
            repository.writeLocationsToFile(File(it + "GrimInternals_TeleportList.txt"), s.selected)
        else
            repository.writeLocationsToFile(File(it + File.separator + "GrimInternals_TeleportList.txt"), s.selected)
    }

    return "GD install location needs to be set."
}