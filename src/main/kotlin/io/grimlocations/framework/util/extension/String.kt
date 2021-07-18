package io.grimlocations.framework.util.extension

import java.io.File

fun String.endsWithOne(vararg suffix: String, ignoreCase: Boolean = false): Boolean {
    for (s in suffix) {
        if (this.endsWith(s, ignoreCase))
            return true
    }
    return false
}

fun String.startsWithOne(vararg prefix: String, ignoreCase: Boolean = false): Boolean {
    for (p in prefix) {
        if (this.startsWith(p, ignoreCase))
            return true
    }
    return false
}

fun String.asPathToFile(filename: String) =
    if (this.endsWithOne("/", "\\"))
        this + filename
    else
        this + File.separator + filename