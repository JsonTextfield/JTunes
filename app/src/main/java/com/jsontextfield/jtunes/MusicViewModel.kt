package com.jsontextfield.jtunes

import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import com.jsontextfield.jtunes.entities.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicViewModel : ViewModel() {
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

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String>
        get() = _searchText

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

    fun onSongChanged(song: Song) {
        _selectedSong.value = song
    }

    fun setPlaying(value: Boolean) {
        _isPlaying.value = value
    }

    fun onUIStateChanged(newUIState: UIState) {
        _uiState.value = newUIState
    }
}

enum class UIState { LOADING, LOADED, ERROR, }
enum class PageState { SONGS, ALBUMS, ARTISTS, GENRES, PLAYLISTS, }