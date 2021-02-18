package io.grimlocations.editor

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import io.grimlocations.shared.ui.view.AppEntryStandaloneView
import io.grimlocations.shared.ui.view.GrimLocationsTheme
import io.grimlocations.shared.ui.view.SPLASH_SCREEN_SIZE
import io.grimlocations.shared.ui.view.editor.openEditorView
import io.grimlocations.shared.ui.view.openPropertiesView
import io.grimlocations.shared.util.enableThreadLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager

val logger = LogManager.getLogger()

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
                    openEditorView(vmProvider, window)
                else
                    openPropertiesView(
                        vmProvider,
                        nextWindow = ::openEditorView,
                        previousWindowToClose = window
                    )
            }
        }
    }
}