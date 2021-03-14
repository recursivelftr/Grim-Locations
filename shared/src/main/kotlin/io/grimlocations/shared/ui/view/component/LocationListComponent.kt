package io.grimlocations.shared.ui.view.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.ShortcutsBuilderScope
import androidx.compose.ui.input.key.plus
import androidx.compose.ui.input.key.shortcuts
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.constant.DATETIME_FORMATTER
import io.grimlocations.shared.data.dto.LocationDTO

@ExperimentalFoundationApi
@Composable
fun LocationListComponent(
    locations: Set<LocationDTO>,
    selectedLocations: Set<LocationDTO>,
    onSelectLocations: (Set<LocationDTO>) -> Unit,
    rowHeight: Dp,
    rowWidth: Dp,
    stateVertical: LazyListState,
) {
    val primaryColor = MaterialTheme.colors.primary

    val scrollBarStyle = ScrollbarStyleAmbient.current.let {
        remember {
            it.copy(
                unhoverColor = primaryColor,
                hoverColor = primaryColor
            )
        }
    }
    Box(modifier = Modifier.width(rowWidth)
        .border(width = 3.dp, color = Color.DarkGray)
    ) {
        if(locations.isEmpty()) {
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
                        rowHeight,
                        rowWidth,
                        location,
                        selectedLocations.contains(location),
                        onSelectLocations
                    )
                    Divider()
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = stateVertical,
                itemCount = locations.size,
                averageItemSize = rowHeight
            ),
            style = scrollBarStyle
        )
    }
}

@Composable
private fun Item(
    rowHeight: Dp,
    rowWidth: Dp,
    location: LocationDTO,
    isSelected: Boolean,
    onClick: (Set<LocationDTO>) -> Unit,
) {

    val modifier =
        if (isSelected)
            Modifier.background(MaterialTheme.colors.primary)
        else
            Modifier

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(rowHeight)
            .width(rowWidth)
            .clickable(onClick = { onClick(setOf(location)) })
    ) {
        Spacer(modifier = Modifier.width(rowWidth *.02f))
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
            Text(DATETIME_FORMATTER.format(location.created),
            modifier = Modifier.align(Alignment.CenterEnd))
        }
        Spacer(modifier = Modifier.width(rowWidth *.02f))
    }
}