package io.grimlocations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.grimlocations.constant.APP_ICON
import io.grimlocations.ui.GLViewModelProvider
import io.grimlocations.ui.view.*
import io.grimlocations.util.enableThreadLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

private val logger = LogManager.getLogger()

private enum class Startup {
    LOADING, EDITOR_VIEW, PROPERTIES_VIEW
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    enableThreadLogging()

    application {
        val state = rememberWindowState(size = SPLASH_SCREEN_SIZE, position = WindowPosition.Aligned(Alignment.Center))
        var loadingState by remember { mutableStateOf(Pair<Startup, GLViewModelProvider?>(Startup.LOADING, null)) }
        APP_ICON = painterResource("Location512PurpleBlack.png")

        when (loadingState.first) {
            Startup.LOADING -> {
                Window(
                    title = "Grim Locations",
                    icon = APP_ICON,
                    undecorated = true,
                    state = state,
                    onCloseRequest = ::exitApplication
                ) {
                    GrimLocationsTheme {
                        AppEntryStandaloneView { arePropertiesSet, vmProvider ->
                            loadingState = if (arePropertiesSet) {
                                Pair(Startup.EDITOR_VIEW, vmProvider)
                            } else {
                                Pair(Startup.PROPERTIES_VIEW, vmProvider)
                            }
                        }
                    }
                }
            }
            Startup.EDITOR_VIEW -> {
                openEditorView(loadingState.second!!, ::exitApplication)
            }
            Startup.PROPERTIES_VIEW -> {
                openInitialPropertiesView(
                    vmProvider = loadingState.second!!,
                    nextWindow = { openEditorView(loadingState.second!!, ::exitApplication) },
                    closeWindow = ::exitApplication,
                )
            }
        }


    }
}

fun fileReadsTest() {
    val locations = File("C:\\steam\\steamapps\\common\\Grim Dawn\\GrimInternals_TeleportList.txt")
    val player = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_Recursive\\player.gdc")
    val player2 = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveN\\player.gdc")
    val player3 = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveW\\player.gdc")
    val playerBak = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_Recursive\\player.gdc.bak")
    val player2Bak = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveN\\player.gdc.bak")
    val player3Bak = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveW\\player.gdc.bak")
    val quests =
        File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_Recursive\\maps_survivalworld_i.map\\Elite\\quests.gdd")
    val playmenu = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\playmenu.cpn")

    val locAtr = Files.readAttributes(locations.toPath(), BasicFileAttributes::class.java)
    val playerAtr = Files.readAttributes(player.toPath(), BasicFileAttributes::class.java)
    val player2Atr = Files.readAttributes(player2.toPath(), BasicFileAttributes::class.java)
    val player3Atr = Files.readAttributes(player3.toPath(), BasicFileAttributes::class.java)
    val playerAtrBak = Files.readAttributes(playerBak.toPath(), BasicFileAttributes::class.java)
    val player2AtrBak = Files.readAttributes(player2Bak.toPath(), BasicFileAttributes::class.java)
    val player3AtrBak = Files.readAttributes(player3Bak.toPath(), BasicFileAttributes::class.java)
    val questsAtr = Files.readAttributes(quests.toPath(), BasicFileAttributes::class.java)
    val playmenuAtr = Files.readAttributes(playmenu.toPath(), BasicFileAttributes::class.java)

    println("Teleport list last read: ${locAtr.lastAccessTime()}")
    println("Teleport list last modified: ${locAtr.lastModifiedTime()}")
    println("Player file last read: ${playerAtr.lastAccessTime()}")
    println("Player 2 file last read: ${player2Atr.lastAccessTime()}")
    println("Player 3 file last read: ${player3Atr.lastAccessTime()}")
    println("Player bak file last read: ${playerAtrBak.lastAccessTime()}")
    println("Player 2 bak file last read: ${player2AtrBak.lastAccessTime()}")
    println("Player 3 bak file last read: ${player3AtrBak.lastAccessTime()}")
    println("Player Quests file last read: ${questsAtr.lastAccessTime()}")
    println("Play menu last modified: ${playmenuAtr.lastModifiedTime()}")
}