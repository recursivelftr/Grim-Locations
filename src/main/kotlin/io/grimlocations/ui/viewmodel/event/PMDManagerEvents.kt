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

fun PMDManagerViewModel.createProfileAndClosePopup(name: String) {
    viewModelScope.launch {
        stateManager.createProfileAndClosePopup(name)
    }
}

fun PMDManagerViewModel.createModAndClosePopup(name: String, profile: ProfileDTO) {
    viewModelScope.launch {
        stateManager.createModAndClosePopup(name, profile)
    }
}

fun PMDManagerViewModel.createDifficultyAndClosePopup(name: String, pmContainer: PMContainer) {
    viewModelScope.launch {
        stateManager.createDifficultyAndClosePopup(name, pmContainer)
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

fun PMDManagerViewModel.moveMods(selected: Set<ModDTO>, profile: ProfileDTO, moveUp: Boolean) {
    viewModelScope.launch {
        stateManager.moveMods(selected, profile, moveUp)
    }
}

fun PMDManagerViewModel.moveDifficulties(selected: Set<DifficultyDTO>, pmContainer: PMContainer, moveUp: Boolean) {
    viewModelScope.launch {
        stateManager.moveDifficulties(selected, pmContainer, moveUp)
    }
}

fun PMDManagerViewModel.deleteProfiles(selected: Set<ProfileDTO>) {
    viewModelScope.launch {
        stateManager.deleteProfiles(selected)
    }
}

fun PMDManagerViewModel.deleteMods(selected: Set<ModDTO>, profile: ProfileDTO) {
    viewModelScope.launch {
        stateManager.deleteMods(selected, profile)
    }
}

fun PMDManagerViewModel.deleteDifficulties(selected: Set<DifficultyDTO>, pmContainer: PMContainer) {
    viewModelScope.launch {
        stateManager.deleteDifficulties(selected, pmContainer)
    }
}
