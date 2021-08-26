package io.grimlocations.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.framework.data.dto.NameDTO
import io.grimlocations.framework.data.dto.UserCreatedNameDTO
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.framework.util.extension.isSequential
import io.grimlocations.ui.view.component.*
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
private val buttonSpacerHeight = 5.dp
private val buttonSeparatorHeight = 75.dp
private val horizontalSeparatorWidth = 35.dp

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

private const val PROFILE_DUPLICATE_MESSAGE = "A profile already exists with that name"
private const val MOD_DUPLICATE_MESSAGE = "A profile cannot have duplicate mods"
private const val DIFFICULTY_DUPLICATE_MESSAGE = "A mod cannot have duplicate difficulties"

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun PMDManagerView(
    vm: PMDManagerViewModel = getFactoryViewModel(),
    onClose: (() -> Unit),
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

    val closePopup = { vm.setPopupState(NONE) }

    when (state.popupOpen) {
        CREATE_PROFILE -> openCreateNameDTOPopup(
            title = "Create Profile",
            dtos = state.profiles,
            duplicateNameMessage = PROFILE_DUPLICATE_MESSAGE,
            onOkClicked = vm::createProfileAndClosePopup,
            onCancelClicked = closePopup,
        )
        CREATE_MOD -> openCreateNameDTOPopup(
            title = "Create Mod",
            dtos = state.mods,
            duplicateNameMessage = MOD_DUPLICATE_MESSAGE,
            onOkClicked = { name ->
                vm.createModAndClosePopup(
                    name, state.selectedProfiles.single()
                )
            },
            onCancelClicked = closePopup,
        )
        CREATE_DIFFICULTY -> openCreateNameDTOPopup(
            title = "Create Difficulty",
            dtos = state.difficulties,
            duplicateNameMessage = DIFFICULTY_DUPLICATE_MESSAGE,
            onOkClicked = { name ->
                vm.createDifficultyAndClosePopup(
                    name, PMContainer(
                        profile = state.selectedProfiles.single(),
                        mod = state.selectedMods.single(),
                    )
                )
            },
            onCancelClicked = closePopup,
        )
        EDIT_PROFILE -> {
            openEditNameDTOPopup(
                title = "Edit Profile",
                dto = state.selectedProfiles.single(),
                duplicateNameMessage = PROFILE_DUPLICATE_MESSAGE,
                otherDtos = state.profiles.filter { it != state.selectedProfiles.single() }.toSet(),
                onOkClicked = vm::editProfileAndClosePopup,
                onCancelClicked = closePopup,
            )
        }
        EDIT_MOD -> {
            openEditNameDTOPopup(
                title = "Edit Mod",
                dto = state.selectedMods.single(),
                duplicateNameMessage = MOD_DUPLICATE_MESSAGE,
                otherDtos = state.mods.filter { it != state.selectedMods.single() }.toSet(),
                onOkClicked = { name, mod ->
                    vm.editModAndClosePopup(
                        name, PMContainer(
                            profile = state.selectedProfiles.single(),
                            mod = mod
                        )
                    )
                },
                onCancelClicked = closePopup,
            )
        }
        EDIT_DIFFICULTY -> {
            openEditNameDTOPopup(
                title = "Edit Difficulty",
                dto = state.selectedDifficulties.single(),
                duplicateNameMessage = DIFFICULTY_DUPLICATE_MESSAGE,
                otherDtos = state.difficulties.filter { it != state.selectedDifficulties.single() }.toSet(),
                onOkClicked = { name, difficulty ->
                    vm.editDifficultyAndClosePopup(
                        name, PMDContainer(
                            profile = state.selectedProfiles.single(),
                            mod = state.selectedMods.single(),
                            difficulty = difficulty
                        )
                    )
                },
                onCancelClicked = closePopup,
            )
        }
        DELETE_PROFILE -> openConfirmDeletePopup(
            msgMultiple = "Are you sure you want to delete these profiles?",
            msgSingle = "Are you sure you want to delete this profile?",
            isMultiple = state.selectedProfiles.size > 1,
            onOkClicked = { vm.deleteProfiles(state.selectedProfiles) },
            onCancelClicked = closePopup,
        )
        DELETE_MOD -> openConfirmDeletePopup(
            msgMultiple = "Are you sure you want to delete these mods?",
            msgSingle = "Are you sure you want to delete this mod?",
            isMultiple = state.selectedMods.size > 1,
            onOkClicked = { vm.deleteMods(state.selectedMods, state.selectedProfiles.single()) },
            onCancelClicked = closePopup,
        )
        DELETE_DIFFICULTY -> openConfirmDeletePopup(
            msgMultiple = "Are you sure you want to delete these difficulties?",
            msgSingle = "Are you sure you want to delete this difficulty?",
            isMultiple = state.selectedDifficulties.size > 1,
            onOkClicked = {
                vm.deleteDifficulties(
                    selected = state.selectedDifficulties,
                    pmContainer = PMContainer(
                        profile = state.selectedProfiles.single(),
                        mod = state.selectedMods.single()
                    )
                )
            },
            onCancelClicked = closePopup,
        )
        NONE -> Unit
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(650.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Profiles")
                    Spacer(Modifier.height(10.dp))
                    NameDTOListComponent(
                        dtos = state.profiles,
                        selectedDTOS = state.selectedProfiles,
                        onSelectDTOS = vm::selectProfiles,
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        getSelectionMode = PMDManagerFocusManager::selectionMode,
                        captureFocus = { PMDManagerFocusManager.currentFocus = PMDManagerFocus.PROFILE_LIST },
                        noDtosMessage = "No Profiles"
                    )
                }
                Column {
                    ArrowUpButton(
                        dtos = state.profiles,
                        selected = state.selectedProfiles,
                        onClick = { vm.moveProfiles(state.selectedProfiles, moveUp = true) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    ArrowDownButton(
                        dtos = state.profiles,
                        selected = state.selectedProfiles,
                        onClick = { vm.moveProfiles(state.selectedProfiles, moveUp = false) }
                    )
                    Spacer(Modifier.height(buttonSeparatorHeight))
                    CreateButton(
                        onClick = { vm.setPopupState(CREATE_PROFILE) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    EditButton(
                        selected = state.selectedProfiles,
                        onClick = { vm.setPopupState(EDIT_PROFILE) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    DeleteButton(
                        disabled = state.profiles.isEmpty() || state.selectedProfiles.isEmpty(),
                        onClick = { vm.setPopupState(DELETE_PROFILE) }
                    )
                }
                Spacer(Modifier.width(horizontalSeparatorWidth))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mods")
                    Spacer(Modifier.height(10.dp))
                    NameDTOListComponent(
                        dtos = state.mods,
                        selectedDTOS = state.selectedMods,
                        onSelectDTOS = vm::selectMods,
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        getSelectionMode = PMDManagerFocusManager::selectionMode,
                        captureFocus = { PMDManagerFocusManager.currentFocus = PMDManagerFocus.MOD_LIST },
                        noDtosMessage = "No Mods"
                    )
                }
                Column() {
                    ArrowUpButton(
                        dtos = state.mods,
                        selected = state.selectedMods,
                        onClick = { vm.moveMods(state.selectedMods, state.selectedProfiles.single(), moveUp = true) },
                        additionalDisabledCheck = state.selectedProfiles.size != 1
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    ArrowDownButton(
                        dtos = state.mods,
                        selected = state.selectedMods,
                        onClick = { vm.moveMods(state.selectedMods, state.selectedProfiles.single(), moveUp = false) },
                        additionalDisabledCheck = state.selectedProfiles.size != 1
                    )
                    Spacer(Modifier.height(buttonSeparatorHeight))
                    CreateButton(
                        disabled = state.selectedProfiles.size != 1,
                        onClick = { vm.setPopupState(CREATE_MOD) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    EditButton(
                        selected = state.selectedMods,
                        onClick = { vm.setPopupState(EDIT_MOD) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    DeleteButton(
                        disabled = state.mods.isEmpty() || state.selectedMods.isEmpty() || state.selectedProfiles.size != 1,
                        onClick = { vm.setPopupState(DELETE_MOD) }
                    )
                }
                Spacer(Modifier.width(horizontalSeparatorWidth))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Difficulties")
                    Spacer(Modifier.height(10.dp))
                    NameDTOListComponent(
                        dtos = state.difficulties,
                        selectedDTOS = state.selectedDifficulties,
                        onSelectDTOS = vm::selectDifficulties,
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        getSelectionMode = PMDManagerFocusManager::selectionMode,
                        captureFocus = { PMDManagerFocusManager.currentFocus = PMDManagerFocus.DIFFICULTY_LIST },
                        noDtosMessage = "No Difficulties"
                    )
                }
                Column() {
                    ArrowUpButton(
                        dtos = state.difficulties,
                        selected = state.selectedDifficulties,
                        onClick = {
                            vm.moveDifficulties(
                                state.selectedDifficulties,
                                PMContainer(state.selectedProfiles.single(), state.selectedMods.single()),
                                moveUp = true
                            )
                        },
                        additionalDisabledCheck = state.selectedProfiles.size != 1 || state.selectedMods.size != 1
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    ArrowDownButton(
                        dtos = state.difficulties,
                        selected = state.selectedDifficulties,
                        onClick = {
                            vm.moveDifficulties(
                                state.selectedDifficulties,
                                PMContainer(state.selectedProfiles.single(), state.selectedMods.single()),
                                moveUp = false
                            )
                        },
                        additionalDisabledCheck = state.selectedProfiles.size != 1 || state.selectedMods.size != 1
                    )
                    Spacer(Modifier.height(buttonSeparatorHeight))
                    CreateButton(
                        disabled = state.selectedMods.size != 1,
                        onClick = { vm.setPopupState(CREATE_DIFFICULTY) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    EditButton(
                        selected = state.selectedDifficulties,
                        onClick = { vm.setPopupState(EDIT_DIFFICULTY) }
                    )
                    Spacer(Modifier.height(buttonSpacerHeight))
                    DeleteButton(
                        disabled = state.difficulties.isEmpty() || state.selectedDifficulties.isEmpty() || state.selectedMods.size != 1,
                        onClick = { vm.setPopupState(DELETE_DIFFICULTY) }
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
            Row {
                Button(
                    modifier = Modifier.width(200.dp),
                    onClick = onClose
                ) {
                    Text("Back", fontSize = 15.sp)
                }
                Spacer(Modifier.width(40.dp))
            }
        }
    }
}

@Composable
private fun EditButton(
    selected: Set<UserCreatedNameDTO>,
    onClick: (UserCreatedNameDTO) -> Unit
) {
    val disabled = selected.size != 1

    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = {
            onClick(selected.single())
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
    disabled: Boolean,
    onClick: () -> Unit
) {
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
    onClick: () -> Unit,
    additionalDisabledCheck: Boolean = false,
) {
    val disabled = additionalDisabledCheck || selected.isEmpty() || dtos.size < 2 ||
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
    onClick: () -> Unit,
    additionalDisabledCheck: Boolean = false,
) {
    val disabled = additionalDisabledCheck || selected.isEmpty() || dtos.size < 2 ||
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

@Composable
private fun CreateButton(
    disabled: Boolean = false,
    onClick: () -> Unit,
) {
    IconButton(
        enabled = !disabled,
        modifier = Modifier.size(arrowButtonSize),
        onClick = onClick,
    ) {
        Icon(
            Icons.Default.AddCircle,
            "New",
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun openConfirmDeletePopup(
    msgMultiple: String,
    msgSingle: String,
    onOkClicked: () -> Unit,
    onCancelClicked: () -> Unit,
    isMultiple: Boolean
) {
    openOkCancelPopup(
        message = if (isMultiple)
            msgMultiple
        else
            msgSingle,
        onCancelClicked = onCancelClicked,
        onOkClicked = onOkClicked,
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun openPMDManagerView(
    onClose: (() -> Unit),
) {

    val dialogState =
        rememberDialogState(size = WindowSize(2000.dp, 950.dp), position = WindowPosition.Aligned(Alignment.Center))

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
            PMDManagerView(onClose = onClose)
        }
    }
}