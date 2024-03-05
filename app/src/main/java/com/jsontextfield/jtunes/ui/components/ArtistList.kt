package com.jsontextfield.jtunes.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.MusicLibrary
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Artist
import java.io.FileNotFoundException

@Composable
fun ArtistList(
    artists: List<Artist>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (artist: Artist) -> Unit = {},
) {
    Row {
        val context = LocalContext.current
        SectionIndex(
            data = artists.map { artist -> artist.name },
            listState = listState,
            selectedColour = colorResource(R.color.colourAccent)
        )
        LazyColumn(
            state = listState,
            modifier = modifier,
        ) {
            items(artists, { it.hashCode() }) { artist ->
                var bitmap: Bitmap? by remember { mutableStateOf(null) }
                LaunchedEffect(artist) {
                    bitmap = try {
                        val trackUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                            artist.id
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
                ListTile(
                    title = artist.name,
                    subtitle = pluralStringResource(
                        R.plurals.songs,
                        MusicLibrary.getInstance().songs.count { it.artist == artist.name },
                        MusicLibrary.getInstance().songs.count { it.artist == artist.name }),
                    onClick = { onItemClick(artist) },
                )
            }
            item {
                Text(
                    pluralStringResource(R.plurals.artists, artists.size, artists.size),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}