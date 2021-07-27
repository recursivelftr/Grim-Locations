package io.grimlocations.ui.view.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.grimlocations.data.dto.ProfileModDifficultyMap
import io.grimlocations.data.dto.RESERVED_NO_DIFFICULTIES_INDICATOR
import io.grimlocations.data.dto.RESERVED_NO_MODS_INDICATOR
import io.grimlocations.data.dto.isReserved
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val textBoxWidth = 300.dp

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun PMDChooserComponent(
    map: ProfileModDifficultyMap,
    selected: PMDContainer,
    onSelect: (PMDContainer) -> Unit,
    spacerHeight: Dp = 15.dp,
    controlsOnLeft: Boolean = false,
) {
    val primaryColor = MaterialTheme.colors.primary
    val profiles = map.keys.map { Triple(it.id, it.name, if (it.isReserved) primaryColor else null) }
    val mods = map[selected.profile]!!.keys.map { Triple(it.id, it.name, null) }
    val difficulties = map[selected.profile]!![selected.mod]!!.map { Triple(it.id, it.name, null) }

    val selectedProfile = selected.profile
    val selectedMod =
        if (selected.mod == RESERVED_NO_MODS_INDICATOR)
            selected.mod.copy(name = "None")
        else
            selected.mod
    val selectedDifficulty =
        if (selected.difficulty == RESERVED_NO_DIFFICULTIES_INDICATOR)
            selected.difficulty.copy(name = "None")
        else
            selected.difficulty

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComboPopup(
            "Profile",
            items = profiles,
            emptyItemsMessage = "No Profiles",
            selected = Triple(
                selectedProfile.id,
                selectedProfile.name,
                if (selectedProfile.isReserved) primaryColor else null
            ),
            width = textBoxWidth,
            controlOnLeft = controlsOnLeft,
            onSelect = {
                val profile = map.keys.find { item -> item.id == it.first }!!
                val mod = map[profile]!!.keys.first()
                val difficulty = map[profile]!![mod]!!.first()

                onSelect(
                    PMDContainer(
                        profile = profile,
                        mod = mod,
                        difficulty = difficulty
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(spacerHeight))
        ComboPopup(
            "Mod",
            items = mods,
            emptyItemsMessage = "No Mods",
            disabled = selected.mod == RESERVED_NO_MODS_INDICATOR,
            selected = Triple(selectedMod.id, selectedMod.name, null),
            width = textBoxWidth,
            controlOnLeft = controlsOnLeft,
            onSelect = {
                val mod = map[selected.profile]!!.keys.find { item -> item.id == it.first }!!
                val difficulty = map[selected.profile]!![mod]!!.first()

                onSelect(
                    PMDContainer(
                        profile = selected.profile,
                        mod = mod,
                        difficulty = difficulty
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(spacerHeight))
        ComboPopup(
            "Difficulty",
            items = difficulties,
            emptyItemsMessage = "No Difficulties",
            disabled = selected.difficulty == RESERVED_NO_DIFFICULTIES_INDICATOR,
            selected = Triple(selectedDifficulty.id, selectedDifficulty.name, null),
            width = textBoxWidth,
            controlOnLeft = controlsOnLeft,
            onSelect = {
                val difficulty = map[selected.profile]!![selected.mod]!!.find { item -> item.id == it.first }!!

                onSelect(
                    PMDContainer(
                        profile = selected.profile,
                        mod = selected.mod,
                        difficulty = difficulty
                    )
                )
            }
        )
    }
}
