package io.grimlocations.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

private val logger: Logger = LogManager.getLogger("ThreadLogger")

fun enableThreadLogging() {
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        logger.error("Exception in thread: ${t.name}", e)
    }
}