package com.jsontextfield.jtunes.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.menu.Action
import com.jsontextfield.jtunes.ui.viewmodels.MusicViewModel
import com.jsontextfield.jtunes.ui.viewmodels.PageState
import kotlinx.coroutines.delay

@Composable
fun MainScreen(musicViewModel: MusicViewModel = viewModel()) {
    val pageState by musicViewModel.pageState.collectAsState()
    val musicState by musicViewModel.musicState.collectAsState()
    var showNowPlayingScreen by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }

    val actions: List<Action> = listOf(
        Action(
            tooltip = stringResource(id = R.string.songs),
            icon = Icons.Rounded.MusicNote,
            isChecked = pageState == PageState.SONGS,
            onClick = { musicViewModel.onPageChanged(PageState.SONGS) },
        ),
        Action(
            tooltip = stringResource(id = R.string.albums),
            icon = Icons.Rounded.Album,
            isChecked = pageState == PageState.ALBUMS,
            onClick = { musicViewModel.onPageChanged(PageState.ALBUMS) },
        ),
        Action(
            tooltip = stringResource(id = R.string.artists),
            icon = Icons.Rounded.Person,
            isChecked = pageState == PageState.ARTISTS,
            onClick = { musicViewModel.onPageChanged(PageState.ARTISTS) },
        ),
        Action(
            tooltip = stringResource(id = R.string.genres),
            icon = Icons.Rounded.Brush,
            isChecked = pageState == PageState.GENRES,
            onClick = { musicViewModel.onPageChanged(PageState.GENRES) },
        ),
        Action(
            tooltip = stringResource(id = R.string.playlists),
            icon = Icons.AutoMirrored.Rounded.QueueMusic,
            isChecked = pageState == PageState.PLAYLISTS,
            onClick = { musicViewModel.onPageChanged(PageState.PLAYLISTS) },
        ),
    )
    Scaffold(
        topBar = { MainTopAppBar(actions = actions) },
        bottomBar = {
            musicState.currentSong?.let {
                Surface(tonalElevation = 2.dp, shadowElevation = 10.dp) {
                    NowPlayingSmall(
                        musicViewModel = musicViewModel,
                        onClick = { showNowPlayingScreen = true },
                    )
                }
            }
        },
        floatingActionButton = {
            if (pageState == PageState.PLAYLISTS) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
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
        var progress by remember { mutableLongStateOf(musicViewModel.currentPlayerPosition) }
        LaunchedEffect(musicState.isPlaying) {
            while (musicState.isPlaying) {
                progress = musicViewModel.currentPlayerPosition
                delay(100)
            }
        }
        NowPlayingLarge(
            musicViewModel = musicViewModel,
            onBackPressed = { showNowPlayingScreen = false },
            onQueuePressed = { showQueue = true },
            position = progress.toFloat(),
            onSeek = { musicViewModel.seekTo(it.toLong()) },
        )
    }
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    LaunchedEffect(showQueue) {
        songs = musicViewModel.getQueueSongs()
    }
    if (showQueue) {
        QueueView(
            songs = songs,
            onBackPressed = { showQueue = false },
        )
    }
}