package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.ui.viewmodels.MusicState

enum class PlayerButton {
    PLAY_PAUSE,
    NEXT,
    PREVIOUS,
    PREVIOUS_SONG,
    SHUFFLE,
    LOOP,
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    musicState: MusicState = MusicState(),
    songDuration: Long = 0L,
    position: Float = 0f,
    onPlayerButtonPressed: (PlayerButton) -> Unit = {},
    onSeek: (value: Float) -> Unit = {},
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Slider(
            modifier = Modifier.padding(horizontal = 20.dp),
            value = position,
            onValueChange = onSeek,
            valueRange = 0f..songDuration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface,
            )
        )
        val durationMinutes = (songDuration / 1000 / 60).toInt()
        val durationSeconds = (songDuration / 1000 % 60).toInt()
        val timeMinutes = (position / 1000 / 60).toInt()
        val timeSeconds = (position / 1000 % 60).toInt()
        Text(
            stringResource(
                R.string.time_remaining,
                timeMinutes,
                timeSeconds,
                durationMinutes,
                durationSeconds
            ),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            val isPlaying = musicState.isPlaying
            val isShuffling = musicState.isShuffling
            val loopMode = musicState.loopMode
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.SHUFFLE) },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = if (isShuffling) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.PREVIOUS) },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }

            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.PLAY_PAUSE) },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.NEXT) },
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    Icons.Rounded.SkipNext,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                )
            }
            IconButton(
                onClick = { onPlayerButtonPressed(PlayerButton.LOOP) },
                modifier = Modifier.size(60.dp)
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

@Preview
@Composable
fun PlayerControlsPreview() {
    PlayerControls()
}