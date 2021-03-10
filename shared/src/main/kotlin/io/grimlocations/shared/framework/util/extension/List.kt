package io.grimlocations.shared.framework.util.extension

fun List<String>.removeAllBlank(): List<String> {
    val ml = mutableListOf<String>()
    this.forEach {
        if(it.isNotBlank())
            ml.add(it)
    }
    return ml
}

fun List<String?>.removeAllNullOrBlank(): List<String> {
    val ml = mutableListOf<String>()
    this.forEach {
        if(it != null && it.isNotBlank())
            ml.add(it)
    }
    return ml
}