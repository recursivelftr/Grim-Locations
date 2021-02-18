package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import io.grimlocations.shared.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_NO_DIFFICULTIES_INDICATOR_NAME = "__RESERVED_NO_DIFFICULTIES__"
var RESERVED_NO_DIFFICULTIES_INDICATOR: DifficultyDTO by assignOnce()

data class DifficultyDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
) : DTO