package io.grimlocations.ui.view.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.grimlocations.constant.DATETIME_FORMATTER
import io.grimlocations.framework.data.dto.NameDTO

enum class SelectionMode {
    SINGLE, MULTIPLE, RANGE
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun <T: NameDTO> NameDTOListComponent(
    dtos: Set<T>,
    selectedDTOS: Set<T>,
    onSelectDTOS: (Set<T>) -> Unit,
    getSelectionMode: () -> SelectionMode,
    rowHeight: Dp,
    rowWidth: Dp,
    stateVertical: LazyListState? = null,
    captureFocus: () -> Unit,
    noDtosMessage: String,
) {
    val primaryColor = MaterialTheme.colors.primary

    val actualStateVertical = stateVertical ?: remember(dtos) {
        LazyListState(
            0,
            0
        )
    }

    val scrollBarStyle = LocalScrollbarStyle.current.let {
        remember {
            it.copy(
                unhoverColor = primaryColor,
                hoverColor = primaryColor
            )
        }
    }
    Box(
        modifier = Modifier.width(rowWidth)
            .border(width = 3.dp, color = Color.DarkGray).clickable(onClick = captureFocus)
    ) {
        if (dtos.isEmpty()) {
            Text(
                noDtosMessage,
                modifier = Modifier.align(Alignment.Center)
            )

        } else {
            LazyColumn(state = actualStateVertical) {
                items(
                    dtos.size,
                    { dtos.elementAt(it).id }
                ) {
                    val dto = dtos.elementAt(it)
                    Item(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        dto = dto,
                        isSelected = selectedDTOS.contains(dto),
                        captureFocus = captureFocus,
                        onClick = { l ->
                            when (getSelectionMode()) {
                                SelectionMode.SINGLE -> {
                                    onSelectDTOS(setOf(l))
                                }
                                SelectionMode.MULTIPLE -> { //holding ctrl
                                    if (selectedDTOS.contains(l))
                                        onSelectDTOS(selectedDTOS.filter { ll -> ll != l }.toSet())
                                    else
                                        onSelectDTOS(selectedDTOS.toMutableSet().apply { add(l) })
                                }
                                SelectionMode.RANGE -> { //holding shift
                                    if (selectedDTOS.isEmpty()) {
                                        onSelectDTOS(setOf(l))
                                    } else if (!selectedDTOS.contains(l)) {
                                        val firstLocIndex = dtos.indexOf(selectedDTOS.first())
                                        val lIndex = dtos.indexOf(l)

                                        if (firstLocIndex > lIndex) {
                                            onSelectDTOS(selectedDTOS.toMutableSet().apply {
                                                addAll(
                                                    dtos.filter { aLoc ->
                                                        val aLocIndex = dtos.indexOf(aLoc)
                                                        aLocIndex in lIndex until firstLocIndex
                                                    }
                                                )
                                            })
                                        } else {
                                            onSelectDTOS(selectedDTOS.toMutableSet().apply {
                                                addAll(
                                                    dtos.filter { aLoc ->
                                                        val aLocIndex = dtos.indexOf(aLoc)
                                                        aLocIndex in (firstLocIndex + 1)..lIndex
                                                    }
                                                )
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = actualStateVertical,
            ),
            style = scrollBarStyle
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun <T: NameDTO> Item(
    rowHeight: Dp,
    rowWidth: Dp,
    dto: T,
    onClick: (T) -> Unit,
    isSelected: Boolean,
    captureFocus: () -> Unit,
) {

    val modifier =
        if (isSelected)
            Modifier.background(MaterialTheme.colors.primary)
        else
            Modifier

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(rowHeight)
            .width(rowWidth)
            .clickable(onClick = {
                captureFocus()
                onClick(dto)
            })

    ) {
        Spacer(modifier = Modifier.width(rowWidth * .02f))
        Box(modifier.width(rowWidth * .63f)) {
            Text(dto.name)
        }
//        Box(modifier.width(rowWidth * .2f)) {
//            with(location.coordinate) {
//                Text("$coordinate1, $coordinate2, $coordinate3")
//            }
//        }
//        Box(modifier.width(rowWidth * .25f)) {
//            Text(location.modified.toString())
//        }
        Box(modifier.width(rowWidth * .33f)) {
            Text(
                DATETIME_FORMATTER.format(dto.created),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.width(rowWidth * .02f))
    }
}