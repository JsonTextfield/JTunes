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
import com.jsontextfield.jtunes.entities.Genre
import com.jsontextfield.jtunes.ui.components.GenreList
import com.jsontextfield.jtunes.ui.components.SearchBar

@Composable
fun GenrePage(
    musicViewModel: MusicViewModel,
    genres: List<Genre> = ArrayList(),
    hintText: String = "",
    onItemClick: (Genre) -> Unit = {},
    onCreatePlaylist: () -> Unit = {},
) {
    val searchText by musicViewModel.searchText.collectAsState()
    Column {
        SearchBar(
            value = searchText,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            hintText = hintText,
            onTextChanged = { musicViewModel.onSearchTextChanged(it) },
            onCreatePlaylist = onCreatePlaylist,
        )
        val listState = rememberLazyListState()
        GenreList(
            listState = listState,
            genres = genres.filter { it.name.contains(searchText, true) },
            onItemClick = onItemClick,
        )
    }
}