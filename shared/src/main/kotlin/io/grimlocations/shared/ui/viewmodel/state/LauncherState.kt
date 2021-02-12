package io.grimlocations.shared.ui.viewmodel.state

import io.grimlocations.shared.data.dto.ProfileDTO
import io.grimlocations.shared.framework.ui.State

data class LauncherState(
    val showPropertiesDialog: Boolean,
    val profiles: List<ProfileDTO>
): State