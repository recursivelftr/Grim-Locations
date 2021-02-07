package io.grimlocations.editor

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.ui.GDLocationManagerTheme
import io.grimlocations.shared.util.getResolution
import org.apache.logging.log4j.LogManager

val logger = LogManager.getLogger()

fun main() = Window(
    title = "Editor",
    size = managerDimensions()
) {
    logger.debug("test")
    var text by remember { mutableStateOf("Hello, World!") }

    GDLocationManagerTheme {
        Surface(color = Color.Black) {
            Button(onClick = {
                text = "Hello, Desktop!"
            }, modifier = Modifier.padding(24.dp)) {
                Text(text)
            }
        }
    }
}

fun managerDimensions(): IntSize {
    val xPercent = .5
    val yPercent = .7
    val (x, y) = getResolution()

    return IntSize(768, 1024)

//    return IntSize((x * xPercent).toInt(), (y * yPercent).toInt()).also {
//        println("Width: ${it.width} --- Height: ${it.height}")
//    }
}