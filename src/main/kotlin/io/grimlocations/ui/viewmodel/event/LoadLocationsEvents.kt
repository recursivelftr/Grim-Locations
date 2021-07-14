package io.grimlocations.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.view.component.legacyOpenOkCancelPopup
import io.grimlocations.ui.viewmodel.LoadLocationsViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

fun LoadLocationsViewModel.loadLocationsIntoSelectedProfile(
    filePath: String,
    window: AppWindow,
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
) {
    viewModelScope.launch {
        val msg = stateManager.loadLocationsIntoSelectedProfile(filePath)
        withContext(Dispatchers.Main) {
            if (msg != null) {
                legacyOpenOkCancelPopup(
                    message = msg,
                    onOpen = onOpenPopup,
                    onOkClicked = {
                        onClosePopup(it)
                        it.closeIfOpen()
                    },
                )
            } else {
                window.closeIfOpen()
            }
        }
    }
}
