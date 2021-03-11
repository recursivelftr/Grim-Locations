@file:Suppress("UNCHECKED_CAST")

package io.grimlocations.shared.framework.util

import kotlinx.coroutines.Deferred

suspend fun <T1, T2> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>
): Pair<T1, T2> {
    val l = kotlinx.coroutines.awaitAll(d1, d2)
    return Pair(
        l[0] as T1,
        l[1] as T2
    )
}

suspend fun <T1, T2, T3> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>,
    d3: Deferred<T3>
): Triple<T1, T2, T3> {
    val l = kotlinx.coroutines.awaitAll(d1, d2, d3)
    return Triple(
        l[0] as T1,
        l[1] as T2,
        l[2] as T3
    )
}

suspend fun <T1, T2, T3, T4> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>,
    d3: Deferred<T3>,
    d4: Deferred<T4>
): FourTuple<T1, T2, T3, T4> {
    val l = kotlinx.coroutines.awaitAll(d1, d2, d3, d4)
    return FourTuple(
        l[0] as T1,
        l[1] as T2,
        l[2] as T3,
        l[3] as T4
    )
}

suspend fun <T1, T2, T3, T4, T5> awaitAll(
    d1: Deferred<T1>,
    d2: Deferred<T2>,
    d3: Deferred<T3>,
    d4: Deferred<T4>,
    d5: Deferred<T5>
): FiveTuple<T1, T2, T3, T4, T5> {
    val l = kotlinx.coroutines.awaitAll(d1, d2, d3, d4, d5)
    return FiveTuple(
        l[0] as T1,
        l[1] as T2,
        l[2] as T3,
        l[3] as T4,
        l[4] as T5
    )
}