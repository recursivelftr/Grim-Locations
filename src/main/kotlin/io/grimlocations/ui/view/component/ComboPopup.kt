package io.grimlocations.ui.view.component

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import io.grimlocations.ui.view.GrimLocationsTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val transparentColor = Color(0, 0, 0, 0)
private val dropDownBackgroundColorDark = Color(47, 47, 47)
private val dropDownBackgroundColorLight = Color(224, 224, 224)
private val dropDownButtonWidth = 40.dp
private val dropDownItemHeight = 32.dp
private val dropDownSpacerHeight = 5.dp
private val dropDownAverageItemHeight = dropDownItemHeight + dropDownSpacerHeight
private val dropDownHeaderHeight = dropDownAverageItemHeight + 5.dp

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun <K> ComboPopup(
    title: String,
    selected: Triple<K, String, Color?>?,
    items: List<Triple<K, String, Color?>>,
    emptyItemsMessage: String = "None",
    width: Dp,
    popupMaxHeight: Dp = 560.dp,
    textFieldHeight: Dp = 56.dp, //Minimum height for a text field defined by compose
    onSelect: (Triple<K, String, Color?>) -> Unit,
    disabled: Boolean = false,
    controlOnLeft: Boolean = false,
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

    var isPopupOpen by remember { mutableStateOf(false) }
    val openPopup = {
        if(items.isNotEmpty() && !disabled) {
            isPopupOpen = true
        }
    }
    val closePopup = {
        if(items.isNotEmpty() && !disabled) {
            isPopupOpen = false
        }
    }

    if (items.isNotEmpty() && !disabled && isPopupOpen) {
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
            closePopup,
            onSelect,
        )
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
            if(controlOnLeft) {
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
                Spacer(modifier = Modifier.width(10.dp))
            }
            Box {
                TextField(
                    value = displayValue,
                    readOnly = true,
                    enabled = !disabled,
                    onValueChange = {},
                    singleLine = true,
                    textStyle = selected?.third?.let { LocalTextStyle.current.copy(color = it)  } ?: LocalTextStyle.current,
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
            if(!controlOnLeft) {
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
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
private fun <K> openPopupWindow(
    title: String,
    titleColor: Color,
    width: Dp,
    height: Dp,
    items: List<Triple<K, String, Color?>>,
    primaryColor: Color,
    dropDownBackgroundColor: Color,
    textColor: Color,
    onClose: () -> Unit,
    onSelect: (Triple<K, String, Color?>) -> Unit,
) {

    val dialogState =
        rememberDialogState(size = WindowSize(width, height), position = WindowPosition.Aligned(Alignment.Center))

    Dialog(
        state = dialogState,
        title = title,
        onCloseRequest = onClose,
        undecorated = true,
    ) {
        GrimLocationsTheme {
//            val primaryColor = MaterialTheme.colors.primary
//            val dropDownBackgroundColor = MaterialTheme.colors.surface.let { remember { it.copy(alpha = ContainerAlpha) } }
            val scrollBarStyle = LocalScrollbarStyle.current.let {
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
                            onClick = onClose,
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
                                val (_, text, color) = items[index]
                                TextBox(text, color ?: textColor, width - 10.dp) {
                                    onSelect(items[index])
                                    onClose()
                                }
                                Spacer(modifier = Modifier.height(dropDownSpacerHeight))
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(
                                scrollState = stateVertical,
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