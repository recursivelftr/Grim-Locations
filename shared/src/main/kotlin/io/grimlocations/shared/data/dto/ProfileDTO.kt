package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import io.grimlocations.shared.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_PROFILE_GI_LOCATIONS_NAME = "__RESERVED_GI_LOCATIONS__"
const val RESERVED_PROFILE_REDDIT_LOCATIONS_NAME = "__RESERVED_REDDIT_LOCATIONS__"
val RESERVED_PROFILES: List<ProfileDTO> by assignOnce()

val RESERVED_PROFILE_ALIAS_MAP = mapOf(
    "__RESERVED_GI_LOCATIONS__" to "New Character Locations",
    "__RESERVED_REDDIT_LOCATIONS__" to "Additional Locations"
)

data class ProfileDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
) : DTO