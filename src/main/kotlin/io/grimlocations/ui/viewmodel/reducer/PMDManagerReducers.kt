package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.dto.*
import io.grimlocations.data.repo.action.*
import io.grimlocations.framework.data.dto.replaceDTO
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.awaitAll
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.PMDManagerState
import io.grimlocations.ui.viewmodel.state.PMDManagerStatePopups
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun GLStateManager.loadPMDManagerState(previousState: PMDManagerState? = null) {

    val (pmdMap, meta) = awaitAll(
        repository.getProfilesModsDifficultiesAsync(false),
        repository.getMetaAsync()
    )

    if (previousState == null) {
        setState(
            PMDManagerState(
                activePMD = meta.activePMD,
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

            newSelectedDifficulties = if (newSelectedMods.singleOrNull() == null) {
                emptySet()
            } else {
                val mod = newSelectedMods.single()
                previousState.selectedDifficulties.filter {
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
                activePMD = meta.activePMD,
                profileMap = pmdMap,
                selectedProfiles = newSelectedProfiles,
                selectedMods = newSelectedMods,
                selectedDifficulties = newSelectedDifficulties,
            )
        )
    }
}

suspend fun GLStateManager.selectProfiles(profiles: Set<ProfileDTO>) {
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState<PMDManagerState>().copy(selectedProfiles = profiles))
    }
}

suspend fun GLStateManager.selectMods(mods: Set<ModDTO>) {
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState<PMDManagerState>().copy(selectedMods = mods))
    }
}

suspend fun GLStateManager.selectDifficulties(difficulties: Set<DifficultyDTO>) {
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState<PMDManagerState>().copy(selectedDifficulties = difficulties))
    }
}

suspend fun GLStateManager.editProfileAndClosePopup(name: String, profile: ProfileDTO) {
    val p = repository.modifyOrCreateProfileAsync(name, profile).await()!!
    val s = getState<PMDManagerState>()
    withContext(Dispatchers.Main) {
        loadPMDManagerState(
            s.copy(
                selectedProfiles = s.selectedProfiles.replaceDTO(profile, p),
                popupOpen = PMDManagerStatePopups.NONE
            )
        )
    }
}

suspend fun GLStateManager.editMod(name: String, pmContainer: PMContainer) {
    val m = repository.modifyOrCreateModAsync(name, pmContainer).await()!!
    val s = getState<PMDManagerState>()
    withContext(Dispatchers.Main) {
        loadPMDManagerState(
            s.copy(
                selectedMods = s.selectedMods.replaceDTO(pmContainer.mod, m),
                popupOpen = PMDManagerStatePopups.NONE
            )
        )
    }
}

suspend fun GLStateManager.editDifficulty(name: String, pmdContainer: PMDContainer) {
    val d = repository.modifyOrCreateDifficultyAsync(name, pmdContainer).await()!!
    val s = getState<PMDManagerState>()
    withContext(Dispatchers.Main) {
        loadPMDManagerState(
            s.copy(
                selectedDifficulties = s.selectedDifficulties.replaceDTO(pmdContainer.difficulty, d),
                popupOpen = PMDManagerStatePopups.NONE
            )
        )
    }
}

suspend fun GLStateManager.setPopupState(popup: PMDManagerStatePopups) {
    withContext(Dispatchers.Main) {
        setState(getState<PMDManagerState>().copy(popupOpen = popup))
    }
}

suspend fun GLStateManager.moveProfiles(selected: Set<ProfileDTO>, moveUp: Boolean) {
    if(moveUp) {
        repository.decrementProfilesOrder(selected)
    } else {
        repository.incrementProfilesOrder(selected)
    }
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState())
    }
}

fun GLStateManager.deleteProfiles(selected: Set<ProfileDTO>) {

}