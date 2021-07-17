package io.grimlocations.ui.viewmodel.event

import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.viewmodel.LoadLocationsViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val logger: Logger = LogManager.getLogger()

fun LoadLocationsViewModel.selectPMD(selected: PMDContainer) {
    stateFlow.value?.let {
        viewModelScope.launch {
            stateManager.updateLoadLocationsState(it.copy(selected = selected))
        }
    }
}

fun LoadLocationsViewModel.updateLocationsFilePath(path: String) {
    stateFlow.value?.let {
        viewModelScope.launch {
            stateManager.updateLoadLocationsState(it.copy(locationsFilePath = path))
        }
    }
}

fun LoadLocationsViewModel.getGdSaveLocation(): String? = stateManager.getGdSaveLocation()

fun LoadLocationsViewModel.loadLocationsIntoSelectedProfile(filePath: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
        stateManager.loadLocationsIntoSelectedProfile(filePath, onSuccess)
    }
}

fun LoadLocationsViewModel.clearLoadMsg() {
    viewModelScope.launch {
        stateManager.clearLoadMsg()
    }
}

fun LoadLocationsViewModel.reloadEditorStateAndClose() {
    viewModelScope.launch {
        with(stateManager) {
            reloadEditorState(
                getState<EditorState>().copy(isLoadLocationsPopupOpen = false)
            )
        }
    }
}
