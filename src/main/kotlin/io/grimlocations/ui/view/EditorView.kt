package io.grimlocations.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberWindowState
import io.grimlocations.constant.APP_ICON
import io.grimlocations.data.dto.hasOnlyReservedProfiles
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.framework.ui.get
import io.grimlocations.framework.ui.getLazyViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.framework.ui.viewmodel.stateFlow
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.view.component.SelectionMode
import io.grimlocations.ui.view.component.openOkCancelPopup
import io.grimlocations.ui.viewmodel.EditorViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.ui.viewmodel.state.EditorState
import io.grimlocations.ui.viewmodel.state.container.PMDContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class EditorFocus {
    LEFT_LOCATION_LIST, RIGHT_LOCATION_LIST, NONE
}

object EditorFocusManager {
    lateinit var editorState: EditorState
    var selectionMode = SelectionMode.SINGLE
    var currentFocus = EditorFocus.NONE
    var ctrlAActionLeft: (() -> Unit)? = null
    var ctrlAActionRight: (() -> Unit)? = null
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun EditorView(
    vm: EditorViewModel = getLazyViewModel(),
) = View(vm) { state ->

    EditorFocusManager.ctrlAActionLeft = {
        vm.selectLocationsLeft(state.locationsLeft)
    }

    EditorFocusManager.ctrlAActionRight = {
        vm.selectLocationsRight(state.locationsRight)
    }

    EditorFocusManager.editorState = state

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
                captureFocusLeft = { EditorFocusManager.currentFocus = EditorFocus.LEFT_LOCATION_LIST },
                captureFocusRight = { EditorFocusManager.currentFocus = EditorFocus.RIGHT_LOCATION_LIST },
                editorFocusManager = EditorFocusManager,
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

    var isClosingWithGdRunning by remember { mutableStateOf(false) }

    Window(
        title = "Grim Locations",
        icon = APP_ICON,
        state = state,
        onPreviewKeyEvent = {
            when {
                (it.isCtrlPressed && it.key == Key.A) -> {
                    if (EditorFocusManager.currentFocus == EditorFocus.LEFT_LOCATION_LIST) {
                        EditorFocusManager.ctrlAActionLeft?.invoke()
                    } else if (EditorFocusManager.currentFocus == EditorFocus.RIGHT_LOCATION_LIST) {
                        EditorFocusManager.ctrlAActionRight?.invoke()
                    }
                }
                (it.isShiftPressed) -> {
                    EditorFocusManager.selectionMode = SelectionMode.RANGE
                }
                (it.isCtrlPressed) -> {
                    EditorFocusManager.selectionMode = SelectionMode.MULTIPLE
                }
                else -> {
                    EditorFocusManager.selectionMode = SelectionMode.SINGLE
                }
            }
            true
        },
        onCloseRequest = {
            if (EditorFocusManager.editorState.isGDRunning) {
                isClosingWithGdRunning = true
            } else {
                exitApplication()
            }
        }
    ) {
        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                EditorView()

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