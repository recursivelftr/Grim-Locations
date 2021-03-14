package io.grimlocations.shared.framework.util

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

@Composable
fun LaunchedEffect(
    block: suspend CoroutineScope.() -> Unit
) = androidx.compose.runtime.LaunchedEffect(true, block)