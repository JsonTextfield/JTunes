package com.jsontextfield.jtunes.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Shuffle
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
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.SearchBar
import com.jsontextfield.jtunes.ui.components.SongList
import com.jsontextfield.jtunes.ui.components.menu.RadioMenuItem
import com.jsontextfield.jtunes.ui.viewmodels.MusicState
import com.jsontextfield.jtunes.ui.viewmodels.SongSortMode

@Composable
fun SongPage(
    musicState: MusicState = MusicState(),
    songs: List<Song> = ArrayList(),
    hintText: String = "",
    onItemClick: (song: Song) -> Unit = {},
    onCreatePlaylist: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onSearchTextChanged: (String) -> Unit = {},
) {
    Column {
        val searchText = musicState.searchText
        val isPlaying = musicState.isPlaying
        val selectedSong = musicState.currentSong
        SearchBar(
            value = searchText,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            hintText = hintText,
            onTextChanged = onSearchTextChanged,
            onCreatePlaylist = onCreatePlaylist
        )
        var showSortMenu by remember { mutableStateOf(false) }
        var songSortMode by remember { mutableStateOf(SongSortMode.Title) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { showSortMenu = true }) {
                Icon(Icons.AutoMirrored.Rounded.Sort, null)
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }) {
                    SongSortMode.entries.map {
                        RadioMenuItem(
                            title = it.name,
                            selected = it == songSortMode
                        ) {
                            songSortMode = it
                            showSortMenu = false
                        }
                    }
                }
            }
            IconButton(onClick = onShuffleClick) {
                Icon(Icons.Rounded.Shuffle, null)
            }
            IconButton(
                onClick = onQueueClick,
                enabled = isPlaying,
            ) {
                Icon(Icons.AutoMirrored.Rounded.QueueMusic, null)
            }
        }
        val listState = rememberLazyListState()
        SongList(
            songs = songs,
            selectedSong = selectedSong,
            listState = listState,
            modifier = Modifier.weight(1f),
            sortMode = songSortMode,
            onItemClicked = onItemClick,
        )
    }
}