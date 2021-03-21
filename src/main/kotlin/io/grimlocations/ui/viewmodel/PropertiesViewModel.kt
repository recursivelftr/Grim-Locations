package io.grimlocations.ui.viewmodel

import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.reducer.loadPropertiesState
import io.grimlocations.ui.viewmodel.state.PropertiesState
import io.grimlocations.util.JSystemFileChooser
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