package com.jsontextfield.jtunes.ui.viewmodels

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.annotation.OptIn
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
import com.jsontextfield.jtunes.MusicLibrary
import com.jsontextfield.jtunes.MusicPlayerService
import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.entities.Artist
import com.jsontextfield.jtunes.entities.Genre
import com.jsontextfield.jtunes.entities.Playlist
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.components.PlayerButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MusicViewModel(
    private val musicLibrary: MusicLibrary = MusicLibrary.getInstance(),
    private var mediaController: MediaController? = null,
) : ViewModel() {

    private val _musicState: MutableStateFlow<MusicState> = MutableStateFlow(MusicState())
    val musicState: StateFlow<MusicState> get() = _musicState.asStateFlow()

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.LOADING)
    val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

    private val _pageState: MutableStateFlow<PageState> = MutableStateFlow(PageState.SONGS)
    val pageState: StateFlow<PageState> get() = _pageState.asStateFlow()

    val currentPlayerPosition: Long get() = mediaController?.currentPosition ?: 0L

    @OptIn(UnstableApi::class)
    fun load(context: Context) {
        musicLibrary.load(context)
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
                            song = musicLibrary.queue.getOrNull(
                                mediaController?.currentMediaItemIndex ?: -1
                            ),
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
                    if (it.mediaItemCount == 0) {
                        loadQueue(getSongs())
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

    fun playPause() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            }
            else {
                it.play()
            }
        }
    }

    fun next() {
        mediaController?.let {
            if (it.mediaItemCount > 0) {
                it.seekToNextMediaItem()
            }
        }
    }

    fun previous() {
        mediaController?.let {
            if (it.mediaItemCount > 0) {
                it.seekToPrevious()
            }
        }
    }

    fun previousSong() {
        mediaController?.let {
            if (it.mediaItemCount > 0) {
                it.seekToPreviousMediaItem()
            }
        }
    }

    fun loop() {
        mediaController?.let {
            it.repeatMode = (it.repeatMode + 2) % 3
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun getQueueSongs(): List<Song> {
        val songs = ArrayList<Song>()
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
        return songs
    }

    private fun loadQueue(songs: List<Song>) {
        musicLibrary.queue.clear()
        musicLibrary.queue.addAll(songs)
        mediaController?.setMediaItems(
            musicLibrary.queue.map { song ->
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

    fun getSongs(searchText: String = "", selector: (Song) -> String = { it.title }): List<Song> {
        return musicLibrary.songs.filter {
            selector(it).contains(searchText, true)
        }
    }

    fun getAlbums(searchText: String = ""): List<Album> {
        return musicLibrary.albums.filter { album ->
            album.title.contains(searchText, true)
        }
    }

    fun getArtists(searchText: String = ""): List<Artist> {
        return musicLibrary.artists.filter { artist ->
            artist.name.contains(searchText, true)
        }
    }

    fun getGenres(searchText: String = ""): List<Genre> {
        return musicLibrary.genres.filter { genre ->
            genre.name.contains(searchText, true)
        }
    }

    fun getPlaylists(searchText: String = ""): List<Playlist> {
        return musicLibrary.playlists.sortedBy { it.title }
            .filter { playlist ->
                playlist.title.contains(searchText, true)
            }
    }

    fun setShuffling(value: Boolean) {
        _musicState.update {
            it.copy(isShuffling = value)
        }
        mediaController?.shuffleModeEnabled = value
    }

    fun onLoopModeChanged(mode: Int) {
        _musicState.update {
            it.copy(loopMode = mode)
        }
    }

    fun onSearchTextChanged(text: String) {
        _musicState.update {
            it.copy(searchText = text)
        }
    }

    fun onPageChanged(state: PageState) {
        _pageState.value = state
    }

    fun onSongChanged(index: Int) {
        onSongChanged(musicLibrary.queue[index])
    }

    fun onSongChanged(
        song: Song? = musicState.value.currentSong,
        nextSong: Song? = null,
        previousSong: Song? = null,
    ) {
        _musicState.update {
            it.copy(
                currentSong = song,
                nextSong = nextSong,
                previousSong = previousSong,
            )
        }
    }

    fun setPlaying(value: Boolean) {
        _musicState.update {
            it.copy(isPlaying = value)
        }
    }

    fun onUIStateChanged(newUIState: UIState) {
        _uiState.value = newUIState
    }

    fun onPlayerAction(playerButton: PlayerButton) {
        when (playerButton) {
            PlayerButton.PLAY_PAUSE -> playPause()
            PlayerButton.NEXT -> next()
            PlayerButton.PREVIOUS -> previous()
            PlayerButton.PREVIOUS_SONG -> previousSong()
            PlayerButton.SHUFFLE -> setShuffling(!musicState.value.isShuffling)
            PlayerButton.LOOP -> loop()
        }
    }

    fun shuffleAndPlay() {
        setShuffling(true)
        loadQueue(getSongs())
        playPause()
    }

    fun playSong(song: Song) {
        loadQueue(getSongs())
        onSongChanged(song)
        mediaController?.seekToDefaultPosition(musicLibrary.queue.indexOf(song))
        playPause()
    }

    fun playGenre(genre: Genre) {
        val songs = musicLibrary.songs.filter { song ->
            song.genre == genre.name
        }
        loadQueue(songs)
        playPause()
    }

    fun playArtist(artist: Artist) {
        val songs = musicLibrary.songs.filter { song ->
            song.artist == artist.name
        }
        loadQueue(songs)
        playPause()
    }

    fun playAlbum(album: Album) {
        val songs = musicLibrary.songs.filter { song ->
            song.album == album.title
        }
        loadQueue(songs)
        playPause()
    }

    fun playPlaylist(playlist: Playlist) {
        loadQueue(playlist.songs)
        playPause()
    }

    fun createPlaylistFromSearch() {
        val searchText = musicState.value.searchText
        musicLibrary.playlists.add(
            Playlist(
                title = searchText,
                songs = when (_pageState.value) {
                    PageState.SONGS -> getSongs(searchText)
                    PageState.ALBUMS -> getSongs(searchText) { it.album }
                    PageState.ARTISTS -> getSongs(searchText) { it.artist }
                    PageState.GENRES -> getSongs(searchText) { it.genre }
                    PageState.PLAYLISTS -> emptyList()
                },
            ),
        )
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