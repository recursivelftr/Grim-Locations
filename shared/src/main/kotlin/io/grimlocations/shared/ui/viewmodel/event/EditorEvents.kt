package io.grimlocations.shared.ui.viewmodel.event

import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.reducer.loadCharacterProfiles
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.launch

fun EditorViewModel.loadCharacterProfiles(
    selected: Pair<PMDContainer, PMDContainer>
) {
    viewModelScope.launch {
        stateManager.loadCharacterProfiles(selected)
    }
}