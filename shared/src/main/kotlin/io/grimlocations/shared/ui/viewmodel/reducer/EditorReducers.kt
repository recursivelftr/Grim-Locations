package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.detectAndCreateProfilesAsync
import io.grimlocations.shared.data.repo.action.getLocationsAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.awaitAll

suspend fun GLStateManager.loadEditorState(
    selected: Pair<PMDContainer, PMDContainer>? = null,
) {
    val pmdMap = repository.getProfilesModsDifficultiesAsync().await()

    if(selected == null) {
        val container = pmdMap.firstContainer()
        val locList = repository.getLocationsAsync(container).await()

        setState(
            EditorState(
                profileMap = pmdMap,
                selectedPmdLeft = container,
                selectedPmdRight = container.copy(),
                locationsLeft = locList,
                locationsRight = locList.toList() //copies the list
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
                selectedPmdLeft = selected.first,
                selectedPmdRight = selected.second,
                locationsLeft = locList1,
                locationsRight = locList2
            )
        )
    }
}

suspend fun GLStateManager.loadCharacterProfiles(
    selected: Pair<PMDContainer, PMDContainer>,
) {
    repository.detectAndCreateProfilesAsync().await()
    loadEditorState(selected)
}