package io.grimlocations.shared.ui.view.component

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.data.dto.LocationDTO
import io.grimlocations.shared.data.dto.RESERVED_PROFILES
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.event.selectLocationsLeft
import io.grimlocations.shared.ui.viewmodel.event.selectLocationsRight
import io.grimlocations.shared.ui.viewmodel.event.selectPMDLeft
import io.grimlocations.shared.ui.viewmodel.event.selectPMDRight
import io.grimlocations.shared.ui.viewmodel.state.EditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer

private val listHeight = 400.dp
private val arrowButtonSize = 50.dp
private val rowHeight = 50.dp
private val rowWidth = 550.dp
private val horizontalSpacerWidth = 10.dp
private val verticalSpacerHeight = 20.dp

@ExperimentalFoundationApi
@Composable
fun LocationListPanelComponent(
    state: EditorState,
    vm: EditorViewModel,
    onOpen: (AppWindow?, AppWindow) -> Unit,
    onClose: (() -> Unit),
) {
    with(state) {
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
                    LocationListComponent(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        locations = locationsLeft,
                        selectedLocations = selectedLocationsLeft,
                        onSelectLocations = { locs ->
                            vm.selectLocationsLeft(locs)
                        }
                    )
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    ArrowButton(true, isLeftArrowDisabled) {

                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row() {
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
                    ArrowButton(false, isRightArrowDisabled) {

                    }
                    Spacer(Modifier.width(horizontalSpacerWidth))
                    LocationListComponent(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        locations = locationsRight,
                        selectedLocations = selectedLocationsRight,
                        onSelectLocations = { locs ->
                            vm.selectLocationsRight(locs)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArrowButton(isLeft: Boolean, disabled: Boolean, onClick: () -> Unit) {
    IconButton(
        modifier = Modifier.size(arrowButtonSize),
        enabled = !disabled,
        onClick = onClick,
    ) {
        Icon(
            if (isLeft) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
            "Copy",
            tint = if (disabled) Color.DarkGray else MaterialTheme.colors.primary
        )
    }
}

private fun SelectRangeButton(onClick: () -> Unit) {

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

    primarySelectedLocations.forEach {
        if(otherLocations.contains(it))
            return true
    }

    return false
}