package io.grimlocations.constant

import java.awt.image.BufferedImage
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO


const val TITLE = "Grim Locations"
const val VERSION = "0.1.0"

val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")

val APP_ICON by lazy {
    ImageIO.read(Thread.currentThread().contextClassLoader.getResourceAsStream("Location512PurpleBlack.png")) as BufferedImage
}