package io.grimlocations.shared.ui.viewmodel.state.container

import io.grimlocations.shared.data.dto.DifficultyDTO
import io.grimlocations.shared.data.dto.ModDTO
import io.grimlocations.shared.data.dto.ProfileDTO

data class PMDContainer(
    val profile: ProfileDTO,
    val mod: ModDTO,
    val difficulty: DifficultyDTO
)