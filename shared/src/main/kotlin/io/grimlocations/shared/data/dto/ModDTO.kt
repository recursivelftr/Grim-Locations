package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO

data class ModDTO(
    override val id: Int,
    val name: String,
    val difficulties: List<DifficultyDTO>
): DTO