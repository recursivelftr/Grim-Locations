package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.repo.action.loadProfilesAsync
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherEditorState

suspend fun GLStateManager.loadLauncherEditorState() {
    setState(
        LauncherEditorState(
            showPropertiesDialog = false,
            profiles = repository.loadProfilesAsync().await()
        )
    )
}

suspend fun GLStateManager.updateShowPropertiesDialog(value: Boolean) {
    setState(
        getState<LauncherEditorState>().copy(showPropertiesDialog = value)
    )
}