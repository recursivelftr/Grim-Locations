package io.grimlocations.shared.ui.view.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val transparentColor = Color(0, 0, 0, 0)
private val dropDownButtonWidth = 40.dp
private val dropDownItemHeight = 32.dp
private val dropDownSpacerHeight = 5.dp
private val dropDownAverageItemSize = dropDownItemHeight + dropDownSpacerHeight

@ExperimentalFoundationApi
@Composable
fun <K> DropdownBox(
    selected: Pair<K, String>,
    items: List<Pair<K, String>>,
    isOpen: Boolean,
    onOpen: () -> Unit,
    maxWidth: Dp,
    textFieldHeight: Dp = 56.dp, //Minimum height for a text field defined by compose
    onSelect: (Pair<K, String>) -> Unit,
    contentBelow: @Composable () -> Unit
) {

    val labelColor = MaterialTheme.colors.onSurface.let {
        val isLightColors = MaterialTheme.colors.isLight
        remember {
            val offset = if (isLightColors) .3f else -.3f
            it.copy(red = it.red + offset, blue = it.blue + offset, green = it.green + offset)
        }
    }
    val primaryColor = MaterialTheme.colors.primary
    val dropDownBackgroundColor = MaterialTheme.colors.onSurface.let { remember { it.copy(alpha = ContainerAlpha) } }
    val scrollBarStyle = ScrollbarStyleAmbient.current.let {
        remember {
            it.copy(
                unhoverColor = primaryColor,
                hoverColor = primaryColor
            )
        }
    }
    val stateVertical = rememberLazyListState()
    val maxColumnWidth = maxWidth + dropDownButtonWidth

    val displayValue =
        if (items.isEmpty())
            "None"
        else
            selected.second

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.width(maxColumnWidth)
        ) {
            Box {
                TextField(
                    value = displayValue,
                    readOnly = true,
                    onValueChange = {},
                    singleLine = true,
                    label = {
                        Text(
                            "Profile",
                            color = labelColor
                        )
                    },
                    modifier = Modifier.width(maxWidth).height(textFieldHeight)
                )
                Box(
                    modifier = Modifier
                        .background(color = transparentColor)
                        .height(textFieldHeight)
                        .width(maxWidth)
                        .clickable(onClick = onOpen)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                modifier = Modifier.size(dropDownButtonWidth),
                onClick = onOpen,

                ) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    "Open",
                    tint = primaryColor
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            contentBelow()

            if (isOpen) {
                Box(
                    modifier = Modifier
                        .width(maxColumnWidth)
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .width(maxWidth)
                            .align(Alignment.CenterStart)
                            .background(color = dropDownBackgroundColor)
                    ) {
                        LazyColumn(state = stateVertical) {
                            items(items.size) { index ->
                                TextBox(items[index].second, maxWidth - 10.dp) {
                                    onSelect(items[index])
                                }
                                Spacer(modifier = Modifier.height(dropDownSpacerHeight))
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(
                                scrollState = stateVertical,
                                itemCount = items.size,
                                averageItemSize = dropDownAverageItemSize
                            ),
                            style = scrollBarStyle
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TextBox(text: String = "Item", width: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier.height(dropDownItemHeight)
            .width(width)
            .background(color = transparentColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            Spacer(modifier = Modifier.width(15.dp)) //Amount of padding between the left most bound of the text field and the first letter of text
            Text(text = text)
        }
    }
}