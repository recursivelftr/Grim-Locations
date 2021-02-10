package io.grimlocations.shared.ui.viewmodel

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.framework.ui.viewmodel.stateFlow
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.reducer.loadPropertiesState
import io.grimlocations.shared.ui.viewmodel.state.PropertiesState
import io.grimlocations.shared.util.JSystemFileChooser
import kotlinx.coroutines.launch
import java.io.File

class PropertiesViewModel(override val stateManager: GLStateManager) :
    ViewModel<PropertiesState, GLStateManager>() {

    val installFileChooser by lazy {
        JSystemFileChooser().applyDirectoryOnly().apply {
            stateFlow.value?.installPath?.let {
                currentDirectory = File(it)
            }
        }
    }
    val saveFileChooser by lazy {
        JSystemFileChooser().applyDirectoryOnly().apply {
            stateFlow.value?.savePath?.let {
                currentDirectory = File(it)
            }
        }
    }

    override fun loadState() {
        viewModelScope.launch {
            stateManager.loadPropertiesState()
        }
    }
}