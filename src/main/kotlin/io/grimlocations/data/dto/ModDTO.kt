package io.grimlocations.data.dto

import io.grimlocations.framework.data.dto.DTO
import io.grimlocations.framework.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_NO_MODS_INDICATOR_NAME = "__RESERVED_NO_MODS__"
var RESERVED_NO_MODS_INDICATOR: ModDTO by assignOnce()

data class ModDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
): DTO

typealias ModDifficultyMap = Map<ModDTO, List<DifficultyDTO>>
typealias MutableModDifficultyMap = MutableMap<ModDTO, MutableList<DifficultyDTO>>