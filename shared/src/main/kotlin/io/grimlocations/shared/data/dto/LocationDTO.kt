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
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationDTO

        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinate.hashCode()
    }
}