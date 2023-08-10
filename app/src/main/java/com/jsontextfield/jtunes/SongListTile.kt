package com.jsontextfield.jtunes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song

@Preview
@Composable
fun SongListTilePreview() {
    SongListTile(title = "Song", artist = "Artist")
}

@Composable
fun SongListTile(song: Song, selected: Boolean = false, onClick: () -> Unit) {
    SongListTile(title = song.title, artist = song.artist, onClick = onClick, selected = selected)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongListTile(
    title: String,
    artist: String,
    color: Color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(modifier = Modifier.combinedClickable {
        onClick.invoke()
    }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .widthIn(0.dp, 50.dp)
                    .aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.Rounded.MusicNote, contentDescription = "",
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(0.7f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = if (selected) Color.Cyan else Color.Unspecified
                )
                Text(
                    artist,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = if (selected) {
                        Color.Cyan
                    } else if (isSystemInDarkTheme()) {
                        Color.LightGray
                    } else {
                        Color.DarkGray
                    },
                    fontSize = 10.sp
                )
            }
            IconButton(onClick = {}, Modifier.weight(0.1f)) {
                Icon(Icons.Rounded.MoreVert, "")
            }
        }
    }
}