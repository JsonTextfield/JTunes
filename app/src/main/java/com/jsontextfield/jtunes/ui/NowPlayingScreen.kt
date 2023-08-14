package com.jsontextfield.jtunes.ui

import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song
import kotlinx.coroutines.delay
import java.io.FileNotFoundException

@Preview
@Composable
private fun NowPlayingScreenPreview() {
    NowPlayingScreen(song = Song.random())
}

@Composable
fun NowPlayingScreen(
    song: Song,
    position: Float = 0f,
    onBackPressed: () -> Unit = {},
    onSkipForward: () -> Unit = {},
    onSkipBackward: () -> Unit = {},
    onPlayPause: () -> Unit = {},
) {
    val context = LocalContext.current
    Scaffold(contentWindowInsets = WindowInsets(0.dp)) { padding ->
        Box(modifier = Modifier.padding(padding)) {
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
                        .fillMaxSize()
                        .blur(50.dp)
                )
            }
            Column {
                BottomAppBar(containerColor = Color.Transparent) {}
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = ""
                    )
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    CoverArt(bitmap)
                    SongInfo(song)
                }

                Column(modifier = Modifier.weight(1f)) {
                    var value by rememberSaveable { mutableStateOf(position) }
                    var isPlaying by remember { mutableStateOf(true) }
                    var isLooping by remember { mutableStateOf(0) }
                    var shuffle by remember { mutableStateOf(false) }
                    Slider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        value = value,
                        onValueChange = {
                            value = it
                        },
                        valueRange = 0f..song.duration.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.Gray,
                        )
                    )
                    LaunchedEffect(isPlaying) {
                        while (isPlaying) {
                            if (isLooping == 1 || isLooping == 2) {
                                value %= song.duration
                            } else if (value >= song.duration) {
                                isPlaying = false
                            }
                            value += 100
                            delay(100)
                        }
                    }
                    val durationMinutes = (song.duration / 1000 / 60).toInt()
                    val durationSeconds = (song.duration / 1000 % 60).toInt()
                    val timeMinutes = (value / 1000 / 60).toInt()
                    val timeSeconds = (value / 1000 % 60).toInt()
                    Text(
                        String.format(
                            "%d:%02d / %d:%02d",
                            timeMinutes,
                            timeSeconds,
                            durationMinutes,
                            durationSeconds
                        ),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .weight(1f)
                    ) {
                        IconButton(onClick = { shuffle = !shuffle }) {
                            Icon(
                                if (shuffle) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                                ""
                            )
                        }
                        IconButton(onClick = onSkipBackward) {
                            Icon(Icons.Rounded.SkipPrevious, "")
                        }
                        IconButton(onClick = {
                            isPlaying = !isPlaying
                            onPlayPause.invoke()
                        }) {
                            Icon(
                                if (!isPlaying) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                                ""
                            )
                        }
                        IconButton(onClick = onSkipForward) {
                            Icon(Icons.Rounded.SkipNext, "")
                        }
                        IconButton(onClick = { isLooping = (isLooping + 1) % 3 }) {
                            Icon(
                                when (isLooping) {
                                    0 -> {
                                        Icons.Rounded.Repeat
                                    }

                                    1 -> {
                                        Icons.Rounded.RepeatOn
                                    }

                                    else -> {
                                        Icons.Rounded.RepeatOneOn
                                    }
                                },
                                ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongInfo(song: Song) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = song.title,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .basicMarquee()
        )
        Text(
            text = song.artist,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .basicMarquee()
        )
        Text(
            text = "${song.trackNumber} • ${song.album} • ${
                song.date
            }",
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .basicMarquee()
        )
    }
}