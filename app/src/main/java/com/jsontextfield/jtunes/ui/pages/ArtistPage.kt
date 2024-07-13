package com.jsontextfield.jtunes.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.entities.Artist
import com.jsontextfield.jtunes.ui.components.ArtistList
import com.jsontextfield.jtunes.ui.components.SearchBar
import com.jsontextfield.jtunes.ui.viewmodels.MusicState

@Composable
fun ArtistPage(
    musicState: MusicState = MusicState(),
    artists: List<Artist> = ArrayList(),
    hintText: String = "",
    onItemClick: (Artist) -> Unit = {},
    onCreatePlaylist: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
) {
    Column {
        SearchBar(
            value = musicState.searchText,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            hintText = hintText,
            onTextChanged = onSearchTextChanged,
            onCreatePlaylist = onCreatePlaylist,
        )
        val listState = rememberLazyListState()
        ArtistList(
            listState = listState,
            artists = artists,
            onItemClick = onItemClick,
        )
    }
}