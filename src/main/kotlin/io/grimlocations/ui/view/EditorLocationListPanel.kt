package io.grimlocations.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.data.dto.RESERVED_PROFILES
import io.grimlocations.framework.util.extension.isSequential
import io.grimlocations.ui.view.component.LocationListComponent
import io.grimlocations.ui.view.component.PMDChooserComponent
import io.grimlocations.ui.viewmodel.EditorViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager

private val listHeight = 400.dp
private val arrowButtonSize = 50.dp
private val rowHeight = 50.dp
private val rowWidth = 550.dp
private val horizontalSpacerWidth = 10.dp
private val verticalSpacerHeight = 20.dp

private var previousPMDLeft: PMDContainer? = null
private var previousPMDRight: PMDContainer? = null

private lateinit var stateVerticalLeft: LazyListState
private lateinit var stateVerticalRight: LazyListState

private val logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@Composable
fun EditorLocationListPanel(
    state: EditorState,
    vm: EditorViewModel,
    onOpen: (AppWindow?, AppWindow) -> Unit,
    onClose: (() -> Unit),
    onOpenDisabledOverlayPopup: (AppWindow) -> Unit,
    onCloseDisabledOverlayPopup: (AppWindow) -> Unit,
) {
    with(state) {
        val isLeftArrowDisabled = isArrowLeftRightDisabled(
            primaryPMD = selectedPMDLeft,
            otherPMD = selectedPMDRight,
            primarySelectedLocations = selectedLocationsLeft,
            otherLocations = locationsRight,
        )
        val isRightArrowDisabled = isArrowLeftRightDisabled(
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
                Row {
                    PMDChooserComponent(
                        map = profileMap,
                        selected = selectedPMDLeft,
                        onOpen = onOpen,
                        onClose = onClose,
                        onSelect = { pmdContainer ->
                            vm.selectPMDLeft(pmdContainer)
                        }
                    )
                }
                Spacer(Modifier.height(verticalSpacerHeight))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(listHeight)
                ) {
                    Column {
                        SelectRangeButton(
                            locations = locationsLeft,
                            toggled = vm.isLeftMultiSelect,
                            onClick = {
                                vm.isLeftMultiSelect = it
                            }
                        )
                        EditButton(
                            pmdContainer = selectedPMDLeft,
                            selected = selectedLocationsLeft,
                            onClick = {
                                vm.editLocationLeft(
                                    location = it,
                                    onOpenPopup = onOpenDisabledOverlayPopup,
                                    onClosePopup = onCloseDisabledOverlayPopup,
                                )
                            }
                        )
                        DeleteButton(
                            pmdContainer = selectedPMDLeft,
                            locations = locationsLeft,
                            selected = selectedLocationsLeft,
                            onClick = {
                                vm.deleteSelectedLeft()
                            },
                        )
                    }
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    LocationListComponent(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        locations = locationsLeft,
                        isMultiSelect = vm.isLeftMultiSelect,
                        selectedLocations = selectedLocationsLeft,
                        onSelectLocations = { locs ->
                            vm.selectLocationsLeft(locs)
                        },
                        stateVertical = stateVerticalLeft
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
                Row {
                    PMDChooserComponent(
                        map = profileMap,
                        selected = selectedPMDRight,
                        onOpen = onOpen,
                        onClose = onClose,
                        onSelect = { pmdContainer ->
                            vm.selectPMDRight(pmdContainer)
                        }
                    )
                }
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
                    LocationListComponent(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        locations = locationsRight,
                        isMultiSelect = vm.isRightMultiSelect,
                        selectedLocations = selectedLocationsRight,
                        onSelectLocations = { locs ->
                            vm.selectLocationsRight(locs)
                        },
                        stateVertical = stateVerticalRight
                    )
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    Column {
                        SelectRangeButton(
                            locations = locationsRight,
                            toggled = vm.isRightMultiSelect,
                            onClick = {
                                vm.isRightMultiSelect = it
                            }
                        )
                        EditButton(
                            pmdContainer = selectedPMDRight,
                            selected = selectedLocationsRight,
                            onClick = {
                                vm.editLocationRight(
                                    location = it,
                                    onOpenPopup = onOpenDisabledOverlayPopup,
                                    onClosePopup = onCloseDisabledOverlayPopup,
                                )
                            }
                        )
                        DeleteButton(
                            pmdContainer = selectedPMDRight,
                            locations = locationsRight,
                            selected = selectedLocationsRight,
                            onClick = {
                                vm.deleteSelectedRight()
                            },
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

@Composable
private fun SelectRangeButton(
    locations: Set<LocationDTO>,
    toggled: Boolean,
    onClick: (Boolean) -> Unit
) {
    val disabled = locations.isEmpty()
    val tint = when {
        disabled -> Color.DarkGray
        toggled -> MaterialTheme.colors.primary
        else -> Color.White
    }

    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = {
            onClick(!toggled)
        },
    ) {
        Icon(
            Icons.Default.List,
            "Select Multiple",
            tint = tint,
        )
    }
}

private fun isArrowLeftRightDisabled(
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