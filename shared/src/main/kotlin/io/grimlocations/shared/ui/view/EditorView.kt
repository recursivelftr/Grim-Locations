package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.getLazyViewModel
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.viewmodel.EditorViewModel
import io.grimlocations.shared.ui.viewmodel.event.loadCharacterProfiles
import io.grimlocations.shared.ui.viewmodel.event.reloadState
import io.grimlocations.shared.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.shared.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun EditorView(
    vm: EditorViewModel = getLazyViewModel(),
    captureSubWindows: ((Set<AppWindow>) -> Unit),
) = View(vm) { state ->
    val vmProv = LocalViewModel.current as GLViewModelProvider
    val previousLauncherWindow = remember { mutableStateOf<AppWindow?>(null) }

    remember { captureSubWindows(subWindows) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                                vmProvider = vmProv,
                                onClose = {
                                    subWindows.remove(it)
                                    disabled = false
                                },
                                captureWindow = { subWindows.add(it) }
                            )
                            disabled = true
                            onOverlayClick = {}
                        }
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        disabled = true
                        vm.loadCharacterProfiles(
                            onOpenPopup = {
                                onOverlayClick = {
                                    it.closeIfOpen()
                                    subWindows.remove(it)
                                    disabled = false
                                }
                                subWindows.add(it)
                            },
                            onClosePopup = {
                                subWindows.remove(it)
                                disabled = false
                            }
                        )
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
                                vm.reloadState()
                                disabled = false
                            },
                            captureWindow = { w ->
                                subWindows.remove(previousLauncherWindow.value)
                                subWindows.add(w)
                                previousLauncherWindow.value = w
                            }
                        )
                    },
                ) {
                    Text("Play Profile")
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Grim Dawn Status: ")
                Text("Not Running", color = Color(0, 204, 0))
            }
            Spacer(Modifier.height(20.dp))
            ActiveProfileRow(state.activePMD)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column {

                }
                Column {

                }
            }
        }
    }
}

@Composable
fun ActiveProfileRow(pmd: PMDContainer?) {

    val labelColor = MaterialTheme.colors.onSurface.let {
        val isLightColors = MaterialTheme.colors.isLight
        remember {
            val offset = if (isLightColors) .3f else -.3f
            it.copy(red = it.red + offset, blue = it.blue + offset, green = it.green + offset)
        }
    }

    val textBoxWidth = 300.dp
    val textFieldHeight = 56.dp
    val spacerWidth = 15.dp
    val enabled = true
    val readonly = true

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = pmd?.profile?.name ?: "",
            readOnly = readonly,
            enabled = enabled,
            onValueChange = {},
            singleLine = true,
            label = {
                Text(
                    "Profile",
                    color = labelColor
                )
            },
            modifier = Modifier.width(textBoxWidth).height(textFieldHeight)
        )
        Spacer(modifier = Modifier.width(spacerWidth))
        TextField(
            value = pmd?.mod?.name ?: "",
            readOnly = readonly,
            enabled = enabled,
            onValueChange = {},
            singleLine = true,
            label = {
                Text(
                    "Mod",
                    color = labelColor
                )
            },
            modifier = Modifier.width(textBoxWidth).height(textFieldHeight)
        )
        Spacer(modifier = Modifier.width(spacerWidth))
        TextField(
            value = pmd?.difficulty?.name ?: "",
            readOnly = readonly,
            enabled = enabled,
            onValueChange = {},
            singleLine = true,
            label = {
                Text(
                    "Difficulty",
                    color = labelColor
                )
            },
            modifier = Modifier.width(textBoxWidth).height(textFieldHeight)
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun openEditorView(vmProvider: GLViewModelProvider, previousWindow: AppWindow) {
    var subWindows: Set<AppWindow>? = null
    Window(
        title = "Grim Locations",
        size = IntSize(1024, 768),
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