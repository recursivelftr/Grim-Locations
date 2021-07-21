package io.grimlocations.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.reducer.loadEditorState
import io.grimlocations.ui.viewmodel.state.EditorState
import kotlinx.coroutines.launch

class EditorViewModel(override val stateManager: GLStateManager) :
    ViewModel<EditorState, GLStateManager>() {

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadEditorState()
        }
    }
}