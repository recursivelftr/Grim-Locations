package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import io.grimlocations.shared.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_NO_MODS_INDICATOR_NAME = "__RESERVED_NO_MODS__"
var RESERVED_NO_MODS_INDICATOR: ModDTO by assignOnce()

const val DEFAULT_GAME_MOD_NAME = "None"
var DEFAULT_GAME_MOD: ModDTO by assignOnce()

data class ModDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
): DTO

typealias ModDifficultyMap = Map<ModDTO, List<DifficultyDTO>>