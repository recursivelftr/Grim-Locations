package io.grimlocations.shared.framework.util.extension

fun List<String>.removeAllBlank(): List<String> {
    val ml = mutableListOf<String>()
    this.forEach {
        if (it.isNotBlank())
            ml.add(it)
    }
    return ml
}

fun List<String?>.removeAllNullOrBlank(): List<String> {
    val ml = mutableListOf<String>()
    this.forEach {
        if (it != null && it.isNotBlank())
            ml.add(it)
    }
    return ml
}

//checks if the ints are sequential. Returns false if empty
fun List<Int>.isSequential(sortFirst: Boolean = false): Boolean {
    if (this.isEmpty())
        return false

    val list = if (sortFirst)
        this.sorted()
    else
        this

    var prevInt = list[0]
    for (i in list.drop(1)) {
        if (prevInt != (i - 1)) {
            return false
        } else {
            prevInt = i
        }
    }
    return true
}