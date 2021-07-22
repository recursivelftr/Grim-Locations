package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.dto.*
import io.grimlocations.data.repo.action.getProfilesModsDifficultiesAsync
import io.grimlocations.framework.ui.setState
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.PMDManagerState
import io.grimlocations.ui.viewmodel.state.PMDManagerStatePopups
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer

suspend fun GLStateManager.loadPMDManagerState(previousState: PMDManagerState? = null) {

    val pmdMap = repository.getProfilesModsDifficultiesAsync().await()

    if (previousState == null) {
        setState(
            PMDManagerState(
                profileMap = pmdMap,
                selectedProfiles = emptySet(),
                selectedMods = emptySet(),
                selectedDifficulties = emptySet(),
                popupOpen = PMDManagerStatePopups.NONE
            )
        )
    } else {
        val newSelectedProfiles = previousState.selectedProfiles.filter { pmdMap.containsProfile(it) }.toSet()
        val newSelectedMods: Set<ModDTO>
        val newSelectedDifficulties: Set<DifficultyDTO>

        if (newSelectedProfiles.singleOrNull() == null) { //profiles deleted
            newSelectedMods = emptySet()
            newSelectedDifficulties = emptySet()
        } else {
            val profile = newSelectedProfiles.single()

            newSelectedMods = previousState.selectedMods.filter {
                pmdMap.containsProfileMod(
                    PMContainer(
                        profile,
                        it
                    )
                )
            }.toSet()

            if (newSelectedMods.singleOrNull() == null) {
                newSelectedDifficulties = emptySet()
            } else {
                val mod = newSelectedMods.single()

                newSelectedDifficulties = previousState.selectedDifficulties.filter {
                    pmdMap.containsProfileModDifficulty(
                        PMDContainer(
                            profile,
                            mod,
                            it
                        )
                    )
                }.toSet()
            }
        }

        setState(
            previousState.copy(
                profileMap = pmdMap,
                selectedProfiles = newSelectedProfiles,
                selectedMods = newSelectedMods,
                selectedDifficulties = newSelectedDifficulties,
            )
        )
    }
}