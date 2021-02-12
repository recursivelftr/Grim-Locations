package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.updateShowPropertiesDialog
import kotlinx.coroutines.launch

fun LauncherViewModel.updateShowPropertiesDialog(value: Boolean) {
    viewModelScope.launch {
        stateManager.updateShowPropertiesDialog(value)
    }
}