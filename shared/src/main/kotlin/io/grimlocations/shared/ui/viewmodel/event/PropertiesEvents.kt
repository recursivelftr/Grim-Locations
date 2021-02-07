package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.ui.viewmodel.PropertiesViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.persistPropertiesState
import io.grimlocations.shared.ui.viewmodel.reducer.updatePropertiesInstallPath
import io.grimlocations.shared.ui.viewmodel.reducer.updatePropertiesSavePath
import kotlinx.coroutines.launch

fun PropertiesViewModel.persistState() {
    viewModelScope.launch {
        stateManager.persistPropertiesState()
    }
}

fun PropertiesViewModel.updateInstallPath(path: String) {
    viewModelScope.launch {
        stateManager.updatePropertiesInstallPath(path)
    }
}

fun PropertiesViewModel.updateSavePath(path: String) {
    viewModelScope.launch {
        stateManager.updatePropertiesSavePath(path)
    }
}
