package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.get
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GDLocationManagerTheme
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.viewmodel.PropertiesViewModel
import io.grimlocations.shared.ui.viewmodel.event.persistState
import io.grimlocations.shared.ui.viewmodel.event.updateInstallPath
import io.grimlocations.shared.ui.viewmodel.event.updateSavePath
import io.grimlocations.shared.ui.viewmodel.state.PropertiesState
import io.grimlocations.shared.ui.viewmodel.state.PropertiesStateError.GRIM_INTERNALS_NOT_FOUND
import io.grimlocations.shared.ui.viewmodel.state.PropertiesStateWarning.NO_CHARACTERS_FOUND
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.swing.JFileChooser

private val TEXT_FIELD_WIDTH = 400.dp

@ExperimentalCoroutinesApi
@Composable
fun PropertiesView(
    onCancel: () -> Unit,
    onOk: () -> Unit,
    propertiesViewModel: PropertiesViewModel = LocalViewModel.current.let { remember { it.get() } },
) = View(propertiesViewModel) {

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
                    value = it.installPath ?: "",
                    onValueChange = propertiesViewModel::updateInstallPath,
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
                        with(propertiesViewModel.installFileChooser) {
                            val okOrCancel = showOpenDialog(null)
                            if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                propertiesViewModel.updateInstallPath(selectedFile.absolutePath)
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
                    value = it.savePath ?: "",
                    onValueChange = propertiesViewModel::updateSavePath,
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
                        with(propertiesViewModel.saveFileChooser) {
                            val okOrCancel = showOpenDialog(null)
                            if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                propertiesViewModel.updateSavePath(selectedFile.absolutePath)
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
                        propertiesViewModel.persistState()
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
    enablePreviousWindow: (() -> Unit)? = null,
    nextWindow: ((GLViewModelProvider, AppWindow) -> Unit)? = null,
    previousWindowToClose: AppWindow? = null
) {
    Window(
        title = "Properties",
        size = IntSize(550, 300),
        onDismissRequest = enablePreviousWindow
    ) {
        remember { previousWindowToClose?.close() }

        val window = LocalAppWindow.current

        Providers(LocalViewModel provides vmProvider) {
            GDLocationManagerTheme {
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

