package io.grimlocations.framework.util

inline fun <T1 : Any, T2 : Any>
        guardAlso(p1: T1?, p2: T2?, block: (T1, T2) -> Unit): Pair<T1, T2>? {
    return if (p1 != null && p2 != null) {
        block(p1, p2)
        Pair(p1, p2)
    } else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any>
        guardAlso(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> Unit): Triple<T1, T2, T3>? {
    return if (p1 != null && p2 != null && p3 != null) {
        block(p1, p2, p3)
        Triple(p1, p2, p3)
    } else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any>
        guardAlso(p1: T1?, p2: T2?, p3: T3?, p4: T4?, block: (T1, T2, T3, T4) -> Unit): FourTuple<T1, T2, T3, T4>? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) {
        block(p1, p2, p3, p4)
        FourTuple(p1, p2, p3, p4)
    } else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any>
        guardAlso(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    p5: T5?,
    block: (T1, T2, T3, T4, T5) -> Unit
): FiveTuple<T1, T2, T3, T4, T5>? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) {
        block(p1, p2, p3, p4, p5)
        FiveTuple(p1, p2, p3, p4, p5)
    } else null
}