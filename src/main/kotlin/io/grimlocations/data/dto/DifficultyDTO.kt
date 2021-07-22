package io.grimlocations.data.dto

import io.grimlocations.framework.data.dto.DTO
import io.grimlocations.framework.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_NO_DIFFICULTIES_INDICATOR_NAME = "__RESERVED_NO_DIFFICULTIES__"
var RESERVED_NO_DIFFICULTIES_INDICATOR: DifficultyDTO by assignOnce()

data class DifficultyDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
) : DTO