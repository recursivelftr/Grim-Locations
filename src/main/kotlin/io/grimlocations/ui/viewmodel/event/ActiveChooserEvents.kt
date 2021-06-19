package io.grimlocations.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.view.component.openOkCancelPopup
import io.grimlocations.ui.viewmodel.ActiveChooserViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val logger: Logger = LogManager.getLogger()

fun ActiveChooserViewModel.selectPMD(selected: PMDContainer) {
    stateFlow.value?.let {
        viewModelScope.launch {
            stateManager.updateActiveChooserState(it.copy(selected = selected))
        }
    }
}

fun ActiveChooserViewModel.persistPMDAndWriteLocations(window: AppWindow) {
    viewModelScope.launch {
        stateManager.persistActivePMD()
        stateManager.writeToLocationsFile()
        withContext(Dispatchers.Main) {
            window.closeIfOpen()
            stateManager.reloadEditorState()
        }
    }
}
