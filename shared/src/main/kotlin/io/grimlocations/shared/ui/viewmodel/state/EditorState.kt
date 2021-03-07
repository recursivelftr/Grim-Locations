package io.grimlocations.shared.ui.viewmodel.state

import io.grimlocations.shared.data.dto.LocationDTO
import io.grimlocations.shared.data.dto.ProfileModDifficultyMap
import io.grimlocations.shared.framework.ui.State
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer


data class EditorState(
    val profileMap: ProfileModDifficultyMap,
    val selectedPmdLeft: PMDContainer,
    val selectedPmdRight: PMDContainer,
    val locationsLeft: List<LocationDTO>,
    val locationsRight: List<LocationDTO>
) : State