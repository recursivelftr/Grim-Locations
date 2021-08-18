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

fun PMDManagerViewModel.editProfileAndClosePopup(name: String, profile: ProfileDTO) {
    viewModelScope.launch {
        stateManager.editProfileAndClosePopup(name, profile)
    }
}

fun PMDManagerViewModel.editModAndClosePopup(name: String, pmContainer: PMContainer) {
    viewModelScope.launch {
        stateManager.editModAndClosePopup(name, pmContainer)
    }
}

fun PMDManagerViewModel.editDifficultyAndClosePopup(name: String, pmdContainer: PMDContainer) {
    viewModelScope.launch {
        stateManager.editDifficultyAndClosePopup(name, pmdContainer)
    }
}

fun PMDManagerViewModel.setPopupState(popup: PMDManagerStatePopups) {
    viewModelScope.launch {
        stateManager.setPopupState(popup)
    }
}

fun PMDManagerViewModel.moveProfiles(selected: Set<ProfileDTO>, moveUp: Boolean) {
    viewModelScope.launch {
        stateManager.moveProfiles(selected, moveUp)
    }
}

fun PMDManagerViewModel.moveMods(selected: Set<ProfileDTO>, moveUp: Boolean) {
    viewModelScope.launch {
        stateManager.moveProfiles(selected, moveUp)
    }
}

fun PMDManagerViewModel.deleteProfiles(selected: Set<ProfileDTO>) {
    viewModelScope.launch {
        stateManager.deleteProfiles(selected)
    }
}
