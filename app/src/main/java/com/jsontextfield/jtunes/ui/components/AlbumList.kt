package com.jsontextfield.jtunes.ui.components

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.jsontextfield.jtunes.MusicLibrary
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.ui.viewmodels.AlbumSortMode

@Composable
fun AlbumList(
    albums: List<Album>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onItemClick: (album: Album) -> Unit = {},
    showAsList: Boolean = true,
    sortMode: AlbumSortMode = AlbumSortMode.Title,
) {
    val context = LocalContext.current
    if (showAsList) {
        Row {
            SectionIndex(
                data = albums.map { album ->
                    if (sortMode == AlbumSortMode.Title) album.title
                    else album.artist
                },
                listState = listState,
                selectedColour = MaterialTheme.colorScheme.primary
            )
            LazyColumn(
                state = listState,
                modifier = modifier,
            ) {
                items(albums, { it.hashCode() }) { album ->
                    ListTile(
                        title = album.title,
                        subtitle = "${album.artist} • " + pluralStringResource(
                            R.plurals.songs,
                            MusicLibrary.getInstance().songs.count { it.album == album.title },
                            MusicLibrary.getInstance().songs.count { it.album == album.title }),
                        leading = {
                            var bitmap: Bitmap? by remember { mutableStateOf(null) }
                            LaunchedEffect(album) {
                                val uri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                    album.id
                                )
                                bitmap = getCoverArt(context, uri)
                            }
                            CoverArtSmall(bitmap)
                        },
                        onClick = { onItemClick(album) },
                    )
                }
                item {
                    Text(
                        pluralStringResource(R.plurals.albums, albums.size, albums.size),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                items(albums, { album -> album.hashCode() }) { album ->
                    var bitmap: Bitmap? by remember { mutableStateOf(null) }
                    LaunchedEffect(album) {
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            album.id
                        )
                        bitmap = getCoverArt(context, uri)
                    }
                    GalleryTile(
                        title = "${album.title}\n${album.artist}",
                        bitmap = bitmap,
                        onClick = { onItemClick(album) }
                    )
                }
                item {
                    Text(
                        pluralStringResource(R.plurals.albums, albums.size, albums.size),
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            },
        )
    }
}