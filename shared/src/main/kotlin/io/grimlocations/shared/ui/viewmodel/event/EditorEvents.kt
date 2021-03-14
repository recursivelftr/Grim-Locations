package io.grimlocations.shared.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import io.grimlocations.shared.data.dto.LocationDTO
import io.grimlocations.shared.ui.view.component.openOkCancelPopup
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.*
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.*

fun EditorViewModel.loadCharacterProfiles(
    onOpenPopup: (AppWindow) -> Unit,
    onClosePopup: (AppWindow) -> Unit,
) {
    viewModelScope.launch {
        stateManager.loadCharacterProfiles()
        withContext(Dispatchers.Main) {
            openOkCancelPopup(
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

fun EditorViewModel.reloadState() {
    viewModelScope.launch {
        stateManager.reloadEditorState()
    }
}

fun EditorViewModel.startGDProcessCheckLoop() {
    CoroutineScope(Dispatchers.Main + Job()).launch {
        while(true) {
            if(stateManager.updateIfGDRunning()) {
                stateManager.checkIfLocationsFileChangedAndLoadLocation()
            }
            delay(1000)
        }
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
        stateManager.copyLeftSelectedToRight()
    }
}