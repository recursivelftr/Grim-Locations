package io.grimlocations.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.grimlocations.constant.APP_ICON
import io.grimlocations.framework.ui.LocalViewModel
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.viewmodel.PropertiesViewModel
import io.grimlocations.ui.viewmodel.event.*
import io.grimlocations.ui.viewmodel.state.PropertiesState
import io.grimlocations.ui.viewmodel.state.PropertiesStateError.GRIM_INTERNALS_NOT_FOUND
import io.grimlocations.ui.viewmodel.state.PropertiesStateWarning.NO_CHARACTERS_FOUND
import io.grimlocations.util.extension.closeIfOpen
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

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                TextField(
//                    textStyle = TextStyle.Default.copy(color = Color.White),
//                    textColor = Color.White,
                    value = it.savePath ?: vm.getGdSaveLocation() ?: "",
                    onValueChange = vm::updateSavePath,
                    label = {
                        Text("GD Save Folder", style = TextStyle(fontSize = 15.sp))
                    },
                    singleLine = true,
                    modifier = Modifier.width(TEXT_FIELD_WIDTH)
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        with(vm.saveFileChooser) {
                            val okOrCancel = showOpenDialog(null)
                            if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                vm.updateSavePath(selectedFile.absolutePath)
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

            if (it.warnings.contains(NO_CHARACTERS_FOUND)) {
                Text(
                    "No character profiles found",
                    color = Color.Yellow,
                    modifier = Modifier.width(TEXT_FIELD_WIDTH)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onCancel,
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(10.dp))

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

@ExperimentalCoroutinesApi
fun openPropertiesView(
    vmProvider: GLViewModelProvider,
    nextWindow: ((GLViewModelProvider, AppWindow) -> Unit)? = null,
    previousWindowToClose: AppWindow? = null,
    onClose: ((AppWindow) -> Unit)? = null,
    captureWindow: ((AppWindow) -> Unit)? = null,
) {
    lateinit var window: AppWindow

    Window(
        title = "Properties",
        icon = APP_ICON,
        size = IntSize(550, 300),
        onDismissRequest = {
            onClose?.invoke(window)
        }
    ) {

        window = LocalAppWindow.current

        remember {
            captureWindow?.invoke(window)
            previousWindowToClose?.closeIfOpen()
        }

        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                PropertiesView(
                    {
                        window.close()
                    },
                    {
                        if (nextWindow == null)
                            window.close()
                        else
                            nextWindow.invoke(vmProvider, window)
                    }
                )
            }
        }
    }
}

//@ExperimentalCoroutinesApi
//@Composable
//fun PropertiesDialog(
//    onDismiss: () -> Unit,
//    onCancelClicked: (() -> Unit)? = null,
//    onOkClicked: (() -> Unit)? = null,
//) {
//    Dialog(
//        onDismissRequest = onDismiss,
//        properties = DialogProperties()
//    ) {
//        val window = LocalAppWindow.current
//        PropertiesView(
//            onCancel = {
//                onCancelClicked?.invoke()
//                onDismiss()
//            },
//            onOk = {
//                onOkClicked?.invoke()
//                onDismiss()
//            }
//        )
//    }
//}

