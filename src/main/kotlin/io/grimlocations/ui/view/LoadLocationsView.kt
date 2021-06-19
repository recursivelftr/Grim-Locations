package io.grimlocations.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.grimlocations.constant.APP_ICON
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.view.component.PMDChooserComponent
import io.grimlocations.ui.viewmodel.LoadLocationsViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.swing.JFileChooser

private val TEXT_FIELD_WIDTH = 450.dp

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun LoadLocationsView(
    vm: LoadLocationsViewModel = getFactoryViewModel(),
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
                Spacer(Modifier.height(30.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
//                    textStyle = TextStyle.Default.copy(color = Color.White),
//                    textColor = Color.White,
                        value = state.locationsFilePath,
                        onValueChange = vm::updateLocationsFilePath,
                        label = {
                            Text("Locations File", style = TextStyle(fontSize = 15.sp))
                        },
                        singleLine = true,
                        modifier = Modifier.width(TEXT_FIELD_WIDTH)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            with(vm.locationsFileChooser) {
                                val okOrCancel = showOpenDialog(null)
                                if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                    vm.updateLocationsFilePath(selectedFile.absolutePath)
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            "Browse",
                        )
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
                            vm.loadLocationsIntoSelectedProfile(
                                filePath = state.locationsFilePath,
                                window = window,
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
fun openLoadLocationsView(
    vmProvider: GLViewModelProvider,
    previousWindow: AppWindow? = null,
    onClose: (() -> Unit)? = null,
    captureWindow: ((AppWindow) -> Unit)? = null,
) {
    var subWindows: Set<AppWindow>? = null
    Window(
        title = "Grim Locations",
        icon = APP_ICON,
        size = IntSize(650, 450),
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
                LoadLocationsView(
                    captureSubWindows = { l ->
                        subWindows = l
                    }
                )
            }
        }
    }
}
