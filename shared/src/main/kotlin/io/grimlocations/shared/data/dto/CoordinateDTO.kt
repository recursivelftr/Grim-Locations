package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import java.time.LocalDateTime

data class CoordinateDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val coordinate1: String,
    val coordinate2: String,
    val coordinate3: String
): DTO