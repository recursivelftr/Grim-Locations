package io.grimlocations.ui.viewmodel.state

import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.data.dto.ProfileModDifficultyMap
import io.grimlocations.framework.ui.State
import io.grimlocations.ui.viewmodel.state.container.PMDContainer

//Should probably refactor all popupOpen variables ot an enum

data class EditorState(
    val profileMap: ProfileModDifficultyMap,
    val selectedPMDLeft: PMDContainer,
    val selectedPMDRight: PMDContainer,
    val activePMD: PMDContainer?,
    val locationsLeft: Set<LocationDTO>,
    val locationsRight: Set<LocationDTO>,
    val selectedLocationsLeft: Set<LocationDTO>,
    val selectedLocationsRight: Set<LocationDTO>,
    val isGDRunning: Boolean,
    val isPropertiesPopupOpen: Boolean,
    val isLoadLocationsPopupOpen: Boolean,
    val isEditLocationRightPopupOpen: Boolean,
    val isEditLocationLeftPopupOpen: Boolean,
    val isConfirmDeleteRightPopupOpen: Boolean,
    val isConfirmDeleteLeftPopupOpen: Boolean,
    val isPMDManagerViewOpen: Boolean,
) : State {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}