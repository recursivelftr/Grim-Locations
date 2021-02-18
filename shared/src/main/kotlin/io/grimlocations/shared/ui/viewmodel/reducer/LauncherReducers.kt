package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.repo.action.loadProfilesAsync
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherState

suspend fun GLStateManager.loadLauncherState() {
    setState(
        LauncherState(
            profiles = repository.loadProfilesAsync().await()
        )
    )
}
