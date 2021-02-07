package io.grimlocations.shared.ui.view

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
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
import io.grimlocations.shared.ui.viewmodel.LauncherEditorViewModel
import io.grimlocations.shared.ui.viewmodel.event.updateShowPropertiesDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@Composable
fun LauncherView(
    launcherEditorVm: LauncherEditorViewModel = LocalViewModel.current.get()
) {
    val disabled = remember { mutableStateOf(false) }
    val vmProvider = LocalViewModel.current as GLViewModelProvider

    View(launcherEditorVm, disabled.value) { launcherEditorState ->

        Surface(modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.End) {
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

            }
        }
    }
}

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
