package io.grimlocations.shared.ui.viewmodel

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.reducer.loadPropertiesState
import io.grimlocations.shared.ui.viewmodel.state.PropertiesState
import kotlinx.coroutines.launch

class PropertiesViewModel(override val stateManager: GLStateManager) :
    ViewModel<PropertiesState, GLStateManager>() {

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadPropertiesState()
        }
    }
}