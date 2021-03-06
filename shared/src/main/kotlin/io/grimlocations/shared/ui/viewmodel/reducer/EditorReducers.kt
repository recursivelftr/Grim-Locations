package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.dto.firstContainer
import io.grimlocations.shared.data.repo.action.getLocationsAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer

suspend fun GLStateManager.loadEditorState() {
    val pmdMap = repository.getProfilesModsDifficultiesAsync().await()
    val container = pmdMap.firstContainer()
    val (p, m, d) = container
    val locList = repository.getLocationsAsync(p, m, d).await()

    setState(
        EditorState(
            profileMap = pmdMap,
            chosenPmdLeft = container,
            chosenPmdRight = container.copy(),
            locationsLeft = locList,
            locationsRight = locList.toList() //copies the list
        )
    )
}