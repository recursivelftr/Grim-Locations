package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.ui.viewmodel.PropertiesViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.*
import io.grimlocations.shared.ui.viewmodel.reducer.addPropertiesStateErrors
import io.grimlocations.shared.ui.viewmodel.state.PropertiesStateError.*
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import java.io.File

val logger = LogManager.getLogger()

fun PropertiesViewModel.persistState() {
    viewModelScope.launch {
        stateManager.persistPropertiesState()
    }
}

fun PropertiesViewModel.updateInstallPath(path: String) {
    viewModelScope.launch {
        stateManager.updatePropertiesInstallPath(path)

        if (isValidInstallPath(path)) {
            stateManager.removePropertiesStateErrors(GRIM_INTERNALS_NOT_FOUND)
        } else {
            stateManager.addPropertiesStateErrors(GRIM_INTERNALS_NOT_FOUND)
        }
    }
}

fun PropertiesViewModel.updateSavePath(path: String) {
    viewModelScope.launch {
        stateManager.updatePropertiesSavePath(path)
    }
}

private fun isValidInstallPath(path: String): Boolean {
    try {
        File(path).listFiles { it: File -> it.isFile }?.let {
            return it.any { f -> f.name.equals("GrimInternals64.exe", ignoreCase = true) }
        } ?: run {
            logger.error("Path is either not a directory or an I/O error has occurred.")
        }
    } catch (e: SecurityException) {
        logger.error("Read access is denied to this directory: $path", e)
    }

    return false
}

private fun isValidSavePath(path: String): Boolean {

    return false
}