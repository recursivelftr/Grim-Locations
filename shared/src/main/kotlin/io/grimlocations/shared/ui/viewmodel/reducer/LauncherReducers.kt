package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.MetaDTO
import io.grimlocations.shared.data.dto.ProfileModDifficultyMap
import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.getMetaAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.data.repo.action.persistActivePMDAsync
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherState
import kotlinx.coroutines.awaitAll

@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadLauncherState() {
    val list = awaitAll(
        repository.getProfilesModsDifficultiesAsync(
            includeReservedProfiles = false
        ),
        repository.getMetaAsync()
    )
    val map = list[0] as ProfileModDifficultyMap
    val meta = list[1] as MetaDTO

    setState(
        LauncherState(
            map = map,
            selected = meta.activePMD ?: map.firstContainer()
        )
    )
}

suspend fun GLStateManager.updateLauncherState(state: LauncherState) {
    setState(state)
}

suspend fun GLStateManager.persistActivePMD() {
    val s = getState<LauncherState>()
    repository.persistActivePMDAsync(s.selected).await()
}
