package io.grimlocations.ui.viewmodel

import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.reducer.loadActiveChooserState
import io.grimlocations.ui.viewmodel.reducer.loadLoadLocationsState
import io.grimlocations.ui.viewmodel.state.LoadLocationsState
import io.grimlocations.util.JSystemFileChooser
import kotlinx.coroutines.launch
import java.io.File

class LoadLocationsViewModel(override val stateManager: GLStateManager) :
    ViewModel<LoadLocationsState, GLStateManager>() {

    val locationsFileChooser by lazy {
        JSystemFileChooser().applyCsvTextFilesOnly().apply {
            stateFlow.value?.locationsFilePath?.let {
                currentDirectory = File(it)
            }
        }
    }

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadLoadLocationsState()
        }
    }
}