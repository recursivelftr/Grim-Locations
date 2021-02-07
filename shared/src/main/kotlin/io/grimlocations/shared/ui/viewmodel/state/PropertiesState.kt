package io.grimlocations.shared.ui.viewmodel.state

import io.grimlocations.shared.framework.ui.State

data class PropertiesState(
    val savePath: String? = null,
    val installPath: String? = null
): State