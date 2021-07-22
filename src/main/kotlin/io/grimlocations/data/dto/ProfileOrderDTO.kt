package io.grimlocations.data.dto

import io.grimlocations.framework.data.dto.OrderedDTO
import java.time.LocalDateTime

data class ProfileOrderDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    override val order: Int,
    val profile: ProfileDTO,
): OrderedDTO