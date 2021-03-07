package io.grimlocations.shared.framework.util

import kotlin.reflect.KProperty

fun <T> assignOnce() = AssignOnceDelegate<T>()

class AssignOnceDelegate<T> {
    private var value: T? = null
    var initialized = false
        private set

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        value?.let {
            return it
        } ?: error("The value of the delegate has not been assigned.")
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, item: T) {
        if(value != null)
            error("The value of the delegate has already been assigned.")

        if(item == null)
            error("The AssignOnceDelegate does not allow null values.")

        value = item
        initialized = true
    }
}