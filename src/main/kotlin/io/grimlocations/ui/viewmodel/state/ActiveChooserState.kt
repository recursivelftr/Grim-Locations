package io.grimlocations.ui.viewmodel.state

import io.grimlocations.data.dto.ProfileModDifficultyMap
import io.grimlocations.framework.ui.State
import io.grimlocations.ui.viewmodel.state.container.PMDContainer

data class ActiveChooserState(
    val map: ProfileModDifficultyMap,
    val selected: PMDContainer,
) : State {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}