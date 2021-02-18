package io.grimlocations.shared.ui.view.editor

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.get
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.view.GrimLocationsTheme
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun EditorView(
    editorVm: EditorViewModel = LocalViewModel.current.get(),
    captureSubWindow: ((AppWindow?, AppWindow) -> Unit)? = null,
) = View(editorVm) {

}

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun openEditorView(vmProvider: GLViewModelProvider, previousWindow: AppWindow) {
    val subWindows = mutableListOf<AppWindow>()
    Window(
        title = "Grim Locations",
        size = IntSize(800, 600),
        onDismissRequest = {
            subWindows.forEach { it.closeIfOpen() }
        }
    ) {
        remember { previousWindow.close() }

        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                EditorView(
                    captureSubWindow = { p, c ->
                        subWindows.remove(p)
                        subWindows.add(c)
                    }
                )
            }
        }
    }
}