package io.grimlocations.ui.viewmodel.state.container

import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO

data class PMDContainer(
    val profile: ProfileDTO,
    val mod: ModDTO,
    val difficulty: DifficultyDTO
)