package io.grimlocations.ui.view.component

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.constant.APP_ICON
import io.grimlocations.ui.view.GrimLocationsTheme
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val dropDownBackgroundColorDark = Color(47, 47, 47)
private val dropDownBackgroundColorLight = Color(224, 224, 224)

@Composable
private fun OkCancelPopup(
    message: String,
    onOkClicked: () -> Unit,
    onCancelClicked: (() -> Unit)?,
) {

    val dropDownBackgroundColor: Color
    val textColor: Color
    if (MaterialTheme.colors.isLight) {
        dropDownBackgroundColor = dropDownBackgroundColorLight
        textColor = Color.Unspecified
    } else {
        dropDownBackgroundColor = dropDownBackgroundColorDark
        textColor = Color.White
    }

    Surface(
        color = dropDownBackgroundColor,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(message, style = TextStyle(fontSize = 15.sp, color = textColor))
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (onCancelClicked != null) {
                    Button(
                        onClick = onCancelClicked,
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Button(
                    onClick = onOkClicked,
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

fun legacyOpenOkCancelPopup(
    message: String,
    onOpen: (AppWindow) -> Unit,
    onCancelClicked: ((AppWindow) -> Unit)? = null,
    onOkClicked: (AppWindow) -> Unit,
    width: Int = 400,
    height: Int = 200,
) {
    Window(
        title = "Grim Locations",
        icon = APP_ICON,
        size = IntSize(width, height),
//        undecorated = true
    ) {
        val window = LocalAppWindow.current

        remember { onOpen(window) }

        GrimLocationsTheme {
            OkCancelPopup(
                message = message,
                onCancelClicked = onCancelClicked?.let {
                    {
                        it(window)
                        window.closeIfOpen()
                    }
                },
                onOkClicked = {
                    onOkClicked(window)
                    window.closeIfOpen()
                }
            )
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun openOkCancelPopup(
    message: String,
    onCancelClicked: (() -> Unit)? = null, //in the case of null then ok is the same as cancel
    onOkClicked: () -> Unit,
    width: Dp = 400.dp,
    height: Dp = 200.dp,
) {
    val dialogState =
        rememberDialogState(size = WindowSize(width, height), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        onCloseRequest = onCancelClicked ?: onOkClicked,
        title = "",
        state = dialogState,
    ) {
        OkCancelPopup(message, onOkClicked, onCancelClicked)
    }
}