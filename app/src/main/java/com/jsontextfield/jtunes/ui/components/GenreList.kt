package com.jsontextfield.jtunes.ui.components

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jsontextfield.jtunes.entities.Genre
import java.io.FileNotFoundException

@Composable
fun GenreList(
    genres: List<Genre>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (genre: Genre) -> Unit = {},
) {
    val context = LocalContext.current
    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(genres, { genre -> genre.hashCode() }) { genre ->
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            LaunchedEffect(genre) {
                bitmap = try {
                    val trackUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                        genre.id
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
            GenreListTile(genre = genre) {
                onItemClick(genre)
            }
        }
    }
}