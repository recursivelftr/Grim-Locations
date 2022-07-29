package io.grimlocations.constant

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import java.awt.image.BufferedImage
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO


const val TITLE = "Grim Locations"
const val VERSION = "0.1.0"

val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")

lateinit var APP_ICON: Painter