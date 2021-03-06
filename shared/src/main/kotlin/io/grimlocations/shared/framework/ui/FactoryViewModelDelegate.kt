package io.grimlocations.shared.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.reflect.KProperty

fun <T : ViewModelAlias> factoryViewModel(vmCreator: () -> T): FactoryViewModelDelegate<T> {
    return FactoryViewModelDelegate(vmCreator)
}

@Composable
inline fun <reified T: ViewModelAlias> getFactoryViewModel(): T = LocalViewModel.current.let { remember { it.get() } }

class FactoryViewModelDelegate<T : ViewModelAlias>(val vmCreator: () -> T) : ViewModelDelegate<T> {
    override val value: T
        get() = vmCreator().apply { loadState() }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}