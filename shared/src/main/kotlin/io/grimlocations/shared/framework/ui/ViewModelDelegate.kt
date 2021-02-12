package io.grimlocations.shared.framework.ui

import io.grimlocations.shared.framework.ui.viewmodel.ViewModel

typealias ViewModelAlias = ViewModel<*, *>

interface ViewModelDelegate<T : ViewModelAlias> {
    val value: T
}