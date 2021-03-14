package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import java.time.LocalDateTime

data class LocationDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String,
    val order: Int,
    val coordinate: CoordinateDTO
) : DTO {
    override fun equals(other: Any?): Boolean {
        other?.also {
            return coordinate == it
        }

        return false
    }
}