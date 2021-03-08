package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.loadCharacterProfiles
import io.grimlocations.shared.ui.viewmodel.reducer.loadEditorState
import io.grimlocations.shared.ui.viewmodel.reducer.reloadEditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.launch

fun EditorViewModel.loadCharacterProfiles() {
    viewModelScope.launch {
        stateManager.loadCharacterProfiles()
    }
}

fun EditorViewModel.reloadState() {
    viewModelScope.launch {
        stateManager.reloadEditorState()
    }
}