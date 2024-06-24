package com.jsontextfield.jtunes.ui.components

import android.content.ContentUris
import android.content.res.Configuration
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.jsontextfield.jtunes.MusicViewModel
import com.jsontextfield.jtunes.entities.Song
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import kotlin.math.cos

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NowPlayingSmall(
    musicViewModel: MusicViewModel,
    modifier: Modifier = Modifier,
    onPlayerAction: (PlayerButton) -> Unit = {},
    onClick: () -> Unit = {},
) {
    val song by musicViewModel.selectedSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
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
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                    )
                }
                Row {
                    IconButton(
                        onClick = { onPlayerAction(PlayerButton.PREVIOUS) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Rounded.SkipPrevious, null)
                    }
                    IconButton(
                        onClick = { onPlayerAction(PlayerButton.PLAY_PAUSE) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null)
                    }
                    IconButton(
                        onClick = { onPlayerAction(PlayerButton.NEXT) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Rounded.SkipNext, null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NowPlayingLarge(
    musicViewModel: MusicViewModel,
    position: Float = 0f,
    onBackPressed: () -> Unit = {},
    onQueuePressed: () -> Unit = {},
    onPlayerAction: (PlayerButton) -> Unit = {},
    onSeek: (value: Float) -> Unit = {},
) {
    val previousSong: Song? by musicViewModel.previousSong.collectAsState()
    val currentSong: Song by musicViewModel.selectedSong.collectAsState()
    val nextSong: Song? by musicViewModel.nextSong.collectAsState()
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val songList = listOfNotNull(previousSong, currentSong, nextSong)
    val pagerState = rememberPagerState(
        initialPage = if (songList.size > 2 || nextSong == null) 1 else 0,
        pageCount = { songList.size },
    )
    var previousPageIndex by remember { mutableIntStateOf(pagerState.settledPage) }

    LaunchedEffect(currentSong) {
        //delay(500)
        Log.e(
            "CurrentSong",
            "PREVIOUS: ${previousSong?.title}, CURRENT: ${currentSong.title}, NEXT: ${nextSong?.title}"
        )
        if (songList.size == 3) {
            pagerState.scrollToPage(1)
        }
        previousPageIndex = pagerState.settledPage
    }

    LaunchedEffect(pagerState.settledPage) {
        Log.e(
            "SettledPage",
            "settled page: ${pagerState.settledPage}, previous page: $previousPageIndex"
        )
        if (pagerState.settledPage > previousPageIndex) {
            previousPageIndex = pagerState.settledPage
            onPlayerAction(PlayerButton.NEXT)
        }
        else if (pagerState.settledPage < previousPageIndex) {
            previousPageIndex = pagerState.settledPage
            onPlayerAction(PlayerButton.PREVIOUS_SONG)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        // background
        var bitmap: Bitmap? by remember { mutableStateOf(null) }
        LaunchedEffect(currentSong) {
            val trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong.id
            )
            bitmap = getCoverArt(context, trackUri)
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
        // foreground
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
                        IconButton(onClick = onQueuePressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
        ) {
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Row(modifier = Modifier.padding(it)) {
                    HorizontalPager(pagerState, modifier = Modifier.weight(1f)) { index ->
                        var bitmap: Bitmap? by remember { mutableStateOf(null) }
                        LaunchedEffect(index, currentSong) {
                            songList[index].let { song ->
                                val trackUri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    song.id
                                )
                                bitmap = getCoverArt(context, trackUri)
                            }
                        }
                        Box(
                            Modifier
                                .align(Alignment.CenterVertically)
                                .padding(30.dp)
                        ) {
                            CoverArt(bitmap)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(IntrinsicSize.Min)
                            .weight(2f)
                    ) {
                        SongInfo(currentSong)
                        PlayerControls(
                            musicViewModel = musicViewModel,
                            modifier = Modifier.weight(1f),
                            currentSong.duration,
                            position,
                            onPlayerAction,
                            onSeek,
                        )
                    }
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalPager(pagerState, modifier = Modifier.weight(1f)) { index ->
                        var bitmap: Bitmap? by remember { mutableStateOf(null) }
                        LaunchedEffect(index, currentSong) {
                            songList[index].let { song ->
                                val trackUri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    song.id
                                )
                                bitmap = getCoverArt(context, trackUri)
                            }
                        }
                        Box(
                            Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .padding(30.dp)
                        ) {
                            CoverArt(bitmap)
                        }
                    }
                    SongInfo(currentSong, modifier = Modifier.height(IntrinsicSize.Min))
                    val coroutineScope = rememberCoroutineScope()
                    PlayerControls(
                        musicViewModel = musicViewModel,
                        modifier = Modifier.height(IntrinsicSize.Min),
                        currentSong.duration,
                        position,
                        onPlayerButtonPressed = {
                            coroutineScope.launch {
                                if (it == PlayerButton.PREVIOUS && position < 2000 && pagerState.canScrollBackward) {
                                    pagerState.animateScrollToPage(pagerState.settledPage - 1)
                                }
                                else if (it == PlayerButton.NEXT && pagerState.canScrollForward) {
                                    pagerState.animateScrollToPage(pagerState.settledPage + 1)
                                }
                                onPlayerAction(it)
                            }
                        },
                        onSeek,
                    )
                }
            }
        }
    }
}