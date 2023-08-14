package com.jsontextfield.jtunes

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.AlbumList
import com.jsontextfield.jtunes.ui.ArtistList
import com.jsontextfield.jtunes.ui.NowPlaying
import com.jsontextfield.jtunes.ui.NowPlayingScreen
import com.jsontextfield.jtunes.ui.SectionIndex
import com.jsontextfield.jtunes.ui.SongList
import com.jsontextfield.jtunes.ui.menu.Action
import com.jsontextfield.jtunes.ui.menu.ActionBar
import com.jsontextfield.jtunes.ui.theme.JTunesTheme
import java.io.FileNotFoundException

class MainActivity : ComponentActivity() {
    private val musicLibrary = MusicLibrary.getInstance()
    private val mediaPlayer = MediaPlayer()

    private fun loadAudio(song: Song = musicLibrary.songs.random()) {
        Log.e("NOW_PLAYING", song.title)
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(song.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        musicLibrary.load(this)
        loadPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun loadPage() {
        setContent {
            var selectedSong by remember { mutableStateOf(musicLibrary.songs.random()) }
            var showSongs by remember { mutableStateOf(true) }
            var showAlbums by remember { mutableStateOf(false) }

            LaunchedEffect(selectedSong) {
                loadAudio(selectedSong)
                mediaPlayer.setOnCompletionListener {
                    musicLibrary.playedSongs.add(selectedSong)
                    selectedSong = musicLibrary.songs.random()
                }
            }

            val actions: List<Action> = listOf(
                Action(
                    toolTip = stringResource(id = R.string.search),
                    icon = Icons.Rounded.Search,
                ),
                Action(
                    toolTip = stringResource(id = R.string.songs),
                    icon = Icons.Rounded.MusicNote,
                    onClick = {
                        showSongs = true
                        showAlbums = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.albums),
                    icon = Icons.Rounded.Album,
                    onClick = {
                        showAlbums = true
                        showSongs = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.artists),
                    icon = Icons.Rounded.Person,
                    onClick = {
                        showAlbums = false
                        showSongs = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.genres),
                    icon = Icons.Rounded.Brush,
                    onClick = {
                        showAlbums = false
                        showSongs = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.playlists),
                    icon = Icons.Rounded.LibraryMusic,
                    onClick = {
                        showAlbums = false
                        showSongs = false
                    },
                ),
            )

            JTunesTheme {
                var showNowPlayingScreen by remember { mutableStateOf(false) }
                if (showNowPlayingScreen) {
                    NowPlayingScreen(
                        song = selectedSong,
                        onBackPressed = {
                            showNowPlayingScreen = false
                        },
                        onSkipBackward = {
                            if (musicLibrary.playedSongs.isNotEmpty()) {
                                selectedSong = musicLibrary.playedSongs.last()
                                musicLibrary.playedSongs.remove(musicLibrary.playedSongs[musicLibrary.playedSongs.size - 1])
                            }
                        },
                        onSkipForward = {
                            musicLibrary.playedSongs.add(selectedSong)
                            selectedSong = musicLibrary.songs.random()
                        },
                        onPlayPause = {
                            if (mediaPlayer.isPlaying) {
                                mediaPlayer.pause()
                            } else {
                                mediaPlayer.start()
                            }
                        },
                        position = mediaPlayer.currentPosition.toFloat(),
                    )
                } else {
                    Scaffold(
                        topBar = {
                            if (false && !showNowPlayingScreen) {
                                Surface(shadowElevation = 5.dp) {
                                    TopAppBar(
                                        title = {},
                                        actions = { ActionBar(actions) },
                                    )
                                }
                            }
                        }, bottomBar = {
                            if (!showNowPlayingScreen) {
                                NowPlaying(
                                    song = selectedSong,
                                    onSkipBackward = {
                                        if (musicLibrary.playedSongs.isNotEmpty()) {
                                            selectedSong = musicLibrary.playedSongs.last()
                                            musicLibrary.playedSongs.remove(musicLibrary.playedSongs[musicLibrary.playedSongs.size - 1])
                                            mediaPlayer.start()
                                        }
                                    },
                                    onSkipForward = {
                                        musicLibrary.playedSongs.add(selectedSong)
                                        selectedSong = musicLibrary.songs.random()
                                        mediaPlayer.start()
                                    },
                                    onPlayPause = {
                                        if (mediaPlayer.isPlaying) {
                                            mediaPlayer.pause()
                                        } else {
                                            mediaPlayer.start()
                                        }
                                    },
                                    onClick = {
                                        showNowPlayingScreen = true
                                    },
                                    mediaPlayerIsPlaying = mediaPlayer.isPlaying
                                )
                            }
                        }
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            if (showSongs) {
                                val listState = rememberLazyListState()
                                Row {
                                    SectionIndex(
                                        cameras = musicLibrary.songs,
                                        listState = listState
                                    )
                                    SongList(
                                        songs = musicLibrary.songs,
                                        selectedSong = selectedSong,
                                        listState = listState,
                                        modifier = Modifier.weight(1f),
                                        onSongClicked = { song ->
                                            musicLibrary.playedSongs.add(song)
                                            selectedSong = song
                                            mediaPlayer.start()
                                        }
                                    )
                                }
                            } else if (showAlbums) {
                                AlbumList(albums = musicLibrary.albums)
                            } else {
                                ArtistList(artists = musicLibrary.artists)
                            }
                        }
                    }
                }
            }
        }
    }
}