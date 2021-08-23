package io.grimlocations.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.data.dto.RESERVED_PROFILES
import io.grimlocations.framework.util.extension.isSequential
import io.grimlocations.ui.view.component.*
import io.grimlocations.ui.viewmodel.EditorViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager

private val listHeight = 400.dp
val arrowButtonSize = 50.dp
private val rowHeight = 50.dp
private val rowWidth = 550.dp
private val horizontalSpacerWidth = 10.dp
private val verticalSpacerHeight = 20.dp

private var previousPMDLeft: PMDContainer? = null
private var previousPMDRight: PMDContainer? = null

private lateinit var stateVerticalLeft: LazyListState
private lateinit var stateVerticalRight: LazyListState

private val logger = LogManager.getLogger()

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun EditorLocationListPanel(
    state: EditorState,
    vm: EditorViewModel,
    captureFocusLeft: () -> Unit,
    captureFocusRight: () -> Unit,
    editorFocusManager: EditorFocusManager,
) {
    with(state) {

        if (isEditLocationLeftPopupOpen) {
            openEditLocationPopupLeft(this, vm)
        }

        if (isEditLocationRightPopupOpen) {
            openEditLocationPopupRight(this, vm)
        }

        if (isConfirmDeleteLeftPopupOpen) {
            openConfirmDeletePopup(
                onOkClicked = {
                    vm.deleteSelectedLeft()
                    vm.closeConfirmDeleteLeft()
                },
                onCancelClicked = vm::closeConfirmDeleteLeft,
                isMultiple = selectedLocationsLeft.size > 1
            )
        }

        if (isConfirmDeleteRightPopupOpen) {
            openConfirmDeletePopup(
                onOkClicked = {
                    vm.deleteSelectedRight()
                    vm.closeConfirmDeleteRight()
                },
                onCancelClicked = vm::closeConfirmDeleteRight,
                isMultiple = selectedLocationsRight.size > 1
            )
        }

        val isLeftArrowDisabled = isArrowDisabled(
            primaryPMD = selectedPMDLeft,
            otherPMD = selectedPMDRight,
            primarySelectedLocations = selectedLocationsLeft,
            otherLocations = locationsRight,
        )
        val isRightArrowDisabled = isArrowDisabled(
            primaryPMD = selectedPMDRight,
            otherPMD = selectedPMDLeft,
            primarySelectedLocations = selectedLocationsRight,
            otherLocations = locationsLeft,
        )
        if (previousPMDLeft != selectedPMDLeft) {
            stateVerticalLeft = LazyListState(
                0,
                0
            )
            previousPMDLeft = selectedPMDLeft
        }
        if (previousPMDRight != selectedPMDRight) {
            stateVerticalRight = LazyListState(
                0,
                0
            )
            previousPMDRight = selectedPMDRight
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.wrapContentSize()) {
                    SetToActiveButton(
                        enabled = state.activePMD != null,
                        onClick = {
                            vm.selectPMDLeft(state.activePMD!!)
                        }
                    )
                    Spacer(Modifier.width(50.dp))
                }
                Spacer(Modifier.height(5.dp))
                PMDChooserComponent(
                    map = profileMap,
                    selected = selectedPMDLeft,
                    onSelect = { pmdContainer ->
                        vm.selectPMDLeft(pmdContainer)
                    }
                )
                Spacer(Modifier.height(verticalSpacerHeight))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(listHeight)
                ) {
                    Column {
                        EditButton(
                            pmdContainer = selectedPMDLeft,
                            selected = selectedLocationsLeft,
                            onClick = {
                                vm.openEditLocationLeft()
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                        DeleteButton(
                            pmdContainer = selectedPMDLeft,
                            locations = locationsLeft,
                            selected = selectedLocationsLeft,
                            onClick = vm::openConfirmDeleteLeft,
                        )
                    }
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    NameDTOListComponent(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        dtos = locationsLeft,
                        getSelectionMode = editorFocusManager::selectionMode,
                        selectedDTOS = selectedLocationsLeft,
                        onSelectDTOS = { locs ->
                            vm.selectLocationsLeft(locs)
                        },
                        stateVertical = stateVerticalLeft,
                        captureFocus = captureFocusLeft,
                        noDtosMessage = "No Locations"
                    )
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    Column {
                        ArrowUpButton(
                            pmdContainer = selectedPMDLeft,
                            locations = locationsLeft,
                            selected = selectedLocationsLeft,
                            onClick = {
                                vm.moveSelectedLeftUp()
                            }
                        )
                        ArrowLeftRightButton(
                            isLeft = true,
                            disabled = isLeftArrowDisabled,
                            onClick = {
                                vm.copyLeftSelectedToRight()
                            }
                        )
                        ArrowDownButton(
                            pmdContainer = selectedPMDLeft,
                            locations = locationsLeft,
                            selected = selectedLocationsLeft,
                            onClick = {
                                vm.moveSelectedLeftDown()
                            }
                        )
                    }

                }
            }
            Spacer(Modifier.width(15.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.wrapContentSize()) {
                    SetToActiveButton(
                        enabled = state.activePMD != null,
                        onClick = {
                            vm.selectPMDRight(state.activePMD!!)
                        }
                    )
                    Spacer(Modifier.width(50.dp))
                }
                Spacer(Modifier.height(5.dp))
                PMDChooserComponent(
                    map = profileMap,
                    selected = selectedPMDRight,
//                    controlsOnLeft = true,
                    onSelect = { pmdContainer ->
                        vm.selectPMDRight(pmdContainer)
                    }
                )
                Spacer(Modifier.height(verticalSpacerHeight))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(listHeight)
                ) {
                    Column {
                        ArrowUpButton(
                            pmdContainer = selectedPMDRight,
                            locations = locationsRight,
                            selected = selectedLocationsRight,
                            onClick = {
                                vm.moveSelectedRightUp()
                            }
                        )
                        ArrowLeftRightButton(
                            isLeft = false,
                            disabled = isRightArrowDisabled,
                            onClick = {
                                vm.copyRightSelectedToLeft()
                            }
                        )
                        ArrowDownButton(
                            pmdContainer = selectedPMDRight,
                            locations = locationsRight,
                            selected = selectedLocationsRight,
                            onClick = {
                                vm.moveSelectedRightDown()
                            }
                        )
                    }

                    Spacer(Modifier.width(horizontalSpacerWidth))
                    NameDTOListComponent(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        dtos = locationsRight,
                        getSelectionMode = editorFocusManager::selectionMode,
                        selectedDTOS = selectedLocationsRight,
                        onSelectDTOS = { locs ->
                            vm.selectLocationsRight(locs)
                        },
                        stateVertical = stateVerticalRight,
                        captureFocus = captureFocusRight,
                        noDtosMessage = "No Locations"
                    )
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    Column {
                        EditButton(
                            pmdContainer = selectedPMDRight,
                            selected = selectedLocationsRight,
                            onClick = {
                                vm.openEditLocationRight()
                            }
                        )
                        DeleteButton(
                            pmdContainer = selectedPMDRight,
                            locations = locationsRight,
                            selected = selectedLocationsRight,
                            onClick = vm::openConfirmDeleteRight,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArrowLeftRightButton(isLeft: Boolean, disabled: Boolean, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = onClick,
    ) {
        Icon(
            if (isLeft) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
            "Copy",
            tint = if (disabled) Color.DarkGray else MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun ArrowUpButton(
    pmdContainer: PMDContainer,
    locations: Set<LocationDTO>,
    selected: Set<LocationDTO>,
    onClick: () -> Unit
) {
    val disabled = selected.isEmpty() || locations.isEmpty() ||
            selected.contains(locations.first()) ||
            !selected.map { locations.indexOf(it) }.isSequential(sortFirst = true) ||
            RESERVED_PROFILES.contains(pmdContainer.profile)

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
    pmdContainer: PMDContainer,
    locations: Set<LocationDTO>,
    selected: Set<LocationDTO>,
    onClick: () -> Unit
) {
    val disabled = selected.isEmpty() || locations.isEmpty() ||
            selected.contains(locations.last()) ||
            !selected.map { locations.indexOf(it) }.isSequential(sortFirst = true) ||
            RESERVED_PROFILES.contains(pmdContainer.profile)


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
private fun EditButton(
    pmdContainer: PMDContainer,
    selected: Set<LocationDTO>,
    onClick: (LocationDTO) -> Unit
) {
    val disabled = selected.size != 1 || RESERVED_PROFILES.contains(pmdContainer.profile)

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
    pmdContainer: PMDContainer,
    locations: Set<LocationDTO>,
    selected: Set<LocationDTO>,
    onClick: () -> Unit
) {
    val disabled = selected.isEmpty() || locations.isEmpty() || RESERVED_PROFILES.contains(pmdContainer.profile)

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

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun SetToActiveButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val tint =
        if (enabled)
            MaterialTheme.colors.primary
        else
            Color.DarkGray

    Tooltip(
        text = "Set to Active"
    ) {
        IconButton(
            enabled = enabled,
            onClick = onClick,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                Icons.Sharp.ArrowDropDown,
                "Menu",
                tint = tint,
                modifier = Modifier.size(50.dp),
            )
        }

    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun openConfirmDeletePopup(onOkClicked: () -> Unit, onCancelClicked: () -> Unit, isMultiple: Boolean) {
    openOkCancelPopup(
        message = if (isMultiple)
            "Are you sure you want to delete these locations?"
        else
            "Are you sure you want to delete this location?",
        onCancelClicked = onCancelClicked,
        onOkClicked = onOkClicked,
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun openEditLocationPopupLeft(s: EditorState, vm: EditorViewModel) {
    openEditLocationPopup(
        location = s.selectedLocationsLeft.single(),
        onCancelClicked = vm::closeEditLocationLeft,
        onOkClicked = vm::editAndCloseLocationLeft,
    )
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun openEditLocationPopupRight(s: EditorState, vm: EditorViewModel) {
    openEditLocationPopup(
        location = s.selectedLocationsRight.single(),
        onCancelClicked = vm::closeEditLocationRight,
        onOkClicked = vm::editAndCloseLocationRight,
    )
}

private fun isArrowDisabled(
    primaryPMD: PMDContainer,
    primarySelectedLocations: Set<LocationDTO>,
    otherPMD: PMDContainer,
    otherLocations: Set<LocationDTO>,
): Boolean {
    if (primaryPMD == otherPMD)
        return true

    if (RESERVED_PROFILES.contains(otherPMD.profile))
        return true

    if (primarySelectedLocations.isEmpty())
        return true

    primarySelectedLocations.forEach {
        if (otherLocations.contains(it))
            return true
    }

    return false
}