package com.jsontextfield.jtunes.ui.albums

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.ui.components.SearchBar
import com.jsontextfield.jtunes.ui.components.menu.RadioMenuItem
import com.jsontextfield.jtunes.ui.viewmodels.AlbumSortMode
import com.jsontextfield.jtunes.ui.viewmodels.MusicState

@Composable
fun AlbumPage(
    musicState: MusicState = MusicState(),
    albums: List<Album> = ArrayList(),
    hintText: String = "",
    onItemClick: (Album) -> Unit = {},
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

        var showAsList by remember { mutableStateOf(true) }
        var showSortMenu by remember { mutableStateOf(false) }
        var albumSortMode by remember { mutableStateOf(AlbumSortMode.Title) }
        val listState = rememberLazyListState()

        Row {
            IconButton(onClick = { showAsList = !showAsList }) {
                Icon(
                    if (showAsList) Icons.Rounded.GridView else Icons.AutoMirrored.Rounded.List,
                    null,
                )
            }

            IconButton(
                onClick = { showSortMenu = true },
            ) {
                Icon(Icons.AutoMirrored.Rounded.Sort, null)
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }) {
                    AlbumSortMode.entries.map {
                        RadioMenuItem(
                            title = it.name,
                            selected = it == albumSortMode
                        ) {
                            albumSortMode = it
                            showSortMenu = false
                        }
                    }
                }
            }
        }
        AlbumList(
            albums = albums,
            listState = listState,
            onItemClick = onItemClick,
            showAsList = showAsList,
            sortMode = albumSortMode,
        )
    }
}