package com.jsontextfield.jtunes.ui.components

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Song
import java.io.FileNotFoundException

@Composable
fun SongList(
    songs: List<Song>,
    selectedSong: Song,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onSongClicked: (song: Song) -> Unit = {},
) {
    Row {
        SectionIndex(
            data = songs.map { song -> song.title },
            listState = listState,
            selectedColour = colorResource(R.color.colourAccent)
        )
        LazyColumn(
            state = listState,
            modifier = modifier,
        ) {
            items(songs, { it.hashCode() }) { song ->
                ListTile(
                    title = song.title,
                    subtitle = song.artist,
                    onClick = { onSongClicked(song) },
                    selected = selectedSong == song,
                    leading = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .widthIn(0.dp, 50.dp)
                                .aspectRatio(1f)
                        ) {
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
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
                                ) {
                                    Icon(
                                        modifier = Modifier.align(Alignment.Center),
                                        imageVector = Icons.Rounded.MusicNote,
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    },
                    trailing = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.MoreVert, "")
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