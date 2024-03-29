package com.jsontextfield.jtunes.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.MusicViewModel
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.ui.components.PlaylistList
import com.jsontextfield.jtunes.ui.components.SearchBar

@Composable
fun PlaylistPage(
    musicViewModel: MusicViewModel,
    playlists: List<Playlist> = ArrayList(),
    hintText: String = "",
    onItemClick: (Playlist) -> Unit = {},
) {
    val searchText by musicViewModel.searchText.collectAsState()
    Column {
        SearchBar(
            value = searchText,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            hintText = hintText,
            onTextChanged = { text ->
                musicViewModel.onSearchTextChanged(text)
            }
        )
        val listState = rememberLazyListState()
        PlaylistList(
            listState = listState,
            playlists = playlists.sortedBy { it.title }.filter { playlist ->
                playlist.title.contains(searchText, true)
            },
            onItemClick = onItemClick,
        )
    }
}