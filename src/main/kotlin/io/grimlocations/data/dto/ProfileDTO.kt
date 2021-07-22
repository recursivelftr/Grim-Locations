package io.grimlocations.data.dto

import io.grimlocations.framework.data.dto.DTO
import io.grimlocations.framework.util.assignOnce
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import java.time.LocalDateTime

const val RESERVED_PROFILE_GI_LOCATIONS_NAME = "New Character Locations"
const val RESERVED_PROFILE_ACT_1_LOCATIONS_NAME = "Act I"
const val RESERVED_PROFILE_ACT_2_LOCATIONS_NAME = "Act II"
const val RESERVED_PROFILE_ACT_3_LOCATIONS_NAME = "Act III"
const val RESERVED_PROFILE_ACT_4_LOCATIONS_NAME = "Act IV"
const val RESERVED_PROFILE_ACT_5_LOCATIONS_NAME = "Act V"
const val RESERVED_PROFILE_ACT_6_LOCATIONS_NAME = "Act VI"
const val RESERVED_PROFILE_ACT_7_LOCATIONS_NAME = "Act VII"
const val RESERVED_PROFILES_MONSTER_TOTEM_LOCATIONS_NAME = "Monster Totems"
const val RESERVED_PROFILES_NEMESIS_LOCATIONS_NAME = "Nemesis"
const val RESERVED_PROFILE_OTHER_LOCATIONS_NAME = "Other Locations"
var RESERVED_PROFILES: List<ProfileDTO> by assignOnce()

data class ProfileDTO(
    override val id: Int,
    override val created: LocalDateTime,
    override val modified: LocalDateTime,
    val name: String
) : DTO

typealias ProfileModDifficultyMap = Map<ProfileDTO, ModDifficultyMap>
typealias MutableProfileModDifficultyMap = MutableMap<ProfileDTO, MutableModDifficultyMap>

val ProfileDTO.isReserved: Boolean
    get() = RESERVED_PROFILES.find { it.id == id } != null

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

fun ProfileModDifficultyMap.hasOnlyReservedProfiles(): Boolean {
    return this.size == RESERVED_PROFILES.size && this.keys.containsAll(RESERVED_PROFILES)
}

fun ProfileModDifficultyMap.containsProfile(profileDTO: ProfileDTO) =
    this.containsKey(profileDTO)

fun ProfileModDifficultyMap.containsProfileMod(pmContainer: PMContainer) =
    this.getOrElse(pmContainer.profile, { null })?.getOrElse(pmContainer.mod, { null }) != null

fun ProfileModDifficultyMap.containsProfileModDifficulty(pmdContainer: PMDContainer): Boolean {
    val difficultyList = this.getOrElse(pmdContainer.profile, { null })
        ?.getOrElse(pmdContainer.mod, { null }) ?: return false

    return difficultyList.contains(pmdContainer.difficulty)
}
