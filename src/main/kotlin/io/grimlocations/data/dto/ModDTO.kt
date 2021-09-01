package io.grimlocations.data.dto

import io.grimlocations.framework.data.dto.UserCreatedNameDTO
import io.grimlocations.framework.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_NO_MODS_INDICATOR_NAME = "__RESERVED_NO_MODS__"
var RESERVED_NO_MODS_INDICATOR: ModDTO by assignOnce()

data class ModDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    override val name: String,
    override val isUserCreated: Boolean,
): UserCreatedNameDTO

var NO_MODS_OR_DIFFICULTIES_MAP: ModDifficultyMap by assignOnce()

typealias ModDifficultyMap = Map<ModDTO, List<DifficultyDTO>>
typealias MutableModDifficultyMap = MutableMap<ModDTO, MutableList<DifficultyDTO>>