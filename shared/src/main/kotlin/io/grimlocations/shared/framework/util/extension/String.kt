package io.grimlocations.shared.framework.util.extension

fun String.endsWithOne(vararg suffix: String, ignoreCase: Boolean = false): Boolean {
    for (s in suffix) {
        if(this.endsWith(s, ignoreCase))
            return true
    }
    return false
}