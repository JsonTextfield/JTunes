package com.jsontextfield.jtunes.ui.components

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.entities.Artist
import java.io.FileNotFoundException

@Composable
fun ArtistList(artists: List<Artist>, onItemClick: (artist: Artist) -> Unit = {}) {
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(artists, { artist -> artist.hashCode() }) { artist ->
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
            GalleryTile(
                title = artist.name,
                bitmap = bitmap,
                onClick = {
                    onItemClick(artist)
                }
            )
        }
    }
}