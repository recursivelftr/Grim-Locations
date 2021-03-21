package io.grimlocations.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.view.component.openOkCancelPopup
import io.grimlocations.ui.viewmodel.LauncherViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val logger: Logger = LogManager.getLogger()

fun LauncherViewModel.selectPMD(selected: PMDContainer) {
    stateFlow.value?.let {
        viewModelScope.launch {
            stateManager.updateLauncherState(it.copy(selected = selected))
        }
    }
}

fun LauncherViewModel.persistPMDAndWriteLocations(window: AppWindow) {
    viewModelScope.launch {
        stateManager.persistActivePMD()
        stateManager.writeToLocationsFile()
        withContext(Dispatchers.Main) {
            window.closeIfOpen()
            stateManager.reloadEditorState()
        }
    }
}

fun LauncherViewModel.loadLocationsIntoSelectedProfile(
    filePath: String,
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
) {
    viewModelScope.launch {
        val msg = stateManager.loadLocationsIntoSelectedProfile(filePath)
        withContext(Dispatchers.Main) {
            openOkCancelPopup(
                message = msg,
                onOpen = onOpenPopup,
                onOkClicked = {
                    onClosePopup(it)
                    it.closeIfOpen()
                },
            )
        }
    }
}

fun LauncherViewModel.writeToLocationsFile() {
    viewModelScope.launch {

    }
}