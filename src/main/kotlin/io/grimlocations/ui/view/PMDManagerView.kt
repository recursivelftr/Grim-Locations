package io.grimlocations.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.framework.ui.getLazyViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.view.component.SelectionMode
import io.grimlocations.ui.viewmodel.PMDManagerViewModel
import io.grimlocations.ui.viewmodel.state.PMDManagerState
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class PMDManagerFocus {
    NONE, PROFILE_LIST, MOD_LIST, DIFFICULTY_LIST
}

object PMDManagerFocusManager {
    lateinit var pmdManagerState: PMDManagerState
    var selectionMode = SelectionMode.SINGLE
    var currentFocus = PMDManagerFocus.NONE
    var ctrlAActionProfiles: (() -> Unit)? = null
    var ctrlAActionMods: (() -> Unit)? = null
    var ctrlAActionDifficulties: (() -> Unit)? = null
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun PMDManagerView(
    vm: PMDManagerViewModel = getLazyViewModel(),
) = View(vm) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("hello")
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun openPMDManagerView(
    onClose: (() -> Unit),
) {

    val dialogState =
        rememberDialogState(size = WindowSize(1500.dp, 950.dp), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        title = "Grim Locations",
        state = dialogState,
        onCloseRequest = onClose,
    ) {
        GrimLocationsTheme {
            PMDManagerView()
        }
    }
}