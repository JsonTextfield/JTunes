package com.jsontextfield.jtunes.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.MusicViewModel
import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.ui.components.AlbumList
import com.jsontextfield.jtunes.ui.components.SearchBar

@Composable
fun AlbumPage(
    musicViewModel: MusicViewModel,
    albums: List<Album> = ArrayList(),
    hintText: String = "",
    onItemClick: (Album) -> Unit = {},
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
        AlbumList(
            albums = albums.filter { album ->
                album.title.contains(searchText, true)
            },
            onItemClick = onItemClick,
        )
    }
}