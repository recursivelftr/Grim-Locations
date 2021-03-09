package io.grimlocations.shared.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import io.grimlocations.shared.framework.ui.viewmodel.stateFlow
import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.persistActivePMD
import io.grimlocations.shared.ui.viewmodel.reducer.reloadEditorState
import io.grimlocations.shared.ui.viewmodel.reducer.updateLauncherState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun LauncherViewModel.selectPMD(selected: PMDContainer) {
    stateFlow.value?.let {
        viewModelScope.launch {
            stateManager.updateLauncherState(it.copy(selected = selected))
        }
    }
}

fun LauncherViewModel.persistPMD(window: AppWindow) {
    viewModelScope.launch {
        stateManager.persistActivePMD()
        withContext(Dispatchers.Main) {
            window.closeIfOpen()
            stateManager.reloadEditorState()
        }
    }
}