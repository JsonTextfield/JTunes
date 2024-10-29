package com.jsontextfield.jtunes.ui.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.ui.components.SearchBar
import com.jsontextfield.jtunes.ui.viewmodels.MusicState

@Composable
fun PlaylistPage(
    musicState: MusicState = MusicState(),
    playlists: List<Playlist> = ArrayList(),
    hintText: String = "",
    onItemClick: (Playlist) -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
) {
    Column {
        SearchBar(
            value = musicState.searchText,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            hintText = hintText,
            onTextChanged = onSearchTextChanged
        )
        val listState = rememberLazyListState()
        PlaylistList(
            listState = listState,
            playlists = playlists,
            onItemClick = onItemClick,
        )
    }
}