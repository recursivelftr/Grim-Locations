package io.grimlocations.shared.framework.util

import kotlinx.coroutines.CoroutineScope

fun LaunchedEffect(
    block: suspend CoroutineScope.() -> Unit
) = androidx.compose.runtime.LaunchedEffect(true, block)