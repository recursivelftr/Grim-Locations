package io.grimlocations.shared.ui.view.component

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.data.repo.action.ProfileModDifficultyMap
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.shared.util.extension.closeIfOpen

@ExperimentalFoundationApi
@Composable
fun PMDChooserComponent(
    map: ProfileModDifficultyMap,
    selected: PMDContainer,
    onSelect: (PMDContainer) -> Unit,
    onOpen: (List<AppWindow>) -> Unit,
    onClose: (() -> Unit)?,
) {
    val windows = remember { mutableListOf<AppWindow>() }
    val profileListWindow = remember { mutableStateOf<AppWindow?>(null) }
    val modListWindow = remember { mutableStateOf<AppWindow?>(null) }
    val difficultyListWindow = remember { mutableStateOf<AppWindow?>(null) }

    val profiles = remember(map) { map.keys.map { Pair(it.id, it.name) } }
    val mods = remember(map, selected) { map[selected.profile]!!.keys.map { Pair(it.id, it.name) } }
    val difficulties = remember(map, selected) { map[selected.profile]!![selected.mod]!!.map { Pair(it.id, it.name) } }

    ComboPopup(
        "Profile",
        items = profiles,
        emptyItemsMessage = "No Profiles",
        selected = Pair(selected.profile.id, selected.profile.name),
        width = 300.dp,
        onOpen = { p, c ->
            manageWindows(
                onOpen, windows, p, c,
                profileListWindow, modListWindow, difficultyListWindow
            )
        },
        onClose = onClose,
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

    ComboPopup(
        "Mod",
        items = mods,
        emptyItemsMessage = "No Mods",
        selected = Pair(selected.mod.id, selected.mod.name),
        width = 300.dp,
        onOpen = { p, c ->
            manageWindows(
                onOpen, windows, p, c,
                modListWindow, profileListWindow, difficultyListWindow
            )
        },
        onClose = onClose,
        onSelect = {
            val mod = map[selected.profile]!!.keys.first()
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

    ComboPopup(
        "Difficulty",
        items = difficulties,
        emptyItemsMessage = "No Difficulties",
        selected = Pair(selected.difficulty.id, selected.difficulty.name),
        width = 300.dp,
        onOpen = { p, c ->
            manageWindows(
                onOpen, windows, p, c,
                difficultyListWindow, modListWindow, profileListWindow
            )
        },
        onClose = onClose,
        onSelect = {
            val mod = map[selected.profile]!!.keys.first()
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
}

private fun manageWindows(
    onOpen: (List<AppWindow>) -> Unit,
    windows: MutableList<AppWindow>,
    p: AppWindow?,
    c: AppWindow,
    focus: MutableState<AppWindow?>,
    other1: MutableState<AppWindow?>,
    other2: MutableState<AppWindow?>,
) {
    windows.remove(p)
    windows.add(c)
    focus.value = c
    other1.value?.closeIfOpen()
    other2.value?.closeIfOpen()
    onOpen(windows.toList()) //returns a new immutable list, must do as last line of this function
}
