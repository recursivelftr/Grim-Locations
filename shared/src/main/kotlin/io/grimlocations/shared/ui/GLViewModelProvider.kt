package io.grimlocations.shared.ui

import io.grimlocations.shared.framework.ui.LazyViewModel
import io.grimlocations.shared.framework.ui.ViewModelProvider
import io.grimlocations.shared.framework.ui.lazyViewModel
import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.ui.viewmodel.LauncherEditorViewModel
import io.grimlocations.shared.ui.viewmodel.PropertiesViewModel
import kotlin.reflect.KClass

class GLViewModelProvider(stateManager: GLStateManager): ViewModelProvider {
    override val viewModelMap: Map<KClass<out ViewModel<*, *>>, LazyViewModel<*>> = mapOf(
        PropertiesViewModel::class to lazyViewModel { PropertiesViewModel(stateManager) },
        LauncherEditorViewModel::class to lazyViewModel { LauncherEditorViewModel(stateManager) }
    )
}