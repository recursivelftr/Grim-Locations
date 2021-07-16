package io.grimlocations.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.framework.ui.getFactoryViewModel
import io.grimlocations.framework.ui.view.View
import io.grimlocations.ui.view.component.PMDChooserComponent
import io.grimlocations.ui.view.component.openOkCancelPopup
import io.grimlocations.ui.viewmodel.LoadLocationsViewModel
import io.grimlocations.ui.viewmodel.event.clearLoadMsg
import io.grimlocations.ui.viewmodel.event.loadLocationsIntoSelectedProfile
import io.grimlocations.ui.viewmodel.event.selectPMD
import io.grimlocations.ui.viewmodel.event.updateLocationsFilePath
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.swing.JFileChooser

private val TEXT_FIELD_WIDTH = 450.dp

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun LoadLocationsView(
    vm: LoadLocationsViewModel = getFactoryViewModel(),
    onClose: (() -> Unit)
) {
    View(vm) { state ->

        if (state.loadMsg != null) {
            openOkCancelPopup(
                message = state.loadMsg,
                onOkClicked = vm::clearLoadMsg
            )
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PMDChooserComponent(
                    map = state.map,
                    selected = state.selected,
                    onSelect = { c -> vm.selectPMD(c) },
                )
                Spacer(Modifier.height(30.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
//                    textStyle = TextStyle.Default.copy(color = Color.White),
//                    textColor = Color.White,
                        value = state.locationsFilePath,
                        onValueChange = vm::updateLocationsFilePath,
                        label = {
                            Text("Locations File", style = TextStyle(fontSize = 15.sp))
                        },
                        singleLine = true,
                        modifier = Modifier.width(TEXT_FIELD_WIDTH)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = {
                            with(vm.locationsFileChooser) {
                                val okOrCancel = showOpenDialog(null)
                                if (okOrCancel == JFileChooser.APPROVE_OPTION) {
                                    vm.updateLocationsFilePath(selectedFile.absolutePath)
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
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onClose,
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            vm.loadLocationsIntoSelectedProfile(
                                filePath = state.locationsFilePath,
                                onSuccess = onClose,
                            )
                        },
                    ) {
                        Text("Ok")
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun openLoadLocationsView(
    onClose: (() -> Unit),
) {

    val dialogState =
        rememberDialogState(size = WindowSize(650.dp, 450.dp), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        title = "Grim Locations",
        state = dialogState,
        onCloseRequest = onClose,
    ) {
        GrimLocationsTheme {
            LoadLocationsView(
                onClose = onClose,
            )
        }
    }
}
