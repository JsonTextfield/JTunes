package com.jsontextfield.jtunes.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MusicNote
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Date

@Preview
@Composable
private fun NowPlayingScreenPreview() {
    NowPlayingScreen(song = Song.random())
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NowPlayingScreen(
    song: Song, onBackPressed: () -> Unit = {},
    onSkipForward: () -> Unit = {},
    onSkipBackward: () -> Unit = {},
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { },
            actions = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = "")
                }
            })
    }, bottomBar = {
        Column() {
            var value by rememberSaveable { mutableStateOf(0f) }
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
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { shuffle = !shuffle }) {
                    Icon(if (shuffle) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle, "")
                }
                IconButton(onClick = onSkipBackward) {
                    Icon(Icons.Rounded.SkipPrevious, "")
                }
                IconButton(onClick = {
                    isPlaying = !isPlaying

                }) {
                    Icon(if (!isPlaying) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, "")
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
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.5f),
                        imageVector = Icons.Rounded.MusicNote, contentDescription = "",
                    )
                }
            }
            Column {
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
                        Calendar.getInstance().also {
                            it.time = Date(song.date)
                        }.get(Calendar.YEAR)
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
    }

}