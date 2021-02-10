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
import io.grimlocations.shared.util.JSystemFileChooser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

@ExperimentalCoroutinesApi
@Composable
fun PropertiesView(
    onCancel: () -> Unit,
    onOk: () -> Unit,
    propertiesViewModel: PropertiesViewModel = LocalViewModel.current.get(),
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
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    Icons.Default.Edit,
                    "Browse",
                    modifier = Modifier.size(30.dp).clickable {
                        propertiesViewModel.installFileChooser.showOpenDialog(null)
                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                TextField(
//                    textStyle = TextStyle.Default.copy(color = Color.White),
//                    textColor = Color.White,
                    value = it.savePath ?: "",
                    onValueChange = propertiesViewModel::updateSavePath,
                    label = {
                        Text("GD Save Folder", style = TextStyle(fontSize = 15.sp))
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    Icons.Default.Edit,
                    "Browse",
                    modifier = Modifier.size(30.dp).clickable {
                        propertiesViewModel.saveFileChooser.showOpenDialog(null)
                    }
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

@ExperimentalCoroutinesApi
fun openPropertiesView(
    vmProvider: GLViewModelProvider,
    enablePreviousWindow: (() -> Unit)? = null,
    nextWindow: ((GLViewModelProvider, AppWindow) -> Unit)? = null,
    previousWindowToClose: AppWindow? = null
) {
    Window(
        title = "Properties",
        size = IntSize(500, 300),
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

