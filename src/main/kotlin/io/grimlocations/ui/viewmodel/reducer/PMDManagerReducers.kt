package io.grimlocations.ui.viewmodel.reducer

import io.grimlocations.data.dto.*
import io.grimlocations.data.repo.action.*
import io.grimlocations.framework.data.dto.replaceDTO
import io.grimlocations.framework.ui.getState
import io.grimlocations.framework.ui.setState
import io.grimlocations.framework.util.awaitAll
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.PMDManagerState
import io.grimlocations.ui.viewmodel.state.PMDManagerStatePopups
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.ui.viewmodel.state.container.toPMContainer
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
        loadPMDManagerState(
            getState<PMDManagerState>().copy(
                selectedProfiles = profiles,
                selectedMods = emptySet(),
                selectedDifficulties = emptySet()
            )
        )
    }
}

suspend fun GLStateManager.selectMods(mods: Set<ModDTO>) {
    withContext(Dispatchers.Main) {
        loadPMDManagerState(
            getState<PMDManagerState>().copy(
                selectedMods = mods,
                selectedDifficulties = emptySet()
            )
        )
    }
}

suspend fun GLStateManager.selectDifficulties(difficulties: Set<DifficultyDTO>) {
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState<PMDManagerState>().copy(selectedDifficulties = difficulties))
    }
}

suspend fun GLStateManager.createProfileAndClosePopup(name: String) {
    val p = repository.findOrCreateProfileAsync(name).await()!!
    val s = getState<PMDManagerState>()
    withContext(Dispatchers.Main) {
        loadPMDManagerState(
            s.copy(
                popupOpen = PMDManagerStatePopups.NONE
            )
        )
    }
}

suspend fun GLStateManager.editProfileAndClosePopup(name: String, profile: ProfileDTO) {
    val p = repository.modifyOrCreateProfileAsync(name, profile).await()!!

    repository.getMetaAsync().await().activePMD?.also {
        if (it.profile == profile && it.profile.name != name) {
            repository.clearActivePMD()

            val editorState = getState<EditorState>()
            when {
                editorState.selectedPMDLeft.profile == it.profile && editorState.selectedPMDRight.profile == it.profile -> resetSelectedPMDs()
                editorState.selectedPMDLeft.profile == it.profile -> resetSelectedPMDLeft()
                editorState.selectedPMDRight.profile == it.profile -> resetSelectedPMDRight()
            }
        }
    }

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

suspend fun GLStateManager.editModAndClosePopup(name: String, pmContainer: PMContainer) {
    val m = repository.modifyOrCreateModAsync(name, pmContainer).await()!!

    repository.getMetaAsync().await().activePMD?.also {
        val activePMContainer = it.toPMContainer()
        if (activePMContainer == pmContainer && it.mod.name != name) {
            repository.clearActivePMD()

            val editorState = getState<EditorState>()
            val selectedPMContainerLeft = editorState.selectedPMDLeft.toPMContainer()
            val selectedPMContainerRight = editorState.selectedPMDRight.toPMContainer()
            when {
                selectedPMContainerLeft == activePMContainer && selectedPMContainerRight == activePMContainer -> resetSelectedPMDs()
                selectedPMContainerLeft == activePMContainer -> resetSelectedPMDLeft()
                selectedPMContainerRight == activePMContainer -> resetSelectedPMDRight()
            }
        }
    }

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

suspend fun GLStateManager.editDifficultyAndClosePopup(name: String, pmdContainer: PMDContainer) {
    val d = repository.modifyOrCreateDifficultyAsync(name, pmdContainer).await()!!

    repository.getMetaAsync().await().activePMD?.also {
        if (it == pmdContainer && it.difficulty.name != name) {
            repository.clearActivePMD()

            val editorState = getState<EditorState>()
            when {
                editorState.selectedPMDLeft == it && editorState.selectedPMDRight == it -> resetSelectedPMDs()
                editorState.selectedPMDLeft == it -> resetSelectedPMDLeft()
                editorState.selectedPMDRight == it -> resetSelectedPMDRight()
            }
        }
    }

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
    if (moveUp) {
        repository.decrementProfilesOrder(selected)
    } else {
        repository.incrementProfilesOrder(selected)
    }
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState())
    }
}

