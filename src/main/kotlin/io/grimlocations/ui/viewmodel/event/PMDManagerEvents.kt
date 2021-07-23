package io.grimlocations.ui.viewmodel.event

import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.ui.viewmodel.PMDManagerViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.PMDManagerStatePopups
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.launch

fun PMDManagerViewModel.selectProfiles(profiles: Set<ProfileDTO>) {
    viewModelScope.launch {
        stateManager.selectProfiles(profiles)
    }
}

fun PMDManagerViewModel.selectMods(mods: Set<ModDTO>) {
    viewModelScope.launch {
        stateManager.selectMods(mods)
    }
}

fun PMDManagerViewModel.selectDifficulties(difficulties: Set<DifficultyDTO>) {
    viewModelScope.launch {
        stateManager.selectDifficulties(difficulties)
    }
}

fun PMDManagerViewModel.editProfile(name: String, profile: ProfileDTO) {
    viewModelScope.launch {
        stateManager.editProfile(name, profile)
    }
}

fun PMDManagerViewModel.editMod(name: String, pmContainer: PMContainer) {
    viewModelScope.launch {
        stateManager.editMod(name, pmContainer)
    }
}

fun PMDManagerViewModel.editDifficulty(name: String, pmdContainer: PMDContainer) {
    viewModelScope.launch {
        stateManager.editDifficulty(name, pmdContainer)
    }
}

fun PMDManagerViewModel.setPopupState(popup: PMDManagerStatePopups) {
    viewModelScope.launch {
        stateManager.setPopupState(popup)
    }
}
