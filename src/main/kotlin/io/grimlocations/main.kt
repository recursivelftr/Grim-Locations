package io.grimlocations

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import io.grimlocations.constant.APP_ICON
import io.grimlocations.ui.view.*
import io.grimlocations.util.enableThreadLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

private val logger = LogManager.getLogger()

@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    enableThreadLogging()


    Window(
        title = "Grim Locations",
        icon = APP_ICON,
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

fun fileReadsTest() {
    val locations = File("C:\\steam\\steamapps\\common\\Grim Dawn\\GrimInternals_TeleportList.txt")
    val player = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_Recursive\\player.gdc")
    val player2 = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveN\\player.gdc")
    val player3 = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveW\\player.gdc")
    val playerBak = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_Recursive\\player.gdc.bak")
    val player2Bak = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveN\\player.gdc.bak")
    val player3Bak = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_RecursiveW\\player.gdc.bak")
    val quests = File("C:\\Users\\gramb\\Documents\\My Games\\Grim Dawn\\save\\main\\_Recursive\\maps_survivalworld_i.map\\Elite\\quests.gdd")
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