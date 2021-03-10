package io.grimlocations.shared.ui.viewmodel

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.framework.ui.viewmodel.stateFlow
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.reducer.loadLauncherState
import io.grimlocations.shared.ui.viewmodel.state.LauncherState
import io.grimlocations.shared.util.JSystemFileChooser
import kotlinx.coroutines.launch
import java.io.File

class LauncherViewModel(override val stateManager: GLStateManager) :
    ViewModel<LauncherState, GLStateManager>() {

    val locationsFileChooser by lazy {
        JSystemFileChooser().applyCsvTextFilesOnly()
    }

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadLauncherState()
        }
    }
}