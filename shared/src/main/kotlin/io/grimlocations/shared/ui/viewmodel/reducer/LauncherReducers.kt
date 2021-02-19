package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.repo.action.getProfilesAsync
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherState

suspend fun GLStateManager.loadLauncherState() {
    setState(
        LauncherState(
            profiles = repository.getProfilesAsync().await()
        )
    )
}
