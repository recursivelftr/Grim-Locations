package io.grimlocations.ui.view.component

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.framework.data.dto.NameDTO
import io.grimlocations.ui.view.GrimLocationsTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val TEXT_FIELD_WIDTH = 480.dp


@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun <T: NameDTO> CreateNameDTOPopup(
    dto: T,
    dtos: Set<T>,
    duplicateNameMessage: String,
    onOkClicked: (String, T) -> Unit,
    onCancelClicked: (() -> Unit),
) {
    val dtoName = remember { mutableStateOf(dto.name) }
    val containsCommas = dtoName.value.contains(",")
    val duplicateName = dtos.find { it.name == dtoName.value } != null

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = dtoName.value,
                onValueChange = { dtoName.value = it },
                label = {
                    Text("Name", style = TextStyle(fontSize = 15.sp))
                },
                singleLine = true,
                modifier = Modifier.width(TEXT_FIELD_WIDTH)
            )
            if (containsCommas) {
                Text(
                    "Commas are not allowed",
                    color = Color.Red,
                    modifier = Modifier.width(TEXT_FIELD_WIDTH)
                )
            } else if(duplicateName) {
                Text(
                    duplicateNameMessage,
                    color = Color.Red,
                    modifier = Modifier.width(TEXT_FIELD_WIDTH)
                )
            }
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
                Spacer(modifier = Modifier.width(20.dp))
                Button(
                    onClick = { onOkClicked(dtoName.value, dto) },
                    enabled = !(containsCommas || duplicateName)
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun <T: NameDTO> openCreateNameDTOPopup(
    dto: T,
    dtos: Set<T>,
    duplicateNameMessage: String,
    onCancelClicked: () -> Unit,
    onOkClicked: (String, T) -> Unit,
    title: String = "Grim Locations",
) {
    val dialogState =
        rememberDialogState(size = WindowSize(550.dp, 275.dp), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        state = dialogState,
        title = title,
        onCloseRequest = onCancelClicked,
    ) {
        GrimLocationsTheme {
            CreateNameDTOPopup(
                dto = dto,
                dtos = dtos,
                duplicateNameMessage = duplicateNameMessage,
                onCancelClicked = onCancelClicked,
                onOkClicked = onOkClicked,
            )
        }
    }
}