package io.grimlocations.shared.framework.ui

import androidx.compose.runtime.compositionLocalOf
import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import kotlin.reflect.KClass

val LocalViewModel = compositionLocalOf<ViewModelProvider>()

interface ViewModelProvider {
    val viewModelMap: Map<KClass<out ViewModel<*, *>>, LazyViewModel<*>>
}

inline fun <reified T: ViewModel<*, *>> ViewModelProvider.get(): T = viewModelMap.getValue(T::class).value as T