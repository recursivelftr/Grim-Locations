package io.grimlocations.shared.ui.viewmodel.event

import androidx.compose.desktop.AppWindow
import io.grimlocations.shared.ui.view.component.openOkCancelPopup
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.loadCharacterProfiles
import io.grimlocations.shared.ui.viewmodel.reducer.loadEditorState
import io.grimlocations.shared.ui.viewmodel.reducer.reloadEditorState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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