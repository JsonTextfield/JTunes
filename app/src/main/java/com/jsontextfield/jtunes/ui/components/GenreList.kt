package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.entities.Genre

@Composable
fun GenreList(genres: List<Genre>, onItemClick: (genre: Genre) -> Unit = {}) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(genres, { genre -> genre.hashCode() }) { genre ->
            GalleryTile(
                title = genre.name,
                onClick = {
                    onItemClick(genre)
                }
            )
        }
    }
}