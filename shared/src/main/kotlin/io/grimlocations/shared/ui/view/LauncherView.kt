package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.get
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GDLocationManagerTheme
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.view.component.DropdownBox
import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val items = listOf(
    Pair(1, "Oneaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
    Pair(2, "Two"),
    Pair(3, "Three"),
    Pair(4, "Four"),
    Pair(5, "Five"),
    Pair(6, "Six"),
    Pair(7, "Seven"),
    Pair(8, "Eight"),
    Pair(9, "Nine"),
    Pair(10, "Ten"),
    Pair(11, "Eleven"),
    Pair(12, "Twelve"),
    Pair(13, "Thirteen"),
    Pair(14, "Fourteen"),
    Pair(15, "Fifteen"),
    Pair(16, "Sixteen"),
    Pair(17, "Seventeen"),
    Pair(18, "Eighteen"),
)

private val emptyItems = emptyList<Pair<Int,String>>()

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun LauncherView(
    launcherVm: LauncherViewModel = LocalViewModel.current.get()
) {
    val disabled = remember { mutableStateOf(false) }
    val vmProvider = LocalViewModel.current as GLViewModelProvider

    val isOpen = remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf(items[0]) }

    View(launcherVm, disabled.value) { launcherEditorState ->

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(modifier = Modifier.wrapContentSize()) {

                        Spacer(modifier = Modifier.height(10.dp))
                        Icon(
                            Icons.Default.Settings,
                            "Settings",
                            modifier = Modifier.size(40.dp).clickable {
                                openPropertiesView(
                                    vmProvider,
                                    { disabled.value = false }
                                )
                                disabled.value = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                DropdownBox(
                    items = items,
                    isOpen = isOpen.value,
                    selected = selected.value,
                    maxWidth = 300.dp,
                    onOpen = { isOpen.value = true },
                    onSelect = {
                        selected.value = it
                        isOpen.value = false
                    }
                ) {
                    Column {
                        Text("test")
                        Spacer(modifier = Modifier.height(100.dp))
                        Text("test2")
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun openLauncherView(vmProvider: GLViewModelProvider, previousWindow: AppWindow) {
    Window(
        title = "Launcher",
        size = IntSize(500, 300)
    ) {
        remember { previousWindow.close() }

        Providers(LocalViewModel provides vmProvider) {
            GDLocationManagerTheme {
                LauncherView()
            }
        }
    }
}
