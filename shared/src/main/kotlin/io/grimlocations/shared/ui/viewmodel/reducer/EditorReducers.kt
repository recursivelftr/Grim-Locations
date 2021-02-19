package io.grimlocations.shared.ui.viewmodel.reducer

import io.grimlocations.shared.data.repo.action.getLocationsAsync
import io.grimlocations.shared.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.shared.framework.ui.setState
import io.grimlocations.shared.ui.GLStateManager
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import io.grimlocations.shared.ui.viewmodel.state.substate.PMDContainer

suspend fun GLStateManager.loadEditorState() {

    val pmdMap = repository.getProfilesModsDifficultiesAsync().await()

    pmdMap.entries.firstOrNull()?.let { (p, mdMap) ->
        mdMap.entries.firstOrNull()?.let { (m, dList) ->
            dList.firstOrNull()?.let { d ->
                val locList = repository.getLocationsAsync(p, m, d).await()
                val container = PMDContainer(
                    profile = p,
                    mod = m,
                    difficulty = d
                )
                setState(
                    EditorState(
                        profileMap = pmdMap,
                        chosenPmdLeft = container,
                        chosenPmdRight = container.copy(),
                        locationsLeft = locList,
                        locationsRight = locList.toList() //copies the list
                    )
                )
            } ?: error(
                "The mod does not have a difficulty. Every mod should have at least one difficulty, " +
                        "even if it is the no difficulties indicator."
            )
        } ?: error(
            "The profile does not have a mod. Every profile should have at least one mod, " +
                    "even if it is no mods indicator."
        )
    } ?: error("No profiles exist.")
}