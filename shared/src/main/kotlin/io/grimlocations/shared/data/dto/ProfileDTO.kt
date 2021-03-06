package io.grimlocations.shared.data.dto

import io.grimlocations.shared.framework.data.dto.DTO
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
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

typealias ProfileModDifficultyMap = Map<ProfileDTO, ModDifficultyMap>

fun ProfileModDifficultyMap.firstContainer(): PMDContainer {
    this.entries.firstOrNull()?.let { (p, mdMap) ->
        mdMap.entries.firstOrNull()?.let { (m, dList) ->
            dList.firstOrNull()?.let { d ->
                return PMDContainer(
                    profile = p,
                    mod = m,
                    difficulty = d
                )
            } ?: error(
                "The mod does not have a difficulty. Every mod should have at least one difficulty, " +
                        "even if it is the no difficulties indicator."
            )
        } ?: error(
            "The profile does not have a mod. Every profile should have at least one mod, " +
                    "even if it is no mods indicator."
        )
    } ?: error("No profiles exist.")
}