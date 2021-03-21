package io.grimlocations.ui.viewmodel.state

import io.grimlocations.framework.ui.State

enum class PropertiesStateError {
    GRIM_INTERNALS_NOT_FOUND
}

enum class PropertiesStateWarning {
    NO_CHARACTERS_FOUND
}

data class PropertiesState(
    val savePath: String? = null,
    val installPath: String? = null,
    val errors: Set<PropertiesStateError> = emptySet(),
    val warnings: Set<PropertiesStateWarning> = emptySet()
) : State {
    override fun equals(other: Any?): Boolean {
        return this === other
    }
}