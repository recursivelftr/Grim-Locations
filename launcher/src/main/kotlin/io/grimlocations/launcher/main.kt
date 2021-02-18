package io.grimlocations.launcher

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.unit.IntSize
import io.grimlocations.shared.ui.view.*
import io.grimlocations.shared.util.enableThreadLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val logger: Logger = LogManager.getLogger()

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    enableThreadLogging()

    Window(
        title = "Grim Locations",
        undecorated = true,
        size = SPLASH_SCREEN_SIZE
    ) {
        val window = LocalAppWindow.current

        GrimLocationsTheme {
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
