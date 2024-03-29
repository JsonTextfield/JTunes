package com.jsontextfield.jtunes.ui.components

import android.content.ContentUris
import android.content.res.Configuration
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FeaturedPlayList
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.jsontextfield.jtunes.entities.Song
import java.io.FileNotFoundException
import kotlin.math.cos

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingSmall(
    song: Song,
    modifier: Modifier = Modifier,
    onPlayerButtonPressed: (PlayerButton) -> Unit = {},
    onClick: () -> Unit = {},
    isPlaying: Boolean = false,
) {
    Surface(modifier = modifier.combinedClickable {
        onClick()
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
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = .4f,
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
                        lineHeight = 14.sp,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                    )
                    Text(
                        song.artist,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                    )
                }
                Row {
                    IconButton(
                        onClick = { onPlayerButtonPressed(PlayerButton.PREVIOUS) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Rounded.SkipPrevious, null)
                    }
                    IconButton(
                        onClick = { onPlayerButtonPressed(PlayerButton.PLAY_PAUSE) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null)
                    }
                    IconButton(
                        onClick = { onPlayerButtonPressed(PlayerButton.NEXT) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Rounded.SkipNext, null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingLarge(
    song: Song,
    position: Float = 0f,
    onBackPressed: () -> Unit = {},
    onPlayerButtonPressed: (PlayerButton) -> Unit = {},
    onSeek: (value: Float) -> Unit = {},
    loopMode: Int = Player.REPEAT_MODE_OFF,
    isShuffling: Boolean = true,
    isPlaying: Boolean = true,
) {
    Surface(modifier = Modifier.fillMaxHeight()) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                val i by rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = 2 * Math.PI.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(8000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                val blur by remember { derivedStateOf { 15 + 10 * cos(i) } }
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = .4f,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(Dp(blur))
                )
            }
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {},
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.FeaturedPlayList,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                },
            ) {

                val config = LocalConfiguration.current
                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Row(modifier = Modifier.padding(it).padding(30.dp)) {
                        CoverArt(bitmap, modifier = Modifier.align(Alignment.CenterVertically).weight(.3f))
                        Column(modifier = Modifier.weight(.9f)) {
                            SongInfo(song)
                            PlayerControls(
                                modifier = Modifier.weight(1f),
                                song.duration,
                                position,
                                onPlayerButtonPressed,
                                onSeek,
                                loopMode,
                                isShuffling,
                                isPlaying,
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(it).padding(30.dp)) {
                        CoverArt(bitmap, modifier = Modifier.align(Alignment.CenterHorizontally).weight(.3f))
                        Column(modifier = Modifier.weight(.9f)) {
                            SongInfo(song)
                            PlayerControls(
                                modifier = Modifier.weight(1f),
                                song.duration,
                                position,
                                onPlayerButtonPressed,
                                onSeek,
                                loopMode,
                                isShuffling,
                                isPlaying,
                            )
                        }
                    }
                }
            }
        }
    }
}