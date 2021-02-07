package io.grimlocations.shared.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun GDLocationManagerTheme(content: @Composable () -> Unit){
    MaterialTheme(
        colors = darkColors(),
        content = content
    )
}