package com.jsontextfield.jtunes

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.GalleryTile
import com.jsontextfield.jtunes.ui.SectionIndex
import com.jsontextfield.jtunes.ui.SongList
import com.jsontextfield.jtunes.ui.menu.Action
import com.jsontextfield.jtunes.ui.menu.ActionBar
import com.jsontextfield.jtunes.ui.theme.JTunesTheme
import java.io.FileNotFoundException
import java.util.UUID
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    private val songs = ArrayList<Song>()
    private val mediaPlayer = MediaPlayer()

    private fun playAudio(song: Song = songs.random()) {
        try {
            Log.e("NOW_PLAYING", song.title)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }
            mediaPlayer.setDataSource(song.path)
            mediaPlayer.prepare()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        mediaPlayer.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadMusicLibrary {
            loadPage()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun loadPage() {
        setContent {
            var selectedSong by remember { mutableStateOf(songs.random()) }
            var showSongs by remember { mutableStateOf(true) }

            LaunchedEffect(key1 = selectedSong, block = {
                playAudio(selectedSong)
            })

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
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.albums),
                    icon = Icons.Rounded.Album,
                    onClick = {
                        showSongs = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.artists),
                    icon = Icons.Rounded.Person,
                    onClick = {
                        showSongs = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.genres),
                    icon = Icons.Rounded.Brush,
                    onClick = {
                        showSongs = false
                    },
                ),
                Action(
                    toolTip = stringResource(id = R.string.playlists),
                    icon = Icons.Rounded.LibraryMusic,
                    onClick = {
                        showSongs = false
                    },
                ),
            )

            JTunesTheme {
                Scaffold(
                    topBar = {
                        Surface(shadowElevation = 5.dp) {
                            TopAppBar(
                                title = {},
                                actions = { ActionBar(actions) },
                            )
                        }
                    }, bottomBar = {
                        NowPlaying(
                            song = selectedSong,
                            onSkipForward = { selectedSong = songs.random() },
                            onSkipBackward = { selectedSong = songs.random() },
                            onPlayPause = { isPlaying -> if (isPlaying) mediaPlayer.start() else mediaPlayer.pause() },
                            onClick = {
                                val intent =
                                    Intent(this@MainActivity, NowPlayingActivity::class.java)
                                intent.putExtra("song", selectedSong)
                                startActivity(intent)
                            }
                        )
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
                                SectionIndex(cameras = songs, listState = listState)
                                SongList(
                                    songs = songs,
                                    selectedSong = selectedSong,
                                    listState = listState,
                                    modifier = Modifier.weight(1f),
                                    onSongClicked = {
                                        selectedSong = it
                                    }
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(120.dp),
                                contentPadding = PaddingValues(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                content = {
                                    items(Random.nextInt(1, 5)) {
                                        GalleryTile(title = UUID.randomUUID().toString())
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }


    private fun loadAlbumArtwork(onComplete: () -> Unit) {
    }

    private fun loadMusicLibrary(onComplete: () -> Unit) {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
        )
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            MediaStore.Audio.Media.DURATION + ">= 5000",
            null,
            "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                println(cursor.getInt(5))
                songs.add(
                    Song(
                        title = cursor.getString(0),
                        artist = cursor.getString(1),
                        path = cursor.getString(3),
                        album = cursor.getString(2),
                        duration = cursor.getInt(4).toLong(),
                        date = cursor.getInt(5).toLong() * 1000L,
                        trackNumber = cursor.getInt(6),
                    )
                )
            }
            cursor.close()
        }
        onComplete.invoke()
    }
}