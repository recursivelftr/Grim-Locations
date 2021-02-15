package io.grimlocations.shared.util.extension

import androidx.compose.desktop.AppWindow

fun AppWindow.closeIfOpen() {
    if(!isClosed)
        close()
}