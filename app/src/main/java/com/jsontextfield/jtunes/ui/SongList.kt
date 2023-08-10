package com.jsontextfield.jtunes.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.SongListTile
import com.jsontextfield.jtunes.entities.Song

@Composable
fun SongList(songs: List<Song>, selectedSong: Song, onSongClicked: (song: Song) -> Unit) {
    LazyColumn {
        items(songs, { it.hashCode() }) {
            SongListTile(song = it, selected = selectedSong == it) {
                onSongClicked(it)
            }
        }
        item {
            Text(
                "${songs.size} songs",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}