package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player

enum class PlayerButton { PLAY_PAUSE, NEXT, PREVIOUS, SHUFFLE, LOOP, }

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    songDuration: Long = 0L,
    position: Float = 0f,
    onPlayerButtonPressed: (PlayerButton) -> Unit = {},
    onSeek: (value: Float) -> Unit = {},
    loopMode: Int = Player.REPEAT_MODE_OFF,
    isShuffling: Boolean = true,
    isPlaying: Boolean = true,
) {
    Column(modifier = modifier) {
        Slider(
            modifier = Modifier.padding(horizontal = 20.dp),
            value = position,
            onValueChange = onSeek,
            valueRange = 0f..songDuration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = if (isSystemInDarkTheme()) Color.White else Color.DarkGray,
                activeTrackColor = if (isSystemInDarkTheme()) Color.White else Color.DarkGray,
                inactiveTrackColor = if (isSystemInDarkTheme()) Color.Gray else Color.LightGray,
            )
        )
        val durationMinutes = (songDuration / 1000 / 60).toInt()
        val durationSeconds = (songDuration / 1000 % 60).toInt()
        val timeMinutes = (position / 1000 / 60).toInt()
        val timeSeconds = (position / 1000 % 60).toInt()
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
        Row {
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.SHUFFLE) },
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f)
            ) {
                Icon(
                    imageVector = if (isShuffling) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.PREVIOUS) },
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }

            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.PLAY_PAUSE) },
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.NEXT) },
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f)
            ) {
                Icon(
                    Icons.Rounded.SkipNext,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.LOOP) },
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f)
            ) {
                Icon(
                    imageVector = when (loopMode) {
                        Player.REPEAT_MODE_ALL -> Icons.Rounded.RepeatOn
                        Player.REPEAT_MODE_ONE -> Icons.Rounded.RepeatOneOn
                        else -> Icons.Rounded.Repeat
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}