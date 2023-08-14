package com.jsontextfield.jtunes.ui

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song
import java.io.FileNotFoundException

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlaying(
    song: Song,
    modifier: Modifier = Modifier,
    onSkipForward: () -> Unit = {},
    onSkipBackward: () -> Unit = {},
    onPlayPause: (isPlaying: Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    mediaPlayerIsPlaying: Boolean = false,
) {
    Surface(modifier = modifier.combinedClickable {
        onClick.invoke()
    }
    ) {
        Box {
            val context = LocalContext.current
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            LaunchedEffect(song) {
                bitmap = try {
                    val trackUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        song.id
                    )
                    context.contentResolver.loadThumbnail(trackUri, Size(512, 512), null)
                } catch (e: FileNotFoundException) {
                    null
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color(0x88000000), BlendMode.Multiply),
                    modifier = Modifier
                        .matchParentSize()
                        .blur(10.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                CoverArtSmall(bitmap)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.7f)
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        song.title,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                    )
                    Text(
                        song.artist,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                        fontSize = 10.sp,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                    )
                }
                Row {
                    IconButton(
                        onClick = onSkipBackward,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Rounded.SkipPrevious, "")
                    }
                    var isPlaying by remember { mutableStateOf(mediaPlayerIsPlaying) }
                    IconButton(onClick = {
                        isPlaying = !isPlaying
                        onPlayPause.invoke(isPlaying)
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(if (!isPlaying) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, "")
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
}
