package io.grimlocations.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import io.grimlocations.constant.APP_ICON
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.viewmodel.PropertiesViewModel
import io.grimlocations.ui.viewmodel.event.getGdInstallLocation
import io.grimlocations.ui.viewmodel.event.persistState
import io.grimlocations.ui.viewmodel.event.updateInstallPath
import io.grimlocations.ui.viewmodel.state.PropertiesState
import io.grimlocations.ui.viewmodel.state.PropertiesStateError.GRIM_INTERNALS_NOT_FOUND
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.swing.JFileChooser

private val TEXT_FIELD_WIDTH = 400.dp

@ExperimentalCoroutinesApi
@Composable
private fun PropertiesView(
    onCancel: () -> Unit,
    onOk: () -> Unit,
    vm: PropertiesViewModel = getFactoryViewModel(),
) = View(vm) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
//                    textStyle = TextStyle.Default.copy(color = Color.White),
//                    textColor = Color.White,
                    value = it.installPath ?: vm.getGdInstallLocation() ?: "",
                    onValueChange = vm::updateInstallPath,
                    label = {
                        Text("GD Installation Folder", style = TextStyle(fontSize = 15.sp))
                    },
                    singleLine = true,
                    modifier = Modifier.width(TEXT_FIELD_WIDTH)
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        with(vm.installFileChooser) {
                            val okOrCancel = showOpenDialog(null)
                            if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                vm.updateInstallPath(selectedFile.absolutePath)
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

            if (it.errors.contains(GRIM_INTERNALS_NOT_FOUND)) {
                Text(
                    "GrimInternals64.exe not found",
                    color = Color.Red,
                    modifier = Modifier.width(TEXT_FIELD_WIDTH)
                )
            }

            Spacer(Modifier.height(30.dp))

            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onCancel,
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    enabled = isOkEnabled(it),
                    onClick = {
                        vm.persistState()
                        onOk()
                    },
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

private fun isOkEnabled(state: PropertiesState) =
    state.installPath != null && state.installPath.isNotBlank() && state.errors.isEmpty()

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@Composable
fun openInitialPropertiesView(
    vmProvider: GLViewModelProvider,
    nextWindow: @Composable (() -> Unit),
    closeWindow: () -> Unit,
) {
    val state =
        rememberWindowState(size = WindowSize(550.dp, 250.dp), position = WindowPosition.Aligned(Alignment.Center))

    val isOpen = remember { mutableStateOf(true) }

    if (isOpen.value) {
        Window(
            title = "Settings",
            icon = APP_ICON,
            state = state,
            onCloseRequest = closeWindow,
        ) {
            CompositionLocalProvider(LocalViewModel provides vmProvider) {
                GrimLocationsTheme {
                    PropertiesView(
                        onCancel = closeWindow,
                        onOk = {
                            isOpen.value = false
                        }
                    )
                }
            }
        }
    } else {
        nextWindow()
    }
}

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@Composable
fun openPropertiesView(
    closeWindow: () -> Unit,
) {
    val state =
        rememberDialogState(size = WindowSize(550.dp, 250.dp), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        title = "Settings",
        icon = APP_ICON,
        state = state,
        onCloseRequest = closeWindow,
    ) {
        GrimLocationsTheme {
            PropertiesView(
                onCancel = closeWindow,
                onOk = closeWindow,
            )
        }
    }
}