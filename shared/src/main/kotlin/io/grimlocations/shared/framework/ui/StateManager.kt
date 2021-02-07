package io.grimlocations.shared.framework.ui

import io.grimlocations.shared.framework.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KClass

interface StateManager<T: Repository> {
    val repository: T
    val mutableStateFlows: MutableMap<KClass<out State>, MutableStateFlow<out State?>>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : State> StateManager<*>.getMutableFlow(): MutableStateFlow<T> {
    return mutableStateFlows.getValue(T::class) as MutableStateFlow<T>
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : State> StateManager<*>.getState(): T {
    return (mutableStateFlows.getValue(T::class) as MutableStateFlow<T>).value
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : State> StateManager<*>.setState(value: T) {
    (mutableStateFlows.getValue(T::class) as MutableStateFlow<T>).value = value
}

