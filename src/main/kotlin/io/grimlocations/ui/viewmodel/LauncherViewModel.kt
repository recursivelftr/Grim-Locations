package io.grimlocations.ui.viewmodel

import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.reducer.loadLauncherState
import io.grimlocations.ui.viewmodel.state.LauncherState
import io.grimlocations.util.JSystemFileChooser
import kotlinx.coroutines.launch
import java.io.File

class LauncherViewModel(override val stateManager: GLStateManager) :
    ViewModel<LauncherState, GLStateManager>() {

    val locationsFileChooser by lazy {
        JSystemFileChooser().applyCsvTextFilesOnly().apply {
            stateFlow.value?.installPath?.let {
                currentDirectory = File(it)
            }
        }
    }

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadLauncherState()
        }
    }
}