package io.grimlocations.ui.viewmodel

import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.reducer.loadPMDManagerState
import io.grimlocations.ui.viewmodel.state.PMDManagerState
import kotlinx.coroutines.launch

class PMDManagerViewModel(override val stateManager: GLStateManager) :
    ViewModel<PMDManagerState, GLStateManager>() {

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadPMDManagerState()
        }
    }
}