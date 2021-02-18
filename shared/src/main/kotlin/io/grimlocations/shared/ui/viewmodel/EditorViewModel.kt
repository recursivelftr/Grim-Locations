package io.grimlocations.shared.ui.viewmodel

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.reducer.loadEditorState
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import kotlinx.coroutines.launch

class EditorViewModel(override val stateManager: GLStateManager) :
    ViewModel<EditorState, GLStateManager>() {

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadEditorState()
        }
    }
}