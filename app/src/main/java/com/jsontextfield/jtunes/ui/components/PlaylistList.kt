package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Playlist

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (playlist: Playlist) -> Unit = {},
) {
    Row {
        SectionIndex(
            data = playlists.map { playlist -> playlist.title },
            listState = listState,
            selectedColour = colorResource(R.color.colourAccent)
        )
        LazyColumn(
            state = listState,
            modifier = modifier,
        ) {
            items(playlists, { it.hashCode() }) { playlist ->
                ListTile(
                    title = playlist.title,
                    onClick = { onItemClick(playlist) },
                )
            }
            item {
                Text(
                    "${playlists.size} playlists",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}