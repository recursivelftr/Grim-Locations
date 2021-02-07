package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.ui.viewmodel.LauncherEditorViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.updateShowPropertiesDialog
import kotlinx.coroutines.launch

fun LauncherEditorViewModel.updateShowPropertiesDialog(value: Boolean) {
    viewModelScope.launch {
        stateManager.updateShowPropertiesDialog(value)
    }
}