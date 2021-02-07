package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO

data class LocationDTO(
    override val id: Int,
    val profile: ProfileDTO,
    val mod: ModDTO,
    val difficulty: DifficultyDTO,
    val coordinate: CoordinateDTO
) : DTO