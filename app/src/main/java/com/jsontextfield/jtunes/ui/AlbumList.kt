package com.jsontextfield.jtunes.ui

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
import com.jsontextfield.jtunes.entities.Album
import java.io.FileNotFoundException

@Composable
fun AlbumList(albums: List<Album>) {
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            items(albums, { album -> album.hashCode() }) { album ->
                var bitmap: Bitmap? by remember { mutableStateOf(null) }
                LaunchedEffect(album) {
                    bitmap = try {
                        val trackUri = ContentUris.withAppendedId(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            album.id
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
                    title = "${album.title}\n${album.artist} • ${album.date}",
                    bitmap = bitmap
                )
            }
        },
    )
}