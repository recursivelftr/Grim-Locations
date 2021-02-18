package io.grimlocations.shared.util

import kotlin.reflect.KProperty

fun <T> assignOnce() = AssignOnceDelegate<T>()

class AssignOnceDelegate<T> {
    private var value: T? = null
    var initialized = false
        private set

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        value?.let {
            return it
        } ?: error("The value of the delegate has not been set.")
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
        if(value == null)
            error("The AssignOnceDelegate does not allow null values.")

        this.value = value
        initialized = true
    }
}