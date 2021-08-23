package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.repo.action.getMetaAsync
import io.grimlocations.data.repo.action.persistMetaInstallAndSavePathAsync
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.guardLet
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.PropertiesState
import io.grimlocations.ui.viewmodel.state.PropertiesStateError
import io.grimlocations.ui.viewmodel.state.PropertiesStateError.GRIM_INTERNALS_NOT_FOUND
import io.grimlocations.ui.viewmodel.state.PropertiesStateWarning
import io.grimlocations.util.extension.gdInstallLocation
import io.grimlocations.util.extension.gdSaveLocation
import org.apache.logging.log4j.LogManager
import java.io.File

private val logger = LogManager.getLogger()

suspend fun GLStateManager.loadPropertiesState() {
    val meta = repository.getMetaAsync().await()
    val installLoc = meta.installLocation ?: getGdInstallLocation()

    setState(
        PropertiesState(
            savePath = meta.saveLocation,
            installPath = installLoc,
            errors = if (!isValidInstallPath(installLoc))
                setOf(GRIM_INTERNALS_NOT_FOUND)
            else
                emptySet()
        )
    )
}

suspend fun GLStateManager.persistPropertiesState() {
    val state: PropertiesState = getState()

    guardLet(state.installPath, state.savePath) { ip, sp ->
        repository.persistMetaInstallAndSavePathAsync(ip, sp)
    } ?: kotlin.run { println("Install Path or Save Path was null.") }

}

suspend fun GLStateManager.updatePropertiesInstallPath(path: String) {
    val state = getState<PropertiesState>()

    setState(
        state.copy(
            installPath = path,
            errors = if (!isValidInstallPath(path))
                setOf(GRIM_INTERNALS_NOT_FOUND, *state.errors.toTypedArray())
            else
                state.errors.filter { it != GRIM_INTERNALS_NOT_FOUND }.toSet()
        ),
    )
}

suspend fun GLStateManager.updatePropertiesSavePath(path: String) {
    setState(getState<PropertiesState>().copy(savePath = path))
}

suspend fun GLStateManager.addPropertiesStateErrors(vararg errors: PropertiesStateError) {
    val state = getState<PropertiesState>()
    setState(state.copy(errors = setOf(*errors, *state.errors.toTypedArray())))
}

suspend fun GLStateManager.removePropertiesStateErrors(vararg errors: PropertiesStateError) {
    val state = getState<PropertiesState>()
    setState(state.copy(errors = state.errors.filter { !errors.contains(it) }.toSet()))
}

suspend fun GLStateManager.addPropertiesStateWarnings(vararg warnings: PropertiesStateWarning) {
    val state = getState<PropertiesState>()
    setState(state.copy(warnings = setOf(*warnings, *state.warnings.toTypedArray())))
}

suspend fun GLStateManager.removePropertiesStateWarnings(vararg warnings: PropertiesStateWarning) {
    val state = getState<PropertiesState>()
    setState(state.copy(warnings = state.warnings.filter { !warnings.contains(it) }.toSet()))
}

fun GLStateManager.getGdInstallLocation(): String = repository.appDirs.gdInstallLocation ?: ""

fun GLStateManager.getGdSaveLocation(): String? = repository.appDirs.gdSaveLocation

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
    try {
        File(path).listFiles { it: File -> it.isDirectory }?.let {
            return it.any { f -> f.name.startsWith("_") }
        } ?: run {
            logger.error("Path is either not a directory or an I/O error has occurred.")
        }
    } catch (e: SecurityException) {
        logger.error("Read access is denied to this directory: $path", e)
    }

    return false
}