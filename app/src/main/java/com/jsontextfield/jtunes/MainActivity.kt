package com.jsontextfield.jtunes

import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.AlbumList
import com.jsontextfield.jtunes.ui.components.ArtistList
import com.jsontextfield.jtunes.ui.components.NowPlayingLarge
import com.jsontextfield.jtunes.ui.components.NowPlayingSmall
import com.jsontextfield.jtunes.ui.components.SectionIndex
import com.jsontextfield.jtunes.ui.components.SongList
import com.jsontextfield.jtunes.ui.components.menu.Action
import com.jsontextfield.jtunes.ui.theme.JTunesTheme
import kotlinx.coroutines.delay

@UnstableApi
class MainActivity : ComponentActivity() {
    private val musicLibrary = MusicLibrary.getInstance()
    private val musicViewModel by viewModels<MusicViewModel>()
    private var mediaController: MediaController? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            musicViewModel.loadLibrary(this@MainActivity)
            val sessionToken =
                SessionToken(this, ComponentName(this, MusicPlayerService::class.java))
            val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    mediaController = mediaController ?: controllerFuture.get()
                    mediaController?.addListener(object : Player.Listener {

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            musicViewModel.setPlaying(isPlaying)
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            mediaController?.let {
                                musicViewModel.onSongChanged(musicLibrary.queue[it.currentMediaItemIndex])
                            }
                            super.onMediaItemTransition(mediaItem, reason)
                        }
                    })
                    mediaController?.shuffleModeEnabled = true
                    loadQueue()
                    musicViewModel.onUIStateChanged(UIState.LOADED)
                },
                MoreExecutors.directExecutor()
            )
        } else {
            musicViewModel.onUIStateChanged(UIState.ERROR)
        }
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            musicViewModel.onSongChanged(
                intent.getParcelableExtra("song", Song::class.java) ?: Song()
            )
        } else {
            musicViewModel.onSongChanged(intent.getParcelableExtra("song") ?: Song())
        }
        loadPage()
    }

    private fun loadPage() {
        setContent {
            JTunesTheme {
                val uiState by musicViewModel.uiState.collectAsState()
                when (uiState) {
                    UIState.LOADING -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = colorResource(R.color.colourAccent),
                            )
                        }
                    }

                    UIState.ERROR -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Rounded.Error,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.Center),
                                tint = Color.White,
                            )
                        }
                    }

                    UIState.LOADED -> {
                        MainPage()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainPage() {
        val pageState by musicViewModel.pageState.collectAsState()
        val selectedSong by musicViewModel.selectedSong.collectAsState()
        val isPlaying by musicViewModel.isPlaying.collectAsState()
        var isShuffling by remember { mutableStateOf(true) }
        var isLooping by remember { mutableStateOf(false) }
        var showNowPlayingScreen by remember { mutableStateOf(false) }

        val onPlayerButtonPressed: (PlayerButton) -> Unit = { playerButton ->
            when (playerButton) {
                PlayerButton.PLAY_PAUSE -> {
                    mediaController?.let {
                        if (it.isPlaying) {
                            it.pause()
                        } else {
                            it.play()
                        }
                    }
                }
                PlayerButton.NEXT -> {
                    mediaController?.let {
                        it.seekToNextMediaItem()
                        musicViewModel.onSongChanged(musicLibrary.queue[it.currentMediaItemIndex])
                    }
                }
                PlayerButton.PREVIOUS -> {
                    mediaController?.let {
                        it.seekToPrevious()
                        musicViewModel.onSongChanged(musicLibrary.queue[it.currentMediaItemIndex])
                    }
                }
                PlayerButton.SHUFFLE -> {
                    isShuffling = !isShuffling
                    mediaController?.shuffleModeEnabled = isShuffling
                }
                PlayerButton.LOOP -> {
                    isLooping = !isLooping
                    mediaController?.repeatMode = if (isLooping) {
                        Player.REPEAT_MODE_ALL
                    } else {
                        Player.REPEAT_MODE_OFF
                    }
                }
            }
        }
        Scaffold(
            topBar = {
                Surface(shadowElevation = 10.dp) {
                    TopAppBar(
                        title = {},
                        actions = {
                            val actions: List<Action> = listOf(
                                Action(
                                    toolTip = stringResource(id = R.string.search),
                                    icon = Icons.Rounded.Search,
                                ),
                                Action(
                                    toolTip = stringResource(id = R.string.songs),
                                    icon = Icons.Rounded.MusicNote,
                                    checked = pageState == PageState.SONGS,
                                    onClick = {
                                        musicViewModel.onPageChanged(PageState.SONGS)
                                    },
                                ),
                                Action(
                                    toolTip = stringResource(id = R.string.albums),
                                    icon = Icons.Rounded.Album,
                                    checked = pageState == PageState.ALBUMS,
                                    onClick = {
                                        musicViewModel.onPageChanged(PageState.ALBUMS)
                                    },
                                ),
                                Action(
                                    toolTip = stringResource(id = R.string.artists),
                                    icon = Icons.Rounded.Person,
                                    checked = pageState == PageState.ARTISTS,
                                    onClick = {
                                        musicViewModel.onPageChanged(PageState.ARTISTS)
                                    },
                                ),
                                Action(
                                    toolTip = stringResource(id = R.string.genres),
                                    icon = Icons.Rounded.Brush,
                                    checked = pageState == PageState.GENRES,
                                    onClick = {
                                        musicViewModel.onPageChanged(PageState.GENRES)
                                    },
                                ),
                                Action(
                                    toolTip = stringResource(id = R.string.playlists),
                                    icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                                    checked = pageState == PageState.PLAYLISTS,
                                    onClick = {
                                        musicViewModel.onPageChanged(PageState.PLAYLISTS)
                                    },
                                ),
                            )
                            actions.map { action ->
                                IconButton(
                                    onClick = action.onClick,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = null,
                                        tint = if (action.checked) {
                                            colorResource(R.color.colourAccent)
                                        } else if (isSystemInDarkTheme()) {
                                            Color.White
                                        } else {
                                            Color.Gray
                                        }
                                    )
                                }
                            }
                        },
                    )
                }
            },
            bottomBar = {
                Surface(tonalElevation = 2.dp, shadowElevation = 10.dp) {
                    NowPlayingSmall(
                        song = selectedSong,
                        onPlayerButtonPressed = onPlayerButtonPressed,
                        onClick = { showNowPlayingScreen = true },
                        isPlaying = isPlaying,
                    )
                }
            },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background
            ) {
                when (pageState) {
                    PageState.SONGS -> {
                        Row {
                            val listState = rememberLazyListState()
                            SectionIndex(
                                data = musicLibrary.songs.map { song -> song.title },
                                listState = listState,
                                selectedColour = colorResource(R.color.colourAccent)
                            )
                            SongList(
                                songs = musicLibrary.songs,
                                selectedSong = selectedSong,
                                listState = listState,
                                modifier = Modifier.weight(1f),
                                onSongClicked = { song ->
                                    mediaController?.seekToDefaultPosition(
                                        musicLibrary.queue.indexOf(song)
                                    )
                                    mediaController?.play()
                                    musicViewModel.onSongChanged(song)
                                },
                            )
                        }
                    }

                    PageState.ALBUMS -> {
                        AlbumList(
                            albums = musicLibrary.albums,
                            onItemClick = { album ->
                                musicLibrary.queue =
                                    ArrayList<Song>(musicLibrary.songs
                                        .filter { song: Song ->
                                            song.album == album.title
                                        }.sortedBy { song: Song ->
                                            song.trackNumber
                                        }
                                    )
                                loadQueue()
                                musicViewModel.onSongChanged(musicLibrary.queue.first())
                            },
                        )
                    }

                    PageState.ARTISTS -> {
                        ArtistList(
                            artists = musicLibrary.artists,
                            onItemClick = { artist ->
                                musicLibrary.queue =
                                    ArrayList<Song>(musicLibrary.songs
                                        .filter { song: Song ->
                                            song.artist == artist.name
                                        }
                                    )
                                loadQueue()
                                musicViewModel.onSongChanged(musicLibrary.queue.first())
                            },
                        )
                    }

                    else -> {}
                }
            }
        }
        BackHandler(showNowPlayingScreen) {
            showNowPlayingScreen = false
        }
        AnimatedVisibility(
            visible = showNowPlayingScreen,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            var progress by remember { mutableLongStateOf(0L) }
            LaunchedEffect(selectedSong) {
                mediaController?.let {
                    while (true) {
                        progress = it.currentPosition
                        delay(16)
                    }
                }
            }
            NowPlayingLarge(
                song = selectedSong,
                onBackPressed = {
                    showNowPlayingScreen = false
                },
                onPlayerButtonPressed = onPlayerButtonPressed,
                position = progress.toFloat(),
                onSeek = {
                    mediaController?.seekTo(it.toLong())
                },
                isLooping = isLooping,
                isPlaying = isPlaying,
                isShuffling = isShuffling,
            )
        }
    }

    private fun loadQueue() {
        mediaController?.clearMediaItems()
        mediaController?.setMediaItems(
            musicLibrary.queue.map { song ->
                MediaItem.fromUri(song.path)
            },
        )
    }
}

enum class PlayerButton { PLAY_PAUSE, NEXT, PREVIOUS, SHUFFLE, LOOP, }