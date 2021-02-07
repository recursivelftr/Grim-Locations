package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.repo.action.getMetaAsync
import io.grimlocations.shared.data.repo.action.persistMetaInstallAndSavePathAsync
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.PropertiesState
import io.grimlocations.shared.util.guardLet

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