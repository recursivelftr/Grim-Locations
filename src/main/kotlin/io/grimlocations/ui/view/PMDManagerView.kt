package io.grimlocations.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.framework.data.dto.NameDTO
import io.grimlocations.framework.data.dto.UserCreatedNameDTO
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.framework.util.extension.isSequential
import io.grimlocations.ui.view.component.NameDTOListComponent
import io.grimlocations.ui.view.component.SelectionMode
import io.grimlocations.ui.view.component.openEditNameDTOPopup
import io.grimlocations.ui.viewmodel.PMDManagerViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.ui.viewmodel.state.PMDManagerStatePopups.*
import io.grimlocations.ui.viewmodel.state.container.PMContainer
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger()

private val rowHeight = 50.dp
private val rowWidth = 550.dp
private val horizontalSpacerWidth = 10.dp
private val verticalSpacerHeight = 20.dp

enum class PMDManagerFocus {
    NONE, PROFILE_LIST, MOD_LIST, DIFFICULTY_LIST
}

object PMDManagerFocusManager {
    var selectionMode = SelectionMode.SINGLE
    var currentFocus = PMDManagerFocus.NONE
    var ctrlAActionProfiles: (() -> Unit)? = null
    var ctrlAActionMods: (() -> Unit)? = null
    var ctrlAActionDifficulties: (() -> Unit)? = null
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun PMDManagerView(
    vm: PMDManagerViewModel = getFactoryViewModel(),
) = View(vm) { state ->

    PMDManagerFocusManager.ctrlAActionProfiles = {
        vm.selectProfiles(state.profileMap.keys)
    }

    PMDManagerFocusManager.ctrlAActionMods = {
        state.selectedProfiles.singleOrNull()?.also {
            vm.selectMods(state.profileMap[it]!!.keys)
        }
    }

    PMDManagerFocusManager.ctrlAActionDifficulties = {
        state.selectedProfiles.singleOrNull()?.also { profile ->
            state.selectedMods.singleOrNull()?.also { mod ->
                vm.selectDifficulties(state.profileMap[profile]!![mod]!!.toSet())
            }
        }
    }

    when (state.popupOpen) {
        EDIT_PROFILE -> openEditNameDTOPopup(
            dto = state.selectedProfiles.single(),
            onOkClicked = vm::editProfileAndClosePopup,
            onCancelClicked = { vm.setPopupState(NONE) }
        )
        EDIT_MOD -> openEditNameDTOPopup(
            dto = state.selectedMods.single(),
            onOkClicked = { name, mod ->
                vm.editMod(
                    name, PMContainer(
                        profile = state.selectedProfiles.single(),
                        mod = mod
                    )
                )
            },
            onCancelClicked = { vm.setPopupState(NONE) }
        )
        EDIT_DIFFICULTY -> openEditNameDTOPopup(
            dto = state.selectedDifficulties.single(),
            onOkClicked = { name, difficulty ->
                vm.editDifficulty(
                    name, PMDContainer(
                        profile = state.selectedProfiles.single(),
                        mod = state.selectedMods.single(),
                        difficulty = difficulty
                    )
                )
            },
            onCancelClicked = { vm.setPopupState(NONE) }
        )
        DELETE_PROFILE -> Unit
        DELETE_MOD -> Unit
        DELETE_DIFFICULTY -> Unit
        NONE -> Unit
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NameDTOListComponent(
                    dtos = state.profiles,
                    selectedDTOS = state.selectedProfiles,
                    onSelectDTOS = vm::selectProfiles,
                    rowHeight = rowHeight,
                    rowWidth = rowWidth,
                    getSelectionMode = PMDManagerFocusManager::selectionMode,
                    captureFocus = { PMDManagerFocusManager.currentFocus = PMDManagerFocus.PROFILE_LIST }
                )
                EditButton(
                    selected = state.selectedProfiles,
                    onClick = { vm.setPopupState(EDIT_PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun EditButton(
    selected: Set<UserCreatedNameDTO>,
    onClick: (UserCreatedNameDTO) -> Unit
) {
    val disabled = selected.size != 1 || !selected.single().isUserCreated

    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = {
            try {
                onClick(selected.single())
            } catch (e: Exception) {
                logger.error("Could not open location edit popup.", e)
            }
        },
    ) {
        Icon(
            Icons.Default.Edit,
            "Edit",
            tint = if (disabled) Color.DarkGray else Color.White
        )
    }

}

@Composable
private fun DeleteButton(
    dtos: Set<NameDTO>,
    selected: Set<NameDTO>,
    onClick: () -> Unit
) {
    val disabled = selected.isEmpty() || dtos.isEmpty()

    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = onClick,
    ) {
        Icon(
            Icons.Default.Delete,
            "Delete",
            tint = if (disabled) Color.DarkGray else Color.White
        )
    }
}

@Composable
private fun ArrowUpButton(
    dtos: Set<NameDTO>,
    selected: Set<NameDTO>,
    onClick: () -> Unit
) {
    val disabled = selected.isEmpty() || dtos.isEmpty() ||
            selected.contains(dtos.first()) ||
            !selected.map { dtos.indexOf(it) }.isSequential(sortFirst = true)

    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = onClick,
    ) {
        Icon(
            Icons.Default.KeyboardArrowUp,
            "Move Up",
            tint = if (disabled) Color.DarkGray else MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun ArrowDownButton(
    dtos: Set<NameDTO>,
    selected: Set<NameDTO>,
    onClick: () -> Unit
) {
    val disabled = selected.isEmpty() || dtos.isEmpty() ||
            selected.contains(dtos.last()) ||
            !selected.map { dtos.indexOf(it) }.isSequential(sortFirst = true)


    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = onClick,
    ) {
        Icon(
            Icons.Default.KeyboardArrowDown,
            "Move Down",
            tint = if (disabled) Color.DarkGray else MaterialTheme.colors.primary
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun openPMDManagerView(
    onClose: (() -> Unit),
) {

    val dialogState =
        rememberDialogState(size = WindowSize(1500.dp, 950.dp), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        title = "Grim Locations",
        state = dialogState,
        onCloseRequest = onClose,
        onPreviewKeyEvent = {
            when {
                (it.isCtrlPressed && it.key == Key.A) -> {
                    when (PMDManagerFocusManager.currentFocus) {
                        PMDManagerFocus.PROFILE_LIST -> PMDManagerFocusManager.ctrlAActionProfiles?.invoke()
                        PMDManagerFocus.MOD_LIST -> PMDManagerFocusManager.ctrlAActionMods?.invoke()
                        PMDManagerFocus.DIFFICULTY_LIST -> PMDManagerFocusManager.ctrlAActionDifficulties?.invoke()
                        PMDManagerFocus.NONE -> Unit
                    }
                }
                (it.isShiftPressed) -> {
                    PMDManagerFocusManager.selectionMode = SelectionMode.RANGE
                }
                (it.isCtrlPressed) -> {
                    PMDManagerFocusManager.selectionMode = SelectionMode.MULTIPLE
                }
                else -> {
                    PMDManagerFocusManager.selectionMode = SelectionMode.SINGLE
                }
            }
            true
        },
    ) {
        GrimLocationsTheme {
            PMDManagerView()
        }
    }
}