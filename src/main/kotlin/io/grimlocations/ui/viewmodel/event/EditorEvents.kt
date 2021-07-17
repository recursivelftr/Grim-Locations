package io.grimlocations.ui.viewmodel.event

import androidx.compose.foundation.ExperimentalFoundationApi
import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.ui.viewmodel.EditorViewModel
import io.grimlocations.ui.viewmodel.reducer.*
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun EditorViewModel.openConfirmDeleteLeft() {
    viewModelScope.launch {
        stateManager.openConfirmDeleteLeft()
    }
}

fun EditorViewModel.closeConfirmDeleteLeft() {
    viewModelScope.launch {
        stateManager.closeConfirmDeleteLeft()
    }
}

fun EditorViewModel.openConfirmDeleteRight() {
    viewModelScope.launch {
        stateManager.openConfirmDeleteRight()
    }
}

fun EditorViewModel.closeConfirmDeleteRight() {
    viewModelScope.launch {
        stateManager.closeConfirmDeleteRight()
    }
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun EditorViewModel.editAndCloseLocationLeft(loc: LocationDTO) {
    viewModelScope.launch {
        stateManager.updateAndCloseLocationLeft(loc)
    }
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun EditorViewModel.editAndCloseLocationRight(loc: LocationDTO) {
    viewModelScope.launch {
        stateManager.updateAndCloseLocationRight(loc)
    }
}

fun EditorViewModel.openEditLocationLeft() {
    viewModelScope.launch {
        stateManager.openEditLocationLeft()
    }
}

fun EditorViewModel.closeEditLocationLeft() {
    viewModelScope.launch {
        stateManager.closeEditLocationLeft()
    }
}

fun EditorViewModel.openEditLocationRight() {
    viewModelScope.launch {
        stateManager.openEditLocationRight()
    }
}

fun EditorViewModel.closeEditLocationRight() {
    viewModelScope.launch {
        stateManager.closeEditLocationRight()
    }
}

fun EditorViewModel.openPropertiesView() {
    viewModelScope.launch {
        stateManager.openPropertiesView()
    }
}

fun EditorViewModel.closePropertiesView() {
    viewModelScope.launch {
        stateManager.closePropertiesView()
    }
}

fun EditorViewModel.openLoadLocationsView() {
    viewModelScope.launch {
        stateManager.openLoadLocationsView()
    }
}

fun EditorViewModel.closeLoadLocationsView() {
    viewModelScope.launch {
        stateManager.closeLoadLocationsView()
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