package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.getLazyViewModel
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.view.GrimLocationsTheme
import io.grimlocations.shared.ui.view.openLauncherView
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun EditorView(
    editorVm: EditorViewModel = getLazyViewModel(),
    captureSubWindows: ((Set<AppWindow>) -> Unit),
) = View(editorVm) {
    val vmProv = LocalViewModel.current as GLViewModelProvider
    val previousLauncherWindow = remember { mutableStateOf<AppWindow?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {

                    },
                ) {
                    Text("Load Character Profiles")
                }
                Spacer(modifier = Modifier.width(15.dp))
                Button(
                    onClick = {
                        disabled = true
                        onOverlayClick = {}
                        openLauncherView(
                            vmProvider = vmProv,
                            onClose = {
                                subWindows.remove(previousLauncherWindow.value)
                                disabled = false
                            },
                            captureWindow = { w ->
                                subWindows.remove(previousLauncherWindow.value)
                                subWindows.add(w)
                                captureSubWindows(subWindows)
                                previousLauncherWindow.value = w
                            }
                        )
                    },
                ) {
                    Text("Play Profile")
                }
            }
            Row(
//                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column {

                }
                Column {

                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun openEditorView(vmProvider: GLViewModelProvider, previousWindow: AppWindow) {
    var subWindows: Set<AppWindow>? = null
    Window(
        title = "Grim Locations",
        size = IntSize(800, 600),
        onDismissRequest = {
            subWindows?.forEach { it.closeIfOpen() }
        }
    ) {
        remember { previousWindow.closeIfOpen() }

        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                EditorView(
                    captureSubWindows = { l ->
                        subWindows = l
                    }
                )
            }
        }
    }
}