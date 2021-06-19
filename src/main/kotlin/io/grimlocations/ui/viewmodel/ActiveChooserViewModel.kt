package io.grimlocations.ui.viewmodel

import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.reducer.loadActiveChooserState
import io.grimlocations.ui.viewmodel.state.ActiveChooserState
import kotlinx.coroutines.launch

class ActiveChooserViewModel(override val stateManager: GLStateManager) :
    ViewModel<ActiveChooserState, GLStateManager>() {

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadActiveChooserState()
        }
    }
}