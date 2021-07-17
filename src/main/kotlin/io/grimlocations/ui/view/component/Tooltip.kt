package io.grimlocations.ui.view.component

import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val dropDownBackgroundColorDark = Color(47, 47, 47)
private val dropDownBackgroundColorLight = Color(224, 224, 224)

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun Tooltip(text: String, content: @Composable () -> Unit) {
    BoxWithTooltip(
        tooltip = {
            // composable tooltip content
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = dropDownBackgroundColorDark,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(10.dp)
                )
            }
        },
    ) {
        content()
    }
}