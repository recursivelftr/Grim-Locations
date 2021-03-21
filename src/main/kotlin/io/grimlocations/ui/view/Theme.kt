package io.grimlocations.ui.view

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun GrimLocationsTheme(content: @Composable () -> Unit){
    MaterialTheme(
        colors = darkColors(),
        content = content
    )
}