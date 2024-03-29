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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.NowPlayingLarge
import com.jsontextfield.jtunes.ui.components.NowPlayingSmall
import com.jsontextfield.jtunes.ui.components.PlayerButton
import com.jsontextfield.jtunes.ui.components.menu.Action
import com.jsontextfield.jtunes.ui.pages.AlbumPage
import com.jsontextfield.jtunes.ui.pages.ArtistPage
import com.jsontextfield.jtunes.ui.pages.GenrePage
import com.jsontextfield.jtunes.ui.pages.PlaylistPage
import com.jsontextfield.jtunes.ui.pages.SongPage
import com.jsontextfield.jtunes.ui.theme.JTunesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
class MainActivity : ComponentActivity() {
    private val musicLibrary = MusicLibrary.getInstance()
    private val musicViewModel by viewModels<MusicViewModel> { MusicViewModel.MusicViewModelFactory }
    private var mediaController: MediaController? = null
    private val playlists = ArrayList<Playlist>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            musicLibrary.load(this@MainActivity)

            playlists.add(
                Playlist(
                    title = ContextCompat.getString(this@MainActivity, R.string.recently_added),
                    songs = musicLibrary.recentlyAddedSongs,
                )
            )
            val sessionToken =
                SessionToken(this, ComponentName(this, MusicPlayerService::class.java))
            val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    mediaController = controllerFuture.get()
                    mediaController?.addListener(object : Player.Listener {

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                            musicViewModel.setShuffling(shuffleModeEnabled)
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            musicViewModel.setPlaying(isPlaying)
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            super.onMediaItemTransition(mediaItem, reason)
                            musicViewModel.onSongChanged(
                                musicLibrary.queue[mediaController?.currentMediaItemIndex ?: 0]
                            )
                        }

                        override fun onRepeatModeChanged(repeatMode: Int) {
                            super.onRepeatModeChanged(repeatMode)
                            musicViewModel.onLoopModeChanged(repeatMode)
                        }
                    })
                    mediaController?.let {
                        if (it.mediaItemCount == 0) {
                            loadQueue()
                        } else {
                            val currentSong = musicLibrary.songs.find { song ->
                                song.title == it.currentMediaItem?.mediaMetadata?.title
                                        && song.artist == it.currentMediaItem?.mediaMetadata?.artist
                            } ?: Song()
                            musicViewModel.onSongChanged(currentSong)
                        }
                    }
                    musicViewModel.setPlaying(mediaController?.isPlaying == true)
                    musicViewModel.onUIStateChanged(UIState.LOADED)
                },
                MoreExecutors.directExecutor()
            )
        } else {
            musicViewModel.onUIStateChanged(UIState.ERROR)
        }
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

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra("fromService", false)) {
            musicViewModel.onSongChanged(
                musicLibrary.queue[intent.getIntExtra("song", 0)]
            )
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

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    fun MainPage() {
        val pageState by musicViewModel.pageState.collectAsState()
        val selectedSong by musicViewModel.selectedSong.collectAsState()
        val isPlaying by musicViewModel.isPlaying.collectAsState()
        val isShuffling by musicViewModel.isShuffling.collectAsState()
        val loopMode by musicViewModel.loopMode.collectAsState()
        var showNowPlayingScreen by remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(pageCount = { PageState.entries.size })

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
                        if (it.mediaItemCount > 0) {
                            it.seekToNextMediaItem()
                            musicViewModel.onSongChanged(musicLibrary.queue[it.currentMediaItemIndex])
                        }
                    }
                }

                PlayerButton.PREVIOUS -> {
                    mediaController?.let {
                        if (it.mediaItemCount > 0) {
                            it.seekToPrevious()
                            musicViewModel.onSongChanged(musicLibrary.queue[it.currentMediaItemIndex])
                        }
                    }
                }

                PlayerButton.SHUFFLE -> {
                    mediaController?.let {
                        it.shuffleModeEnabled = !it.shuffleModeEnabled
                    }
                }

                PlayerButton.LOOP -> {
                    mediaController?.let {
                        it.repeatMode = (it.repeatMode + 2) % 3
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
                            val coroutineScope = rememberCoroutineScope()
                            val onPageChanged: (PageState) -> Unit = { pageState ->
                                coroutineScope.launch {
                                    pagerState.scrollToPage(pageState.index)
                                    musicViewModel.onPageChanged(pageState)
                                }
                            }
                            val actions: List<Action> = listOf(
                                Action(
                                    tooltip = stringResource(id = R.string.songs),
                                    icon = Icons.Rounded.MusicNote,
                                    isChecked = pageState == PageState.SONGS,
                                    onClick = { onPageChanged(PageState.SONGS) },
                                ),
                                Action(
                                    tooltip = stringResource(id = R.string.albums),
                                    icon = Icons.Rounded.Album,
                                    isChecked = pageState == PageState.ALBUMS,
                                    onClick = { onPageChanged(PageState.ALBUMS) },
                                ),
                                Action(
                                    tooltip = stringResource(id = R.string.artists),
                                    icon = Icons.Rounded.Person,
                                    isChecked = pageState == PageState.ARTISTS,
                                    onClick = { onPageChanged(PageState.ARTISTS) },
                                ),
                                Action(
                                    tooltip = stringResource(id = R.string.genres),
                                    icon = Icons.Rounded.Brush,
                                    isChecked = pageState == PageState.GENRES,
                                    onClick = { onPageChanged(PageState.GENRES) },
                                ),
                                Action(
                                    tooltip = stringResource(id = R.string.playlists),
                                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                                    isChecked = pageState == PageState.PLAYLISTS,
                                    onClick = { onPageChanged(PageState.PLAYLISTS) },
                                ),
                            )
                            actions.map { action ->
                                IconButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = action.onClick,
                                ) {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = null,
                                        tint = if (action.isChecked) {
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
            floatingActionButton = {
                if (pageState == PageState.PLAYLISTS) {
                    FloatingActionButton(
                        containerColor = colorResource(R.color.colourAccent),
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
                val searchText by musicViewModel.searchText.collectAsState()
                HorizontalPager(pagerState) { index ->
                    when (index) {
                        PageState.SONGS.index -> {
                            musicViewModel.onPageChanged(PageState.SONGS)
                            SongPage(
                                musicViewModel = musicViewModel,
                                songs = musicLibrary.songs.filter { song ->
                                    song.title.contains(searchText, true)
                                },
                                hintText = "Search from ${musicLibrary.songs.size} songs",
                                onCreatePlaylist = {
                                    playlists.add(
                                        Playlist(
                                            title = musicViewModel.searchText.value,
                                            songs = musicLibrary.songs.filter { song ->
                                                song.title.contains(searchText, true)
                                            },
                                        )
                                    )
                                },
                                onShuffleClick = {
                                    mediaController?.shuffleModeEnabled = true
                                    musicLibrary.queue = ArrayList(musicLibrary.songs)
                                    loadQueue()
                                    mediaController?.play()
                                },
                                onItemClick = { song ->
                                    musicLibrary.queue = ArrayList(musicLibrary.songs)
                                    loadQueue()
                                    mediaController?.seekToDefaultPosition(
                                        musicLibrary.queue.indexOf(song)
                                    )
                                    mediaController?.play()
                                    musicViewModel.onSongChanged(song)
                                }
                            )
                        }

                        PageState.ALBUMS.index -> {
                            musicViewModel.onPageChanged(PageState.ALBUMS)
                            AlbumPage(
                                musicViewModel = musicViewModel,
                                albums = musicLibrary.albums.filter { album ->
                                    album.title.contains(searchText, true)
                                },
                                hintText = "Search from ${musicLibrary.albums.size} albums",
                                onItemClick = { album ->
                                    musicLibrary.queue =
                                        ArrayList(musicLibrary.songs
                                            .filter { song: Song ->
                                                song.album == album.title
                                            }.sortedBy { song: Song ->
                                                song.trackNumber
                                            }
                                        )
                                    loadQueue()
                                    mediaController?.play()
                                    musicViewModel.onSongChanged(musicLibrary.queue.first())
                                },
                            )
                        }

                        PageState.ARTISTS.index -> {
                            musicViewModel.onPageChanged(PageState.ARTISTS)
                            ArtistPage(
                                musicViewModel = musicViewModel,
                                artists = musicLibrary.artists.filter { artist ->
                                    artist.name.contains(searchText, true)
                                },
                                hintText = "Search from ${musicLibrary.artists.size} artists",
                                onItemClick = { artist ->
                                    musicLibrary.queue =
                                        ArrayList(musicLibrary.songs
                                            .filter { song: Song ->
                                                song.artist == artist.name
                                            }
                                        )
                                    loadQueue()
                                    mediaController?.play()
                                    musicViewModel.onSongChanged(musicLibrary.queue.first())
                                },
                            )
                        }

                        PageState.GENRES.index -> {
                            musicViewModel.onPageChanged(PageState.GENRES)
                            GenrePage(
                                musicViewModel = musicViewModel,
                                genres = musicLibrary.genres.filter { genre ->
                                    genre.name.contains(searchText, true)
                                },
                                hintText = "Search from ${musicLibrary.genres.size} genres",
                                onItemClick = { genre ->
                                    musicLibrary.queue =
                                        ArrayList(musicLibrary.songs
                                            .filter { song: Song ->
                                                song.genre == genre.name
                                            }
                                        )
                                    loadQueue()
                                    mediaController?.play()
                                    musicViewModel.onSongChanged(musicLibrary.queue.first())
                                },
                            )
                        }

                        PageState.PLAYLISTS.index -> {
                            musicViewModel.onPageChanged(PageState.PLAYLISTS)
                            PlaylistPage(
                                musicViewModel = musicViewModel,
                                playlists = playlists.sortedBy { it.title }.filter { playlist ->
                                    playlist.title.contains(searchText, true)
                                },
                                hintText = "Search from ${
                                    pluralStringResource(
                                        R.plurals.playlists,
                                        playlists.size,
                                        playlists.size
                                    )
                                }",
                                onItemClick = { playlist ->
                                    musicLibrary.queue = ArrayList(playlist.songs)
                                    loadQueue()
                                    mediaController?.play()
                                    musicViewModel.onSongChanged(musicLibrary.queue.first())
                                },
                            )
                        }
                    }
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
                        delay(50)
                    }
                }
            }
            NowPlayingLarge(
                song = selectedSong,
                onBackPressed = { showNowPlayingScreen = false },
                onPlayerButtonPressed = onPlayerButtonPressed,
                position = progress.toFloat(),
                onSeek = { mediaController?.seekTo(it.toLong()) },
                loopMode = loopMode,
                isPlaying = isPlaying,
                isShuffling = isShuffling,
            )
        }
    }

    private fun loadQueue() {
        mediaController?.setMediaItems(
            musicLibrary.queue.map { song ->
                val metadata =
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .build()

                MediaItem.Builder()
                    .setMediaMetadata(metadata)
                    .setUri(song.path)
                    .build()
            },
        )
        mediaController?.prepare()
    }
}
