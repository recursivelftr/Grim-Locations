package io.grimlocations.shared.framework.ui

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import kotlin.reflect.KProperty

private typealias ViewModelAlias = ViewModel<*, *>

fun <T : ViewModelAlias> lazyViewModel(vmCreator: () -> T): LazyViewModelDelegate<T> {
    return LazyViewModelDelegate(vmCreator)
}

interface LazyViewModel<T : ViewModelAlias> {
    val value: T
}

class LazyViewModelDelegate<T : ViewModelAlias>(vmCreator: () -> T) : LazyViewModel<T> {
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