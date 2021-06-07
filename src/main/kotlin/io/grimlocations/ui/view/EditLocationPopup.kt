package io.grimlocations.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.grimlocations.constant.APP_ICON
import io.grimlocations.data.dto.LocationDTO
import io.grimlocations.util.extension.closeIfOpen
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val TEXT_FIELD_WIDTH = 400.dp

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun EditLocationPopup(
    location: LocationDTO,
    onOkClicked: (LocationDTO) -> Unit,
    onCancelClicked: (() -> Unit)?,
) {
    val loc = remember { mutableStateOf(location) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = loc.value.name,
                onValueChange = { loc.value = loc.value.copy(name = it) },
                label = {
                    Text("Name", style = TextStyle(fontSize = 15.sp))
                },
                singleLine = true,
                modifier = Modifier.width(TEXT_FIELD_WIDTH)
            )
            Spacer(Modifier.height(20.dp))
            TextField(
                value = loc.value.coordinate.run { "$coordinate1, $coordinate2, $coordinate3" },
                onValueChange = { loc.value = loc.value.copy(name = it) },
                label = {
                    Text("Name", style = TextStyle(fontSize = 15.sp))
                },
                singleLine = true,
                modifier = Modifier.width(TEXT_FIELD_WIDTH)
            )
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
                    onClick = { onOkClicked(loc.value) },
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
fun openEditLocationPopup(
    location: LocationDTO,
    onOpen: (AppWindow) -> Unit,
    onCancelClicked: (AppWindow) -> Unit,
    onOkClicked: (AppWindow, LocationDTO) -> Unit,
) {
    Window(
        title = "Grim Locations",
        icon = APP_ICON,
        size = IntSize(550, 300),
    ) {
        val window = LocalAppWindow.current

        remember { onOpen(window) }

        GrimLocationsTheme {
            EditLocationPopup(
                location = location,
                onCancelClicked = {
                    onCancelClicked(window)
                    window.closeIfOpen()
                },
                onOkClicked = { loc ->
                    onOkClicked(window, loc)
                    window.closeIfOpen()
                }
            )
        }
    }
}
