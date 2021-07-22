package io.grimlocations.ui.viewmodel

import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.PMDManagerState

class PMDManagerViewModel(override val stateManager: GLStateManager) :
    ViewModel<PMDManagerState, GLStateManager>() {

    override fun loadState() {
        TODO("Not yet implemented")
    }
}