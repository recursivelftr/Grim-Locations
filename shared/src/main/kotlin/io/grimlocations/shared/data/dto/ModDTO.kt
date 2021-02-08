package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import java.time.LocalDateTime

data class ModDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
): DTO