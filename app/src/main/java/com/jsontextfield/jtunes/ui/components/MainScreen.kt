package com.jsontextfield.jtunes.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.MusicViewModel
import com.jsontextfield.jtunes.PageState
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.menu.Action
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    musicViewModel: MusicViewModel,
    actions: List<Action> = ArrayList(),
    onPlayerAction: (PlayerButton) -> Unit = {}
) {
    val pageState by musicViewModel.pageState.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    var showNowPlayingScreen by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { MainTopAppBar(actions = actions) },
        bottomBar = {
            Surface(tonalElevation = 2.dp, shadowElevation = 10.dp) {
                NowPlayingSmall(
                    musicViewModel = musicViewModel,
                    onPlayerAction = onPlayerAction,
                    onClick = { showNowPlayingScreen = true },
                )
            }
        },
        floatingActionButton = {
            if (pageState == PageState.PLAYLISTS) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    onClick = {
                        // create new playlist
                    },
                ) {
                    Icon(Icons.Rounded.Add, null)
                }
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            MainContent(musicViewModel = musicViewModel)
        }
    }
    BackHandler(showNowPlayingScreen) {
        showNowPlayingScreen = false
    }
    BackHandler(showQueue) {
        showQueue = false
    }
    AnimatedVisibility(
        visible = showNowPlayingScreen,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        var progress by remember {
            mutableLongStateOf(
                musicViewModel.mediaController?.currentPosition ?: 0L
            )
        }
        LaunchedEffect(isPlaying) {
            while (isPlaying) {
                musicViewModel.mediaController?.let {
                    progress = it.currentPosition
                }
                delay(100)
            }
        }
        NowPlayingLarge(
            musicViewModel = musicViewModel,
            onBackPressed = { showNowPlayingScreen = false },
            onQueuePressed = {
                showQueue = true
            },
            onPlayerAction = onPlayerAction,
            position = progress.toFloat(),
            onSeek = { musicViewModel.mediaController?.seekTo(it.toLong()) },
        )
    }

    AnimatedVisibility(
        visible = showQueue,
    ) {
        val songs = remember { mutableListOf<Song>() }
        LaunchedEffect(showQueue) {
            musicViewModel.mediaController?.let {
                for (i in 0 until it.mediaItemCount) {
                    val mediaItem = it.getMediaItemAt(i)
                    musicViewModel.musicLibrary.songs.find { song ->
                        song.title == mediaItem.mediaMetadata.title
                                && song.artist == mediaItem.mediaMetadata.artist
                                && song.album == mediaItem.mediaMetadata.albumTitle
                                && song.genre == mediaItem.mediaMetadata.genre
                    }?.let { song -> songs.add(song) }
                }
            }
        }
        QueueView(
            songs = songs,
            onBackPressed = { showQueue = false },
        )
    }
}