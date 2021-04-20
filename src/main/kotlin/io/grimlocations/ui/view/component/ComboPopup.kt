package io.grimlocations.ui.view.component

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.grimlocations.ui.view.GrimLocationsTheme
import io.grimlocations.util.extension.closeIfOpen

private val transparentColor = Color(0, 0, 0, 0)
private val dropDownBackgroundColorDark = Color(47, 47, 47)
private val dropDownBackgroundColorLight = Color(224, 224, 224)
private val dropDownButtonWidth = 40.dp
private val dropDownItemHeight = 32.dp
private val dropDownSpacerHeight = 5.dp
private val dropDownAverageItemHeight = dropDownItemHeight + dropDownSpacerHeight
private val dropDownHeaderHeight = dropDownAverageItemHeight + 5.dp

@ExperimentalFoundationApi
@Composable
fun <K> ComboPopup(
    title: String,
    selected: Triple<K, String, Color?>?,
    items: List<Triple<K, String, Color?>>,
    emptyItemsMessage: String = "None",
    onOpen: (previousWindow: AppWindow?, newWindow: AppWindow) -> Unit,
    onClose: (() -> Unit)? = null,
    width: Dp,
    popupMaxHeight: Dp = 260.dp,
    textFieldHeight: Dp = 56.dp, //Minimum height for a text field defined by compose
    onSelect: (Triple<K, String, Color?>) -> Unit,
    disabled: Boolean = false
) {

    val labelColor = MaterialTheme.colors.onSurface.let {
        val isLightColors = MaterialTheme.colors.isLight
        remember {
            val offset = if (isLightColors) .3f else -.3f
            it.copy(red = it.red + offset, blue = it.blue + offset, green = it.green + offset)
        }
    }
    val primaryColor = MaterialTheme.colors.primary
    val dropDownBackgroundColor: Color
    val textColor: Color
    if (MaterialTheme.colors.isLight) {
        dropDownBackgroundColor = dropDownBackgroundColorLight
        textColor = Color.Unspecified
    } else {
        dropDownBackgroundColor = dropDownBackgroundColorDark
        textColor = Color.White
    }
    val maxColumnWidth = width + dropDownButtonWidth

    val displayValue =
        if (items.isEmpty())
            emptyItemsMessage
        else
            selected?.second ?: items[0].second


    val previousWindow = remember { mutableStateOf<AppWindow?>(null) }

    val openPopup: () -> Unit
    if (disabled) {
        openPopup = {}
    } else {
        openPopup = {
            if (items.isNotEmpty()) {
                val possibleHeight = dropDownHeaderHeight + (items.size * dropDownAverageItemHeight.value).dp
                openPopupWindow(
                    title,
                    labelColor,
                    width,
                    if (possibleHeight < popupMaxHeight) possibleHeight else popupMaxHeight,
                    items,
                    primaryColor,
                    dropDownBackgroundColor,
                    textColor,
                    {
                        previousWindow.value?.closeIfOpen()
                        onSelect(it)
                    },
                    {
                        previousWindow.value?.closeIfOpen()
                        onOpen(previousWindow.value, it)
                        previousWindow.value = it
                    },
                    onClose
                )
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    enabled = !disabled,
                    onValueChange = {},
                    singleLine = true,
                    label = {
                        Text(
                            title,
                            color = labelColor
                        )
                    },
                    modifier = Modifier.width(width).height(textFieldHeight)
                )
                Box(
                    modifier = Modifier
                        .background(color = transparentColor)
                        .height(textFieldHeight)
                        .width(width)
                        .clickable(onClick = openPopup)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                modifier = Modifier.size(dropDownButtonWidth),
                onClick = openPopup,
            ) {
                Icon(
                    Icons.Default.Menu,
                    "Open",
                    tint = primaryColor
                )
            }
        }
    }
}

@ExperimentalFoundationApi
private fun <K> openPopupWindow(
    title: String,
    titleColor: Color,
    width: Dp,
    height: Dp,
    items: List<Triple<K, String, Color?>>,
    primaryColor: Color,
    dropDownBackgroundColor: Color,
    textColor: Color,
    onSelect: (Triple<K, String, Color?>) -> Unit,
    onOpen: (AppWindow) -> Unit,
    onClose: (() -> Unit)?
) {
    var isNotOpen = true
    Window(
        undecorated = true,
        size = IntSize(width.value.toInt(), height.value.toInt()),
        onDismissRequest = onClose
    ) {
        GrimLocationsTheme {
            val window = LocalAppWindow.current
            SideEffect {
                if (isNotOpen) {
                    isNotOpen = false
                    onOpen(window)
                }
            }

//            val primaryColor = MaterialTheme.colors.primary
//            val dropDownBackgroundColor = MaterialTheme.colors.surface.let { remember { it.copy(alpha = ContainerAlpha) } }
            val scrollBarStyle = ScrollbarStyleAmbient.current.let {
                remember {
                    it.copy(
                        unhoverColor = primaryColor,
                        hoverColor = primaryColor
                    )
                }
            }
            val stateVertical = rememberLazyListState()
            Surface(
                color = dropDownBackgroundColor,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(dropDownAverageItemHeight).fillMaxWidth()
                    ) {
                        Row {
                            Spacer(modifier = Modifier.width(15.dp)) //Amount of padding between the left most bound of the text field and the first letter of text
                            Text(text = title, color = titleColor)
                        }
                        IconButton(
                            onClick = { window.close() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                "Close",
                                tint = primaryColor
                            )
                        }
                    }
                    Row {
                        Spacer(modifier = Modifier.width(15.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(titleColor))
                    }

                    Spacer(modifier = Modifier.height(dropDownSpacerHeight))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LazyColumn(state = stateVertical) {

                            items(items.size) { index ->
                                TextBox(items[index].second, textColor, width - 10.dp) {
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
                                averageItemSize = dropDownAverageItemHeight
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
private fun TextBox(text: String, textColor: Color, width: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier.height(dropDownItemHeight)
            .width(width)
            .background(color = transparentColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row {
            Spacer(modifier = Modifier.width(15.dp)) //Amount of padding between the left most bound of the text field and the first letter of text
            Text(text = text, color = textColor)
        }
    }
}