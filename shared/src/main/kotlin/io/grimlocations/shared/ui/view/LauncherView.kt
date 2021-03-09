package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.getFactoryViewModel
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.view.component.PMDChooserComponent
import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import io.grimlocations.shared.ui.viewmodel.event.persistPMD
import io.grimlocations.shared.ui.viewmodel.event.persistState
import io.grimlocations.shared.ui.viewmodel.event.selectPMD
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun LauncherView(
    vm: LauncherViewModel = getFactoryViewModel(),
    captureSubWindows: ((Set<AppWindow>) -> Unit),
) {
    val vmProvider = LocalViewModel.current as GLViewModelProvider

    View(vm) { state ->

        val window = LocalAppWindow.current
        remember { captureSubWindows(subWindows) }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(modifier = Modifier.wrapContentSize()) {

                        Spacer(modifier = Modifier.height(10.dp))
                        Icon(
                            Icons.Default.Settings,
                            "Settings",
                            modifier = Modifier.size(40.dp).clickable {
                                openPropertiesView(
                                    vmProvider,
                                    { disabled = false }
                                )
                                disabled = true
                                onOverlayClick = {}
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                PMDChooserComponent(
                    map = state.map,
                    selected = state.selected,
                    onSelect = { c -> vm.selectPMD(c) },
                    onOpen = { p, c ->
                        disabled = true
                        subWindows.remove(p)
                        subWindows.add(c)
                        onOverlayClick = { subWindows.forEach { a -> a.closeIfOpen() } }
                    },
                    onClose = { disabled = false }
                )
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
                            vm.persistPMD(window)
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
        size = IntSize(500, 375),
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
