package com.jsontextfield.jtunes

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.entities.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicViewModel(
    val musicLibrary: MusicLibrary = MusicLibrary.getInstance(),
    var mediaController: MediaController? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIState.LOADING)
    val uiState: StateFlow<UIState>
        get() = _uiState

    private val _pageState = MutableStateFlow(PageState.SONGS)
    val pageState: StateFlow<PageState>
        get() = _pageState

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean>
        get() = _isPlaying

    private val _isShuffling = MutableStateFlow(false)
    val isShuffling: StateFlow<Boolean>
        get() = _isShuffling

    private val _loopMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val loopMode: StateFlow<Int>
        get() = _loopMode

    private val _selectedSong = MutableStateFlow(Song())
    val selectedSong: StateFlow<Song>
        get() = _selectedSong

    private val _nextSong = MutableStateFlow<Song?>(null)
    val nextSong: StateFlow<Song?>
        get() = _nextSong

    private val _previousSong = MutableStateFlow<Song?>(null)
    val previousSong: StateFlow<Song?>
        get() = _previousSong

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String>
        get() = _searchText

    @OptIn(UnstableApi::class)
    fun load(context: Context) {
        musicLibrary.load(context)
        musicLibrary.playlists.add(
            Playlist(
                title = ContextCompat.getString(
                    context,
                    R.string.recently_added
                ),
                songs = musicLibrary.recentlyAddedSongs,
            )
        )
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                mediaController?.addListener(object : Player.Listener {

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                        setShuffling(shuffleModeEnabled)
                        onSongChanged(
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
                        setPlaying(isPlaying)
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {
                        super.onRepeatModeChanged(repeatMode)
                        onLoopModeChanged(repeatMode)
                    }

                    override fun onMediaItemTransition(
                        mediaItem: MediaItem?,
                        reason: Int
                    ) {
                        super.onMediaItemTransition(mediaItem, reason)
                        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                            val playsPrefs =
                                context.getSharedPreferences("plays", Context.MODE_PRIVATE)
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
                        val lastPlayedPrefs = context.getSharedPreferences(
                            "lastPlayed",
                            Context.MODE_PRIVATE,
                        )
                        lastPlayedPrefs.edit {
                            putLong(
                                mediaItem?.mediaId,
                                System.currentTimeMillis(),
                            )
                        }
                        onSongChanged(
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
                    getSharedPrefs(context)
                    musicLibrary.playlists.add(
                        Playlist(
                            title = "Most Played",
                            songs = musicLibrary.mostPlayedSongs,
                        )
                    )
                    musicLibrary.playlists.add(
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
                        onSongChanged(currentSong)
                    }
                }
                setPlaying(mediaController?.isPlaying == true)
                onUIStateChanged(UIState.LOADED)
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun getSharedPrefs(context: Context) {
        val playsPrefs = context.getSharedPreferences("plays", Context.MODE_PRIVATE)
        val lastPlayedPrefs = context.getSharedPreferences("lastPlayed", Context.MODE_PRIVATE)
        for (song in musicLibrary.songs) {
            song.plays = playsPrefs.getInt(song.id.toString(), 0)
            song.lastPlayed = lastPlayedPrefs.getLong(song.id.toString(), 0)
        }
    }

    fun loadQueue() {
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

    fun setShuffling(value: Boolean) {
        _isShuffling.value = value
    }

    fun onLoopModeChanged(mode: Int) {
        _loopMode.value = mode
    }

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }

    fun onPageChanged(state: PageState) {
        _pageState.value = state
    }

    fun onSongChanged(
        song: Song = _selectedSong.value,
        nextSong: Song? = null,
        previousSong: Song? = null,
    ) {
        _selectedSong.value = song
        _nextSong.value = nextSong
        _previousSong.value = previousSong
    }

    fun setPlaying(value: Boolean) {
        _isPlaying.value = value
    }

    fun onUIStateChanged(newUIState: UIState) {
        _uiState.value = newUIState
    }

    companion object {
        val MusicViewModelFactory: ViewModelProvider.Factory = viewModelFactory {
            initializer {

                MusicViewModel()
            }
        }
    }
}

enum class UIState { LOADING, LOADED, ERROR, }
enum class PageState { SONGS, ALBUMS, ARTISTS, GENRES, PLAYLISTS, }