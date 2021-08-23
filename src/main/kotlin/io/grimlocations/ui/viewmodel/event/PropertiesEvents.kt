package io.grimlocations.ui.viewmodel.event

import io.grimlocations.ui.viewmodel.PropertiesViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.PropertiesStateError.GRIM_INTERNALS_NOT_FOUND
import io.grimlocations.ui.viewmodel.state.PropertiesStateWarning.NO_CHARACTERS_FOUND
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()

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

fun PropertiesViewModel.getGdSaveLocation(): String? = stateManager.getGdSaveLocation()