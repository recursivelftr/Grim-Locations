package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.dto.firstContainer
import io.grimlocations.data.repo.action.getMetaAsync
import io.grimlocations.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.data.repo.createLocationsFromFile
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.awaitAll
import io.grimlocations.framework.util.extension.endsWithOne
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.LoadLocationsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            loadMsg = null,
        )
    )
}

suspend fun GLStateManager.updateLoadLocationsState(state: LoadLocationsState) {
    setState(state)
}


suspend fun GLStateManager.loadLocationsIntoSelectedProfile(filePath: String, onSuccess: () -> Unit) {
    val file = File(filePath)
    val s = getState<LoadLocationsState>()
    if (file.isDirectory || !filePath.endsWithOne("csv", "txt", ignoreCase = true)) {
        "The file is not a csv or txt file.".also {
            withContext(Dispatchers.Main) {
                setState(s.copy(loadMsg = it))
            }
            logger.error(it)
        }
    } else {
        repository.createLocationsFromFile(
            file,
            s.selected
        )?.also {
            withContext(Dispatchers.Main) {
                setState(s.copy(loadMsg = it))
            }
            logger.error(it)
        } ?: kotlin.run {
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}

suspend fun GLStateManager.clearLoadMsg() {
    val s = getState<LoadLocationsState>()
    withContext(Dispatchers.Main) {
        setState(s.copy(loadMsg = null))
    }
}