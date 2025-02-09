package com.jsontextfield.jtunes.ui.songs

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.CoverArtSmall
import com.jsontextfield.jtunes.ui.components.ListTile
import com.jsontextfield.jtunes.ui.components.SectionIndex
import com.jsontextfield.jtunes.ui.viewmodels.SongSortMode
import java.io.FileNotFoundException

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    songs: List<Song> = emptyList(),
    selectedSong: Song? = null,
    listState: LazyListState = rememberLazyListState(),
    sortMode: SongSortMode = SongSortMode.Title,
    onItemClicked: (song: Song) -> Unit = {},
) {
    Row {
        SectionIndex(
            data = songs.map { song ->
                if (sortMode == SongSortMode.Title) song.title
                else song.artist
            },
            listState = listState,
            selectedColour = MaterialTheme.colorScheme.primary
        )
        LazyColumn(
            state = listState,
            modifier = modifier,
        ) {
            items(songs) { song ->
                ListTile(
                    title = song.title,
                    subtitle = song.artist,
                    onClick = { onItemClicked(song) },
                    selected = selectedSong == song,
                    leading = {
                        val context = LocalContext.current
                        var bitmap: Bitmap? by remember { mutableStateOf(null) }
                        LaunchedEffect(Unit) {
                            bitmap = try {
                                val trackUri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    song.id
                                )
                                context.contentResolver.loadThumbnail(
                                    trackUri,
                                    Size(512, 512),
                                    null
                                )
                            } catch (e: FileNotFoundException) {
                                null
                            }
                        }
                        CoverArtSmall(bitmap)
                    },
                    trailing = {
                        var showSongMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showSongMenu = true }) {
                            Icon(Icons.Rounded.MoreVert, "")
                            DropdownMenu(
                                expanded = showSongMenu,
                                onDismissRequest = { showSongMenu = false }) {
                                DropdownMenuItem(
                                    text = { Text("Play next") },
                                    onClick = { showSongMenu = false })
                                DropdownMenuItem(
                                    text = { Text("Add to queue") },
                                    onClick = { showSongMenu = false })
                                DropdownMenuItem(
                                    text = { Text("Details") },
                                    onClick = { showSongMenu = false })
                            }
                        }
                    },
                )
            }
            item {
                Text(
                    pluralStringResource(R.plurals.songs, songs.size, songs.size),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}