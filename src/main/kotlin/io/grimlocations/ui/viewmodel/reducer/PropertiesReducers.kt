package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.repo.action.getMetaAsync
import io.grimlocations.data.repo.action.persistMetaInstallAndSavePathAsync
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.guardLet
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.PropertiesState
import io.grimlocations.ui.viewmodel.state.PropertiesStateError
import io.grimlocations.ui.viewmodel.state.PropertiesStateWarning

suspend fun GLStateManager.loadPropertiesState() {
    val meta = repository.getMetaAsync().await()
    setState(
        PropertiesState(
            savePath = meta.saveLocation,
            installPath = meta.installLocation
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
    setState(getState<PropertiesState>().copy(installPath = path))
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