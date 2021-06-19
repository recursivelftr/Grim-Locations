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
import io.grimlocations.ui.viewmodel.state.LoadLocationsState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

private val logger: Logger = LogManager.getLogger()

@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadLoadLocationsState() {
    val (map, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(
            includeReservedProfiles = false
        ),
        repository.getMetaAsync()
    )

    setState(
        LoadLocationsState(
            map = map,
            selected = meta.activePMD ?: map.firstContainer(),
            locationsFilePath = meta.installLocation!!,
        )
    )
}

suspend fun GLStateManager.updateLoadLocationsState(state: LoadLocationsState) {
    setState(state)
}


suspend fun GLStateManager.loadLocationsIntoSelectedProfile(filePath: String): String? {
    val file = File(filePath)
    return if (file.isDirectory || !filePath.endsWithOne("csv", "txt", ignoreCase = true)) {
        "The file is not a csv or txt file.".also {
            logger.error(it)
        }
    } else {
        repository.createLocationsFromFile(
            file,
            getState<LoadLocationsState>().selected
        )?.also {
            logger.error(it)
        }
    }
}