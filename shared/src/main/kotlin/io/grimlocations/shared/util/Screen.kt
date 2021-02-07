package io.grimlocations.shared.util

import java.awt.GraphicsEnvironment

fun getResolution(): Pair<Int, Int> {
    val screen = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    return screen.displayMode.width to screen.displayMode.height
}