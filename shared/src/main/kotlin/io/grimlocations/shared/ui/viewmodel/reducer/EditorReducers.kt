package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.repo.action.loadProfilesModsDifficultiesAsync
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.EditorState

suspend fun GLStateManager.loadEditorState() {

    setState(
        EditorState(
            profileMap = repository.loadProfilesModsDifficultiesAsync().await()
        )
    )
}