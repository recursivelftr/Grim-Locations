package io.grimlocations.shared.framework.ui

import kotlin.reflect.KProperty

fun <T : ViewModelAlias> lazyViewModel(vmCreator: () -> T): LazyViewModelDelegate<T> {
    return LazyViewModelDelegate(vmCreator)
}

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