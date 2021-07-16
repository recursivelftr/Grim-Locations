package io.grimlocations.ui.viewmodel.state

import io.grimlocations.data.dto.ProfileModDifficultyMap
import io.grimlocations.framework.ui.State
import io.grimlocations.ui.viewmodel.state.container.PMDContainer

data class LoadLocationsState(
    val map: ProfileModDifficultyMap,
    val selected: PMDContainer,
    val locationsFilePath: String,
    val loadMsg: String?,
) : State {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}