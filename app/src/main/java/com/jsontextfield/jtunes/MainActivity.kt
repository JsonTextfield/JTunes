package com.jsontextfield.jtunes

import android.Manifest
import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.MoreExecutors
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.MainTopAppBar
import com.jsontextfield.jtunes.ui.components.NowPlayingLarge
import com.jsontextfield.jtunes.ui.components.NowPlayingSmall
import com.jsontextfield.jtunes.ui.components.PlayerButton
import com.jsontextfield.jtunes.ui.components.QueueView
import com.jsontextfield.jtunes.ui.components.menu.Action
import com.jsontextfield.jtunes.ui.pages.AlbumPage
import com.jsontextfield.jtunes.ui.pages.ArtistPage
import com.jsontextfield.jtunes.ui.pages.GenrePage
import com.jsontextfield.jtunes.ui.pages.PlaylistPage
import com.jsontextfield.jtunes.ui.pages.SongPage
import com.jsontextfield.jtunes.ui.theme.AppTheme
import kotlinx.coroutines.delay

@UnstableApi
class MainActivity : ComponentActivity() {
    private val musicLibrary = MusicLibrary.getInstance()
    private val musicViewModel by viewModels<MusicViewModel> { MusicViewModel.MusicViewModelFactory }
    private var mediaController: MediaController? = null
    private val playlists = ArrayList<Playlist>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (intent.getBooleanExtra("fromService", false)) {
            musicViewModel.onSongChanged(
                musicLibrary.queue[intent.getIntExtra("song", 0)]
            )
        }
        loadPage()
    }

    private fun getSharedPrefs() {
        val playsPrefs = getSharedPreferences("plays", Context.MODE_PRIVATE)
        val lastPlayedPrefs = getSharedPreferences("lastPlayed", Context.MODE_PRIVATE)
        for (song in musicLibrary.songs) {
            song.plays = playsPrefs.getInt(song.id.toString(), 0)
            song.lastPlayed = lastPlayedPrefs.getLong(song.id.toString(), 0)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    private fun loadPage() {
        setContent {
            AppTheme {
                val permissionState = rememberPermissionState(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    }
                    else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                )
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        musicLibrary.load(this@MainActivity)
                        playlists.add(
                            Playlist(
                                title = ContextCompat.getString(
                                    this@MainActivity,
                                    R.string.recently_added
                                ),
                                songs = musicLibrary.recentlyAddedSongs,
                            )
                        )
                        val sessionToken =
                            SessionToken(this, ComponentName(this, MusicPlayerService::class.java))
                        val controllerFuture =
                            MediaController.Builder(this, sessionToken).buildAsync()
                        controllerFuture.addListener(
                            {
                                mediaController = controllerFuture.get()
                                mediaController?.addListener(object : Player.Listener {

                                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                                        musicViewModel.setShuffling(shuffleModeEnabled)
                                        musicViewModel.onSongChanged(
                                            nextSong = musicLibrary.queue.getOrNull(
                                                mediaController?.nextMediaItemIndex ?: -1
                                            ),
                                            previousSong = musicLibrary.queue.getOrNull(
                                                mediaController?.previousMediaItemIndex ?: -1
                                            ),
                                        )
                                    }

                                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                                        super.onIsPlayingChanged(isPlaying)
                                        musicViewModel.setPlaying(isPlaying)
                                    }

                                    override fun onRepeatModeChanged(repeatMode: Int) {
                                        super.onRepeatModeChanged(repeatMode)
                                        musicViewModel.onLoopModeChanged(repeatMode)
                                    }

                                    override fun onMediaItemTransition(
                                        mediaItem: MediaItem?,
                                        reason: Int
                                    ) {
                                        super.onMediaItemTransition(mediaItem, reason)
                                        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                                            val playsPrefs =
                                                getSharedPreferences("plays", Context.MODE_PRIVATE)
                                            playsPrefs.edit {
                                                val previousMediaItem =
                                                    mediaController?.getMediaItemAt(
                                                        mediaController?.previousMediaItemIndex ?: 0
                                                    )
                                                val song = musicLibrary.songs.find {
                                                    it.id.toString() == previousMediaItem?.mediaId
                                                }
                                                putInt(
                                                    previousMediaItem?.mediaId,
                                                    (song?.plays ?: 0) + 1,
                                                )
                                            }
                                        }
                                        val lastPlayedPrefs = getSharedPreferences(
                                            "lastPlayed",
                                            Context.MODE_PRIVATE,
                                        )
                                        lastPlayedPrefs.edit {
                                            putLong(
                                                mediaItem?.mediaId,
                                                System.currentTimeMillis(),
                                            )
                                        }
                                        musicViewModel.onSongChanged(
                                            song = musicLibrary.queue[mediaController?.currentMediaItemIndex
                                                ?: 0],
                                            nextSong = musicLibrary.queue.getOrNull(
                                                mediaController?.nextMediaItemIndex ?: -1
                                            ),
                                            previousSong = musicLibrary.queue.getOrNull(
                                                mediaController?.previousMediaItemIndex ?: -1
                                            ),
                                        )
                                    }
                                })
                                mediaController?.let {
                                    getSharedPrefs()
                                    playlists.add(
                                        Playlist(
                                            title = "Most Played",
                                            songs = musicLibrary.mostPlayedSongs,
                                        )
                                    )
                                    playlists.add(
                                        Playlist(
                                            title = "Recently Played",
                                            songs = musicLibrary.recentlyPlayedSongs,
                                        )
                                    )
                                    if (it.mediaItemCount == 0) {
                                        loadQueue()
                                    }
                                    else {
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
                    }
                    else {
                        musicViewModel.onUIStateChanged(UIState.ERROR)
                    }
                }
                LaunchedEffect(permissionState) {
                    requestPermissionLauncher.launch(permissionState.permission)
                }
                val uiState by musicViewModel.uiState.collectAsState()
                val pageState by musicViewModel.pageState.collectAsState()
                when (uiState) {
                    UIState.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    UIState.ERROR -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Error,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                            )
                        }
                    }

                    UIState.LOADED -> {
                        val onPlayerButtonPressed: (PlayerButton) -> Unit = { playerButton ->
                            when (playerButton) {
                                PlayerButton.PLAY_PAUSE -> {
                                    mediaController?.let {
                                        if (it.isPlaying) {
                                            it.pause()
                                        }
                                        else {
                                            it.play()
                                        }
                                    }
                                }

                                PlayerButton.NEXT -> {
                                    mediaController?.let {
                                        if (it.mediaItemCount > 0) {
                                            it.seekToNextMediaItem()
                                        }
                                    }
                                }

                                PlayerButton.PREVIOUS -> {
                                    mediaController?.let {
                                        if (it.mediaItemCount > 0) {
                                            it.seekToPrevious()
                                        }
                                    }
                                }

                                PlayerButton.PREVIOUS_SONG -> {
                                    mediaController?.let {
                                        if (it.mediaItemCount > 0) {
                                            it.seekToPreviousMediaItem()
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
                        MainScreen(
                            musicViewModel = musicViewModel,
                            onPlayerAction = onPlayerButtonPressed,
                            actions = actions,
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainContent(musicViewModel: MusicViewModel) {
        val pageState by musicViewModel.pageState.collectAsState()
        val searchText by musicViewModel.searchText.collectAsState()
        val pagerState = rememberPagerState { PageState.entries.size }

        LaunchedEffect(pageState) {
            pagerState.scrollToPage(pageState.ordinal)
        }
        LaunchedEffect(pagerState.currentPage) {
            musicViewModel.onPageChanged(PageState.entries[pagerState.currentPage])
        }
        HorizontalPager(pagerState) { index ->
            when (index) {
                PageState.SONGS.ordinal -> {
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
                            if (mediaController?.mediaItemCount == 0 ||
                                musicLibrary.queue != ArrayList(musicLibrary.songs)
                            ) {
                                musicLibrary.queue = ArrayList(musicLibrary.songs)
                                loadQueue()
                            }
                            mediaController?.seekToDefaultPosition(
                                musicLibrary.queue.indexOf(song)
                            )
                            mediaController?.play()
                            musicViewModel.onSongChanged(
                                song = song,
                                nextSong = musicLibrary.queue.getOrNull(
                                    mediaController?.nextMediaItemIndex ?: -1
                                ),
                                previousSong = musicLibrary.queue.getOrNull(
                                    mediaController?.previousMediaItemIndex ?: -1
                                ),
                            )
                        }
                    )
                }

                PageState.ALBUMS.ordinal -> {
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

                PageState.ARTISTS.ordinal -> {
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

                PageState.GENRES.ordinal -> {
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

                PageState.PLAYLISTS.ordinal -> {
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
            var progress by remember { mutableLongStateOf(mediaController?.currentPosition ?: 0L) }
            LaunchedEffect(isPlaying) {
                while (isPlaying) {
                    mediaController?.let {
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
                onSeek = { mediaController?.seekTo(it.toLong()) },
            )
        }

        AnimatedVisibility(
            visible = showQueue,
        ) {
            val songs = remember { mutableListOf<Song>() }
            LaunchedEffect(showQueue) {
                mediaController?.let {
                    for (i in 0 until it.mediaItemCount) {
                        val mediaItem = it.getMediaItemAt(i)
                        musicLibrary.songs.find { song ->
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

    private fun loadQueue() {
        mediaController?.setMediaItems(
            MusicLibrary.getInstance().queue.map { song ->
                val metadata =
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setAlbumArtist(song.artist)
                        .setGenre(song.genre)
                        .setTrackNumber(song.trackNumber)
                        .setArtworkUri(
                            ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                song.id
                            )
                        )
                        .build()
                MediaItem.Builder()
                    .setMediaId(song.id.toString())
                    .setMediaMetadata(metadata)
                    .setUri(song.path)
                    .build()
            },
        )
        mediaController?.prepare()
    }
}
