package io.grimlocations.launcher

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.ui.unit.IntSize
import io.grimlocations.shared.ui.GDLocationManagerTheme
import io.grimlocations.shared.ui.view.AppEntryStandaloneView
import io.grimlocations.shared.ui.view.openLauncherView
import io.grimlocations.shared.ui.view.openPropertiesView
import io.grimlocations.shared.util.enableThreadLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger()

@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    enableThreadLogging()

    Window(
        title = "Grim Locations",
        undecorated = true,
        size = IntSize(500, 300)
    ) {
        val window = LocalAppWindow.current

        GDLocationManagerTheme {
            AppEntryStandaloneView { arePropertiesSet, vmProvider ->
                if (arePropertiesSet)
                    openLauncherView(vmProvider, window)
                else
                    openPropertiesView(
                        vmProvider,
                        nextWindow = ::openLauncherView,
                        previousWindowToClose = window
                    )
            }
        }
    }
}
