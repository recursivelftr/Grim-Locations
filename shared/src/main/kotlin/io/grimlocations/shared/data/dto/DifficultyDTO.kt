package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import io.grimlocations.shared.framework.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_NO_DIFFICULTIES_INDICATOR_NAME = "__RESERVED_NO_DIFFICULTIES__"
var RESERVED_NO_DIFFICULTIES_INDICATOR: DifficultyDTO by assignOnce()

const val DEFAULT_GAME_NORMAL_DIFFICULTY_NAME = "Normal"
const val DEFAULT_GAME_VETERAN_DIFFICULTY_NAME = "Veteran"
const val DEFAULT_GAME_ELITE_DIFFICULTY_NAME = "Elite"
const val DEFAULT_GAME_ULTIMATE_DIFFICULTY_NAME = "Ultimate"

var DEFAULT_GAME_NORMAL_DIFFICULTY: DifficultyDTO by assignOnce()
var DEFAULT_GAME_VETERAN_DIFFICULTY: DifficultyDTO by assignOnce()
var DEFAULT_GAME_ELITE_DIFFICULTY: DifficultyDTO by assignOnce()
var DEFAULT_GAME_ULTIMATE_DIFFICULTY: DifficultyDTO by assignOnce()

data class DifficultyDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
) : DTO