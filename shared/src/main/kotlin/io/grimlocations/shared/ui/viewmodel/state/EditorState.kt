package io.grimlocations.shared.ui.viewmodel.state

import io.grimlocations.shared.data.dto.LocationDTO
import io.grimlocations.shared.data.dto.ProfileModDifficultyMap
import io.grimlocations.shared.framework.ui.State
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer


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
    val locationsFileLastModified: Long?
) : State