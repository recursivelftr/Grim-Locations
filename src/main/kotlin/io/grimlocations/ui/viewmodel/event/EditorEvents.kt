package io.grimlocations.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.ui.GLStateManager
import io.grimlocations.ui.view.component.legacyOpenOkCancelPopup
import io.grimlocations.ui.view.openEditLocationPopup
import io.grimlocations.ui.viewmodel.EditorViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.*

fun EditorViewModel.loadCharacterProfiles(
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
) {
    viewModelScope.launch {
        stateManager.loadCharacterProfiles()
        withContext(Dispatchers.Main) {
            legacyOpenOkCancelPopup(
                "Character profiles successfully loaded.",
                onOpen = onOpenPopup,
                onOkClicked = {
                    onClosePopup(it)
                    it.closeIfOpen()
                },
            )
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun EditorViewModel.editLocationLeft(
    location: LocationDTO,
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
) {
    editLocation(
        location = location,
        onOpenPopup = onOpenPopup,
        onClosePopup = onClosePopup,
        updateLocation = GLStateManager::updateLocationLeft
    )
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun EditorViewModel.editLocationRight(
    location: LocationDTO,
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
) {
    editLocation(
        location = location,
        onOpenPopup = onOpenPopup,
        onClosePopup = onClosePopup,
        updateLocation = GLStateManager::updateLocationRight
    )
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
private fun EditorViewModel.editLocation(
    location: LocationDTO,
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
    updateLocation: suspend GLStateManager.(LocationDTO) -> Unit,
) {
    viewModelScope.launch {
        withContext(Dispatchers.Main) {
            openEditLocationPopup(
                location = location,
                onOpen = onOpenPopup,
                onCancelClicked = {
                    onClosePopup(it)
                },
                onOkClicked = { win, loc ->
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            stateManager.updateLocation(loc)
                        }
                    }
                    onClosePopup(win)
                },
            )
        }
    }
}

fun EditorViewModel.reloadState() {
    viewModelScope.launch {
        stateManager.reloadEditorState()
    }
}

suspend fun EditorViewModel.startGDProcessCheckLoop() {
    while (true) {
        if (stateManager.updateIfGDRunning()) {
            stateManager.checkIfLocationsFileChangedAndLoadLocation()
        }
        delay(1000)
    }
}

fun EditorViewModel.selectLocationsLeft(loc: Set<LocationDTO>) {
    viewModelScope.launch {
        stateManager.selectLocationsLeft(loc)
    }
}

fun EditorViewModel.selectLocationsRight(loc: Set<LocationDTO>) {
    viewModelScope.launch {
        stateManager.selectLocationsRight(loc)
    }
}

//fun EditorViewModel.deselectLocationsLeft(loc: Set<LocationDTO>) {
//    viewModelScope.launch {
//        stateManager.deselectLocationsLeft(loc)
//    }
//}
//
//fun EditorViewModel.deselectLocationsRight(loc: Set<LocationDTO>) {
//    viewModelScope.launch {
//        stateManager.deselectLocationsRight(loc)
//    }
//}

fun EditorViewModel.selectPMDLeft(pmd: PMDContainer) {
    viewModelScope.launch {
        stateManager.selectPMDLeft(pmd)
    }
}

fun EditorViewModel.selectPMDRight(pmd: PMDContainer) {
    viewModelScope.launch {
        stateManager.selectPMDRight(pmd)
    }
}

fun EditorViewModel.copyLeftSelectedToRight() {
    viewModelScope.launch {
        stateManager.copyLeftSelectedToRight()
    }
}

fun EditorViewModel.copyRightSelectedToLeft() {
    viewModelScope.launch {
        stateManager.copyRightSelectedToLeft()
    }
}

fun EditorViewModel.moveSelectedLeftUp() {
    viewModelScope.launch {
        stateManager.moveSelectedLeftUp()
    }
}

fun EditorViewModel.moveSelectedLeftDown() {
    viewModelScope.launch {
        stateManager.moveSelectedLeftDown()
    }
}

fun EditorViewModel.moveSelectedRightUp() {
    viewModelScope.launch {
        stateManager.moveSelectedRightUp()
    }
}

fun EditorViewModel.moveSelectedRightDown() {
    viewModelScope.launch {
        stateManager.moveSelectedRightDown()
    }
}

fun EditorViewModel.deleteSelectedLeft() {
    viewModelScope.launch {
        stateManager.deleteSelectedLeft()
    }
}

fun EditorViewModel.deleteSelectedRight() {
    viewModelScope.launch {
        stateManager.deleteSelectedRight()
    }
}