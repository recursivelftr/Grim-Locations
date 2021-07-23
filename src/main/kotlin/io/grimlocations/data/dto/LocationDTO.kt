package io.grimlocations.data.dto

import io.grimlocations.framework.data.dto.OrderedDTO
import io.grimlocations.framework.data.dto.OrderedNameDTO
import java.time.LocalDateTime

data class LocationDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    override val order: Int,
    override val name: String,
    val coordinate: CoordinateDTO
) : OrderedNameDTO {
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