package io.grimlocations.ui

import io.grimlocations.data.repo.SqliteRepository
import io.grimlocations.framework.ui.State
import io.grimlocations.framework.ui.StateManager
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.ActiveChooserState
import io.grimlocations.ui.viewmodel.state.LoadLocationsState
import io.grimlocations.ui.viewmodel.state.PropertiesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

typealias ViewStateManager<S> = GLStateManager

class GLStateManager(override val repository: SqliteRepository) : StateManager<SqliteRepository> {
    override val mutableStateFlows: MutableMap<KClass<out State>, MutableStateFlow<out State?>> = mutableMapOf(
        PropertiesState::class to MutableStateFlow<PropertiesState?>(null),
        ActiveChooserState::class to MutableStateFlow<ActiveChooserState?>(null),
        EditorState::class to MutableStateFlow<EditorState?>(null),
        LoadLocationsState::class to MutableStateFlow<LoadLocationsState?>(null),
    )
}