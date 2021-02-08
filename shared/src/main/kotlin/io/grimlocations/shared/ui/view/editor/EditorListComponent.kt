package io.grimlocations.shared.ui.view.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.grimlocations.shared.data.dto.LocationDTO

@Composable
fun EditorListComponent(
    locations: List<LocationDTO>,
    selectedLocations: List<LocationDTO>,
    onSelectLocation: (LocationDTO) -> Unit
) {
    LazyColumn(/*modifier = Modifier.verticalScroll()*/) {
        items(
            locations.size,
            { locations[it].id }
        ) {
            val location = locations[it]
            Item(location, selectedLocations.contains(location), onSelectLocation)
            Divider()
        }
    }
}

@Composable
private fun Item(location: LocationDTO, isSelected: Boolean, onClick: (LocationDTO) -> Unit) {

    val modifier =
        if (isSelected)
            Modifier.background(MaterialTheme.colors.primarySurface)
        else
            Modifier

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable(onClick = { onClick(location) })
    ) {
        Text(location.name)
        Spacer(modifier = Modifier.width(40.dp))
    }
}