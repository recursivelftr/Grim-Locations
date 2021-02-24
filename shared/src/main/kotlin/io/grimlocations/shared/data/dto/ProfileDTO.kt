package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import io.grimlocations.shared.util.assignOnce
import java.time.LocalDateTime

const val RESERVED_PROFILE_GI_LOCATIONS_NAME = "New Character Locations"
const val RESERVED_PROFILE_REDDIT_LOCATIONS_NAME = "Additional Locations"
var RESERVED_PROFILES: List<ProfileDTO> by assignOnce()

data class ProfileDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
) : DTO