package io.grimlocations.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.constant.APP_ICON
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.view.component.PMDChooserComponent
import io.grimlocations.ui.viewmodel.LauncherViewModel
import io.grimlocations.ui.viewmodel.event.loadLocationsIntoSelectedProfile
import io.grimlocations.ui.viewmodel.event.persistPMDAndWriteLocations
import io.grimlocations.ui.viewmodel.event.selectPMD
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.swing.JFileChooser

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun LauncherView(
    vm: LauncherViewModel = getFactoryViewModel(),
    captureSubWindows: ((Set<AppWindow>) -> Unit),
) {
    View(vm) { state ->

        val window = LocalAppWindow.current
        remember { captureSubWindows(subWindows) }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PMDChooserComponent(
                    map = state.map,
                    selected = state.selected,
                    onSelect = { c -> vm.selectPMD(c) },
                    onOpen = { p, c ->
                        viewDisabled = true
                        subWindows.remove(p)
                        subWindows.add(c)
                        onOverlayClick = { subWindows.forEach { a -> a.closeIfOpen() } }
                    },
                    onClose = { viewDisabled = false }
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            with(vm.locationsFileChooser) {
                                val okOrCancel = showOpenDialog(null)
                                if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                    vm.loadLocationsIntoSelectedProfile(
                                        filePath = selectedFile.absolutePath,
                                        onOpenPopup = {
                                            onOverlayClick = {
                                                it.closeIfOpen()
                                                subWindows.remove(it)
                                                viewDisabled = false
                                            }
                                            subWindows.add(it)
                                        },
                                        onClosePopup = {
                                            subWindows.remove(it)
                                            viewDisabled = false
                                        }
                                    )
                                }
                            }
                        },
                    ) {
                        Text("Load Locations to Profile")
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            window.closeIfOpen()
                        },
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            vm.persistPMDAndWriteLocations(window)
                        },
                    ) {
                        Text("Ok")
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun openLauncherView(
    vmProvider: GLViewModelProvider,
    previousWindow: AppWindow? = null,
    onClose: (() -> Unit)? = null,
    captureWindow: ((AppWindow) -> Unit)? = null,
) {
    var subWindows: Set<AppWindow>? = null
    Window(
        title = "Grim Locations",
        icon = APP_ICON,
        size = IntSize(500, 400),
        onDismissRequest = {
            subWindows?.forEach { it.closeIfOpen() }
            onClose?.invoke()
        }
    ) {
        val window = LocalAppWindow.current
        remember {
            previousWindow?.closeIfOpen()
            captureWindow?.invoke(window)
        }

        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                LauncherView(
                    captureSubWindows = { l ->
                        subWindows = l
                    }
                )
            }
        }
    }
}
