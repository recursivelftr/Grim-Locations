package io.grimlocations.ui

import io.grimlocations.framework.ui.ViewModelDelegate
import io.grimlocations.framework.ui.ViewModelProvider
import io.grimlocations.framework.ui.factoryViewModel
import io.grimlocations.framework.ui.lazyViewModel
import io.grimlocations.framework.ui.viewmodel.ViewModel
import io.grimlocations.ui.viewmodel.*
import kotlin.reflect.KClass

class GLViewModelProvider(stateManager: GLStateManager) : ViewModelProvider {
    override val viewModelMap: Map<KClass<out ViewModel<*, *>>, ViewModelDelegate<*>> = mapOf(
        PropertiesViewModel::class to factoryViewModel { PropertiesViewModel(stateManager) },
        ActiveChooserViewModel::class to factoryViewModel { ActiveChooserViewModel(stateManager) },
        EditorViewModel::class to lazyViewModel { EditorViewModel(stateManager) },
        LoadLocationsViewModel::class to factoryViewModel { LoadLocationsViewModel(stateManager) },
        PMDManagerViewModel::class to lazyViewModel { PMDManagerViewModel(stateManager) },
    )
}