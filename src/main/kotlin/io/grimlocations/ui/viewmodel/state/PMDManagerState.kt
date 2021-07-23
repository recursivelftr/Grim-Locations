package io.grimlocations.ui.viewmodel.state

import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO
import io.grimlocations.data.dto.ProfileModDifficultyMap
import io.grimlocations.framework.ui.State
import io.grimlocations.ui.viewmodel.state.container.PMDContainer

enum class PMDManagerStatePopups {
    NONE, EDIT_PROFILE, EDIT_MOD, EDIT_DIFFICULTY, DELETE_PROFILE, DELETE_MOD, DELETE_DIFFICULTY
}

data class PMDManagerState(
    val activePMD: PMDContainer?,
    val profileMap: ProfileModDifficultyMap,
    val selectedProfiles: Set<ProfileDTO>,
    val selectedMods: Set<ModDTO>,
    val selectedDifficulties: Set<DifficultyDTO>,
    val popupOpen: PMDManagerStatePopups
) : State {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}