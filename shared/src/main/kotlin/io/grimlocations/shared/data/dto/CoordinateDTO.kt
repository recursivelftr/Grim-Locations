package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO

data class CoordinateDTO(
    override val id: Int,
    val coordinate1: String,
    val coordinate2: String,
    val coordinate3: String
): DTO