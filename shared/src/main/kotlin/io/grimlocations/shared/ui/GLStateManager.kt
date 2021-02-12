package io.grimlocations.shared.ui

import io.grimlocations.shared.data.repo.SqliteRepository
import io.grimlocations.shared.framework.ui.State
import io.grimlocations.shared.framework.ui.StateManager
import io.grimlocations.shared.ui.viewmodel.state.LauncherState
import io.grimlocations.shared.ui.viewmodel.state.PropertiesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

class GLStateManager(override val repository: SqliteRepository) : StateManager<SqliteRepository> {
    override val mutableStateFlows: MutableMap<KClass<out State>, MutableStateFlow<out State?>> = mutableMapOf(
        PropertiesState::class to MutableStateFlow<PropertiesState?>(null),
        LauncherState::class to MutableStateFlow<LauncherState?>(null)
    )
}