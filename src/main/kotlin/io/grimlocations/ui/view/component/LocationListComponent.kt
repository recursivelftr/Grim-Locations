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
import io.grimlocations.data.dto.LocationDTO

enum class SelectionMode {
    SINGLE, MULTIPLE, RANGE
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun LocationListComponent(
    locations: Set<LocationDTO>,
    selectedLocations: Set<LocationDTO>,
    onSelectLocations: (Set<LocationDTO>) -> Unit,
    getSelectionMode: () -> SelectionMode,
    rowHeight: Dp,
    rowWidth: Dp,
    stateVertical: LazyListState,
    captureFocus: () -> Unit,
) {
    val primaryColor = MaterialTheme.colors.primary

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
        if (locations.isEmpty()) {
            Text(
                "No Locations",
                modifier = Modifier.align(Alignment.Center)
            )

        } else {
            LazyColumn(state = stateVertical) {
                items(
                    locations.size,
                    { locations.elementAt(it).id }
                ) {
                    val location = locations.elementAt(it)
                    Item(
                        rowHeight = rowHeight,
                        rowWidth = rowWidth,
                        location = location,
                        isSelected = selectedLocations.contains(location),
                        captureFocus = captureFocus,
                        onClick = { l ->
                            when (getSelectionMode()) {
                                SelectionMode.SINGLE -> {
                                    onSelectLocations(setOf(l))
                                }
                                SelectionMode.MULTIPLE -> { //holding ctrl
                                    if (selectedLocations.contains(l))
                                        onSelectLocations(selectedLocations.filter { ll -> ll != l }.toSet())
                                    else
                                        onSelectLocations(selectedLocations.toMutableSet().apply { add(l) })
                                }
                                SelectionMode.RANGE -> { //holding shift
                                    if (selectedLocations.isEmpty()) {
                                        onSelectLocations(setOf(l))
                                    } else if (!selectedLocations.contains(l)) {
                                        val firstLoc = selectedLocations.first()

                                        if (firstLoc.order > l.order) {
                                            onSelectLocations(selectedLocations.toMutableSet().apply {
                                                addAll(
                                                    locations.filter { aLoc ->
                                                        aLoc.order < firstLoc.order && aLoc.order >= l.order
                                                    }
                                                )
                                            })
                                        } else {
                                            onSelectLocations(selectedLocations.toMutableSet().apply {
                                                addAll(
                                                    locations.filter { aLoc ->
                                                        aLoc.order > firstLoc.order && aLoc.order <= l.order
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
                scrollState = stateVertical,
            ),
            style = scrollBarStyle
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun Item(
    rowHeight: Dp,
    rowWidth: Dp,
    location: LocationDTO,
    onClick: (LocationDTO) -> Unit,
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
                onClick(location)
            })

    ) {
        Spacer(modifier = Modifier.width(rowWidth * .02f))
        Box(modifier.width(rowWidth * .63f)) {
            Text(location.name)
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
                DATETIME_FORMATTER.format(location.created),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.width(rowWidth * .02f))
    }
}