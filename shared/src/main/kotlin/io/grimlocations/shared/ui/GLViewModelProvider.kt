package io.grimlocations.shared.ui

import io.grimlocations.shared.framework.ui.ViewModelDelegate
import io.grimlocations.shared.framework.ui.ViewModelProvider
import io.grimlocations.shared.framework.ui.factoryViewModel
import io.grimlocations.shared.framework.ui.lazyViewModel
import io.grimlocations.shared.framework.ui.viewmodel.ViewModel
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import io.grimlocations.shared.ui.viewmodel.PropertiesViewModel
import kotlin.reflect.KClass

class GLViewModelProvider(stateManager: GLStateManager): ViewModelProvider {
    override val viewModelMap: Map<KClass<out ViewModel<*, *>>, ViewModelDelegate<*>> = mapOf(
        PropertiesViewModel::class to factoryViewModel { PropertiesViewModel(stateManager) },
        LauncherViewModel::class to factoryViewModel { LauncherViewModel(stateManager) },
        EditorViewModel::class to lazyViewModel { EditorViewModel(stateManager) }
    )
}