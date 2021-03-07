package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.framework.ui.viewmodel.stateFlow
import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.updateLauncherState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.launch

fun LauncherViewModel.selectPMD(selected: PMDContainer) {
    stateFlow.value?.let {
        viewModelScope.launch {
            stateManager.updateLauncherState(it.copy(selected = selected))
        }
    }
}