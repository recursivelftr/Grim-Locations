package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherState

suspend fun GLStateManager.loadLauncherState() {
    val map = repository.getProfilesModsDifficultiesAsync(
        includeReservedProfiles = false
    ).await()

    setState(
        LauncherState(
            map = map,
            selected = map.firstContainer()
        )
    )
}

suspend fun GLStateManager.updateLauncherState(state: LauncherState) {
    setState(state)
}
