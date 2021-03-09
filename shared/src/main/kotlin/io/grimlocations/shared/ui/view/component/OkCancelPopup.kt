package io.grimlocations.shared.ui.view.component

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.grimlocations.shared.ui.view.GrimLocationsTheme
import io.grimlocations.shared.util.extension.closeIfOpen

@Composable
private fun OkCancelPopup(
    message: String,
    onOkClicked: () -> Unit,
    onCancelClicked: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(message, style = TextStyle(fontSize = 15.sp))
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onCancelClicked,
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = onOkClicked,
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

fun openOkCancelPopup(
    message: String,
    onOpen: (AppWindow) -> Unit,
    onCancelClicked: (AppWindow) -> Unit,
    onOkClicked: (AppWindow) -> Unit
) {
    Window(
        title = "Grim Locations",
        size = IntSize(300, 300),
    ) {
        val window = LocalAppWindow.current

        remember { onOpen(window) }

        GrimLocationsTheme {
            OkCancelPopup(
                message = message,
                onCancelClicked = {
                    onCancelClicked(window)
                    window.closeIfOpen()
                },
                onOkClicked = {
                    onOkClicked(window)
                    window.closeIfOpen()
                }
            )
        }
    }
}