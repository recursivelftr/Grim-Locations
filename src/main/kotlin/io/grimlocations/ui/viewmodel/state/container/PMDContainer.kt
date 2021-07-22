package io.grimlocations.ui.viewmodel.state.container

import io.grimlocations.data.dto.DifficultyDTO
import io.grimlocations.data.dto.ModDTO
import io.grimlocations.data.dto.ProfileDTO

data class PMContainer(
    val profile: ProfileDTO,
    val mod: ModDTO,
)

data class PMDContainer(
    val profile: ProfileDTO,
    val mod: ModDTO,
    val difficulty: DifficultyDTO
)

fun PMDContainer.namesAreEqual(profileName: String, modName: String, difficultyName: String) =
    profileName == profile.name
            && modName == mod.name
            && difficultyName == difficulty.name