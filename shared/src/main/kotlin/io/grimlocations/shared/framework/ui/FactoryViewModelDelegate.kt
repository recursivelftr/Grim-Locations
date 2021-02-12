package io.grimlocations.shared.framework.ui

import kotlin.reflect.KProperty

fun <T : ViewModelAlias> factoryViewModel(vmCreator: () -> T): FactoryViewModelDelegate<T> {
    return FactoryViewModelDelegate(vmCreator)
}

class FactoryViewModelDelegate<T : ViewModelAlias>(val vmCreator: () -> T) : ViewModelDelegate<T> {
    override val value: T
        get() = vmCreator().apply { loadState() }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}