package io.grimlocations.shared.constant

import io.grimlocations.shared.data.dto.DifficultyDTO
import io.grimlocations.shared.data.dto.ModDTO
import io.grimlocations.shared.data.dto.ProfileDTO
import java.time.LocalDateTime

const val TITLE = "Grim Locations"
const val VERSION = "0.1.0"

val SYSTEM_RESERVED_NO_MODS_INDICATOR = ModDTO(
    -1,
    LocalDateTime.now(),
    LocalDateTime.now(),
    "__SYSTEM_RESERVED_NO_MODS_INDICATOR__"
)

val SYSTEM_RESERVED_NO_DIFFICULTIES_INDICATOR = DifficultyDTO(
    -1,
    LocalDateTime.now(),
    LocalDateTime.now(),
    "__SYSTEM_RESERVED_NO_DIFFICULTIES_INDICATOR__"
)

val SYSTEM_RESERVED_PROFILES = listOf(
    ProfileDTO(
        -1,
        LocalDateTime.now(),
        LocalDateTime.now(),
        "__SYSTEM_RESERVED_GI_LOCATIONS__"
    ),
    ProfileDTO(
        -1,
        LocalDateTime.now(),
        LocalDateTime.now(),
        "__SYSTEM_RESERVED_REDDIT_LOCATIONS__"
    )
)

val SYSTEM_PROFILE_ALIAS_MAP = mapOf(
    "__SYSTEM_RESERVED_GI_LOCATIONS__" to "New Character Locations",
    "__SYSTEM_RESERVED_REDDIT_LOCATIONS__" to "Additional Locations"
)
