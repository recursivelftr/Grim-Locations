package io.grimlocations.shared.framework.ui

import androidx.compose.runtime.Composable
import kotlin.reflect.KProperty

fun <T : ViewModelAlias> lazyViewModel(vmCreator: () -> T): LazyViewModelDelegate<T> {
    return LazyViewModelDelegate(vmCreator)
}

@Composable
inline fun <reified T: ViewModelAlias> getLazyViewModel(): T = LocalViewModel.current.get()

class LazyViewModelDelegate<T : ViewModelAlias>(vmCreator: () -> T) : ViewModelDelegate<T> {
    private lateinit var vm: T
    private var vmc: (() -> T)? = vmCreator

    override val value: T
        get() {
            if (this::vm.isInitialized) {
                return vm
            }
            vm = vmc!!.invoke()
            vmc = null
            vm.loadState()
            return vm
        }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}