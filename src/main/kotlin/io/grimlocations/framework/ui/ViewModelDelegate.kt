package io.grimlocations.framework.ui

import io.grimlocations.framework.ui.viewmodel.ViewModel

typealias ViewModelAlias = ViewModel<*, *>

interface ViewModelDelegate<T : ViewModelAlias> {
    val value: T
}