package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.detectAndCreateProfilesAsync
import io.grimlocations.shared.data.repo.action.getLocationsAsync
import io.grimlocations.shared.data.repo.action.getMetaAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.framework.ui.getState
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.framework.util.awaitAll
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer

@Suppress("UNCHECKED_CAST")
suspend fun GLStateManager.loadEditorState(
    selected: Pair<PMDContainer, PMDContainer>? = null,
) {
    val (pmdMap, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(),
        repository.getMetaAsync()
    )

    if (selected == null) {
        val container = pmdMap.firstContainer()
        val locList = repository.getLocationsAsync(container).await()

        setState(
            EditorState(
                profileMap = pmdMap,
                selectedPMDLeft = container,
                selectedPMDRight = container.copy(),
                locationsLeft = locList,
                locationsRight = locList.toList(), //copies the list,
                activePMD = meta.activePMD
            )
        )
    } else {
        val (locList1, locList2) = awaitAll(
            repository.getLocationsAsync(selected.first),
            repository.getLocationsAsync(selected.second)
        )

        setState(
            EditorState(
                profileMap = pmdMap,
                selectedPMDLeft = selected.first,
                selectedPMDRight = selected.second,
                locationsLeft = locList1,
                locationsRight = locList2,
                activePMD = meta.activePMD
            )
        )
    }
}

suspend fun GLStateManager.loadCharacterProfiles() {
    repository.detectAndCreateProfilesAsync().await()
    reloadEditorState()
}

suspend fun GLStateManager.reloadEditorState() {
    val s = getState<EditorState>()
    loadEditorState(Pair(s.selectedPMDLeft, s.selectedPMDRight))
}