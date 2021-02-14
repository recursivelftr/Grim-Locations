package io.grimlocations.shared.ui.view.component

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@ExperimentalFoundationApi
@Composable
fun <K> DropdownBox(
    selected: Pair<K, String>,
    items: List<Pair<K, String>>,
    isOpen: Boolean,
    onOpen: () -> Unit,
    maxWidth: Dp,
    maxHeight: Dp,
    onSelect: (Pair<K, String>) -> Unit,
    contentBelow: @Composable () -> Unit
) {

    val stateVertical = rememberLazyListState()
    val stateHorizontal = rememberScrollState(0f)

    val windowHeight = LocalAppWindow.current.window.height

    val displayValue =
        if (items.isEmpty())
            "None"
        else
            selected.second

    Box(modifier = Modifier.width(maxWidth + 40.dp)) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = displayValue,
                    readOnly = true,
                    enabled = false,
                    onValueChange = {},
                    singleLine = true,
                    label = {
                        Text("Profile", style = TextStyle(fontSize = 15.sp))
                    },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.width(maxWidth)
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = onOpen
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        "Open",
                    )
                }
            }
            Box {
                if (isOpen) {
                    Box(modifier = Modifier.layout(adjustDropdownBoxHeight(windowHeight))) {
                        LazyColumn {
                            items(items.size) { index ->
                                TextBox(items[index].second, maxWidth) {
                                    onSelect(items[index])
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(
                                scrollState = stateVertical,
                                itemCount = items.size,
                                averageItemSize = 37.dp // TextBox height + Spacer height
                            )
                        )
                        HorizontalScrollbar(
                            modifier = Modifier.align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(end = 12.dp),
                            adapter = rememberScrollbarAdapter(stateHorizontal)
                        )
                    }
                }

                contentBelow()
            }
        }
    }

}

@Composable
fun TextBox(text: String = "Item", width: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier.height(32.dp)
            .width(width)
            .background(color = Color(0, 0, 0, 20))
            .clickable(onClick = onClick)
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

private fun adjustDropdownBoxHeight(
    windowHeight: Int
): MeasureScope.(Measurable, Constraints) -> MeasureResult =
    { measurable, constraints ->

        val placeable = measurable.measure(constraints)
        AlignmentLine
measurable.parentData
        layout(constraints.maxWidth, placeable.height) {

        }
    }