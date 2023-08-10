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
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song

@Composable
fun NowPlaying(
    song: Song, modifier: Modifier = Modifier,
    onSkipForward: () -> Unit = {},
    onSkipBackward: () -> Unit = {},
) {
    NowPlaying(
        title = song.title, artist = song.artist, modifier = modifier,
        onSkipForward = onSkipForward,
        onSkipBackward = onSkipBackward
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlaying(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    color: Color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
    onSkipForward: () -> Unit = {},
    onSkipBackward: () -> Unit = {},
) {
    Surface(modifier = modifier.combinedClickable {

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
                Text(title, overflow = TextOverflow.Ellipsis, fontSize = 14.sp, maxLines = 1)
                Text(
                    artist,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                    fontSize = 10.sp,
                    maxLines = 1,
                )
            }
            Row {

                IconButton(
                    onClick = onSkipBackward,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Rounded.SkipPrevious, "")
                }
                var icon by remember { mutableStateOf(Icons.Rounded.Pause) }
                IconButton(onClick = {
                    icon = if (icon == Icons.Rounded.Pause) {
                        Icons.Rounded.PlayArrow
                    } else {
                        Icons.Rounded.Pause
                    }
                }, modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(icon, "")
                }
                IconButton(
                    onClick = onSkipForward,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Rounded.SkipNext, "")
                }
            }
        }
    }
}