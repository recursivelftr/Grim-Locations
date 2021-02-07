package io.grimlocations.shared.ui.viewmodel

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.reducer.loadLauncherEditorState
import io.grimlocations.shared.ui.viewmodel.state.LauncherEditorState
import kotlinx.coroutines.launch

class LauncherEditorViewModel(override val stateManager: GLStateManager) :
    ViewModel<LauncherEditorState, GLStateManager>() {

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadLauncherEditorState()
        }
    }
}