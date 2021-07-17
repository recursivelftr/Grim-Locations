package io.grimlocations.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.ui.window.v1.MenuBar
import io.grimlocations.constant.APP_ICON
import io.grimlocations.data.dto.hasOnlyReservedProfiles
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.framework.ui.getLazyViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.view.component.openOkCancelPopup
import io.grimlocations.ui.viewmodel.EditorViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun EditorView(
    vm: EditorViewModel = getLazyViewModel(),
    captureState: (EditorState) -> Unit,
    exitApplication: () -> Unit
) = View(vm) { state ->

    captureState(state)

    LaunchedEffect(vm) {
        vm.startGDProcessCheckLoop()
    }

    if (state.isPropertiesPopupOpen) {
        openPropertiesView(vm::closePropertiesView)
    }

    if (state.isLoadLocationsPopupOpen) {
        openLoadLocationsView(
            onClose = vm::closeLoadLocationsView,
        )
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Column(modifier = Modifier.wrapContentSize()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HamburgerDropdownMenu(
                        state = state,
                        vm = vm,
                        exitApplication = exitApplication,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Grim Dawn Status: ")
                if (state.isGDRunning) {
                    Text("Running", color = Color(0, 204, 0))
                } else {
                    Text("Not Running")
                }
            }
            Spacer(Modifier.height(30.dp))
            ActiveProfileRow(state.activePMD)
            Spacer(Modifier.height(10.dp))
            EditorLocationListPanel(
                state = state,
                vm = vm,
            )
        }
    }
}

@Composable
private fun ActiveProfileRow(pmd: PMDContainer?) {

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
                    "Active Profile",
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
                    "Active Mod",
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
                    "Active Difficulty",
                    color = labelColor
                )
            },
            modifier = Modifier.width(textBoxWidth).height(textFieldHeight)
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun HamburgerDropdownMenu(
    state: EditorState,
    vm: EditorViewModel,
    exitApplication: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box {
        Icon(
            Icons.Default.Menu,
            "Menu",
            modifier = Modifier.size(40.dp).clickable(onClick = {
                isMenuExpanded = !isMenuExpanded
            }),
        )
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
        ) {
            DropdownMenuItem(
                enabled = !state.profileMap.hasOnlyReservedProfiles(),
                onClick = {
                    isMenuExpanded = false
                    vm.openLoadLocationsView()
                },
            ) {
                Text("Load Locations to Profile")
            }

            DropdownMenuItem(
                onClick = {
                    isMenuExpanded = false
                    vm.openPropertiesView()
                },
            ) {
                Text("Settings")
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun openEditorView(
    vmProvider: GLViewModelProvider,
    exitApplication: () -> Unit,
) {
    val state =
        rememberWindowState(size = WindowSize(1500.dp, 950.dp), position = WindowPosition.Aligned(Alignment.Center))

    lateinit var editorState: EditorState

    val captureState = remember<(EditorState) -> Unit> { { editorState = it } }

    var isClosingWithGdRunning by remember { mutableStateOf(false) }

    Window(
        title = "Grim Locations",
        icon = APP_ICON,
        state = state,
        onCloseRequest = {
            if (editorState.isGDRunning) {
                isClosingWithGdRunning = true
            } else {
                exitApplication()
            }
        }
    ) {
        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                EditorView(
                    captureState = captureState,
                    exitApplication = exitApplication,
                )

                if (isClosingWithGdRunning) {
                    openOkCancelPopup(
                        message = "Grim Dawn is running. Are you sure you want to exit?",
                        width = 500.dp,
                        onOkClicked = {
                            exitApplication()
                        },
                        onCancelClicked = {
                            isClosingWithGdRunning = false
                        }
                    )
                }
            }
        }
    }
}