suspend fun GLStateManager.moveMods(selected: Set<ModDTO>, profile: ProfileDTO, moveUp: Boolean) {
    if (moveUp) {
        repository.decrementModsOrder(selected, profile)
    } else {
        repository.incrementModsOrder(selected, profile)
    }
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState())
    }
}

suspend fun GLStateManager.moveDifficulties(selected: Set<DifficultyDTO>, pmContainer: PMContainer, moveUp: Boolean) {
    if (moveUp) {
        repository.decrementDifficultiesOrder(selected, pmContainer)
    } else {
        repository.incrementDifficultiesOrder(selected, pmContainer)
    }
    withContext(Dispatchers.Main) {
        loadPMDManagerState(getState())
    }
}

suspend fun GLStateManager.deleteProfiles(selected: Set<ProfileDTO>) {
    repository.deleteProfiles(selected)

    repository.getMetaAsync().await().activePMD?.also {
        for(p in selected) {
            if (it.profile == p) {
                repository.clearActivePMD()

                val editorState = getState<EditorState>()
                when {
                    editorState.selectedPMDLeft.profile == it.profile && editorState.selectedPMDRight.profile == it.profile -> resetSelectedPMDs()
                    editorState.selectedPMDLeft.profile == it.profile -> resetSelectedPMDLeft()
                    editorState.selectedPMDRight.profile == it.profile -> resetSelectedPMDRight()
                }
                break
            }
        }
    }
    loadPMDManagerState(
        getState<PMDManagerState>().copy(
            popupOpen = PMDManagerStatePopups.NONE
        )
    )
}

suspend fun GLStateManager.deleteMods(selected: Set<ModDTO>, profile: ProfileDTO) {
    repository.deleteMods(selected, profile)
    repository.getMetaAsync().await().activePMD?.also {
        for(m in selected) {
            if (it.profile == profile && it.mod == m) {
                repository.clearActivePMD()

                val editorState = getState<EditorState>()
                val activePMContainer = it.toPMContainer()
                val selectedPMContainerLeft = editorState.selectedPMDLeft.toPMContainer()
                val selectedPMContainerRight = editorState.selectedPMDRight.toPMContainer()

                when {
                    selectedPMContainerLeft == activePMContainer && selectedPMContainerRight == activePMContainer -> resetSelectedPMDs()
                    selectedPMContainerLeft == activePMContainer -> resetSelectedPMDLeft()
                    selectedPMContainerRight == activePMContainer -> resetSelectedPMDRight()
                }
                break
            }
        }
    }
    loadPMDManagerState(
        getState<PMDManagerState>().copy(
            popupOpen = PMDManagerStatePopups.NONE
        )
    )
}

suspend fun GLStateManager.deleteDifficulties(selected: Set<DifficultyDTO>, pmContainer: PMContainer) {
    repository.deleteDifficulties(selected, pmContainer)
    repository.getMetaAsync().await().activePMD?.also {
        val activePMContainer = it.toPMContainer()
        for(d in selected) {
            if (activePMContainer == pmContainer && it.difficulty == d) {
                repository.clearActivePMD()

                val editorState = getState<EditorState>()
                when {
                    editorState.selectedPMDLeft == it && editorState.selectedPMDRight == it -> resetSelectedPMDs()
                    editorState.selectedPMDLeft == it -> resetSelectedPMDLeft()
                    editorState.selectedPMDRight == it -> resetSelectedPMDRight()
                }
                break
            }
        }
    }

    loadPMDManagerState(
        getState<PMDManagerState>().copy(
            popupOpen = PMDManagerStatePopups.NONE
        )
    )
}