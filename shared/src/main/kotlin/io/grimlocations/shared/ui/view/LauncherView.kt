package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.framework.ui.LocalViewModel
import io.grimlocations.shared.framework.ui.get
import io.grimlocations.shared.framework.ui.getFactoryViewModel
import io.grimlocations.shared.framework.ui.getLazyViewModel
import io.grimlocations.shared.framework.ui.view.View
import io.grimlocations.shared.ui.GLViewModelProvider
import io.grimlocations.shared.ui.view.component.ComboPopup
import io.grimlocations.shared.ui.viewmodel.LauncherViewModel
import io.grimlocations.shared.util.extension.closeIfOpen
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

private val emptyItems = emptyList<Pair<Int, String>>()

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun LauncherView(
    launcherVm: LauncherViewModel = getFactoryViewModel(),
    captureSubWindow: ((AppWindow?, AppWindow) -> Unit)? = null,
) {

    val vmProvider = LocalViewModel.current as GLViewModelProvider
    val selected = remember { mutableStateOf(items[0]) }

    View(launcherVm) { launcherEditorState ->

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
                                    { disabled = false }
                                )
                                disabled = true
                                onOverlayClick = {}
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                ComboPopup(
                    "Profile",
                    items = items,
                    emptyItemsMessage = "No Profiles",
                    selected = selected.value,
                    width = 300.dp,
                    onOpen = { p, c ->
                        disabled = true
                        onOverlayClick = {
                            c.closeIfOpen()
                            disabled = false
                        }
                        captureSubWindow?.invoke(p, c)
                    },
                    onClose = { disabled = false },
                    onSelect = {
                        selected.value = it
                    }
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun openLauncherView(vmProvider: GLViewModelProvider, previousWindow: AppWindow) {
    val subWindows = mutableListOf<AppWindow>()
    Window(
        title = "Grim Locations",
        size = IntSize(500, 300),
        onDismissRequest = {
            subWindows.forEach { it.closeIfOpen() }
        }
    ) {
        remember { previousWindow.close() }

        CompositionLocalProvider(LocalViewModel provides vmProvider) {
            GrimLocationsTheme {
                LauncherView(
                    captureSubWindow = { p, c ->
                        subWindows.remove(p)
                        subWindows.add(c)
                    }
                )
            }
        }
    }
}
