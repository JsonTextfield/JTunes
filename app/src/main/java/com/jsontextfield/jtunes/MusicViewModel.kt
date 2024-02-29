package com.jsontextfield.jtunes

import android.content.Context
import androidx.lifecycle.ViewModel
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

    private val _selectedSong = MutableStateFlow(Song())
    val selectedSong: StateFlow<Song>
        get() = _selectedSong

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

    fun loadLibrary(context: Context) {
        MusicLibrary.getInstance().load(context)
    }
}

enum class UIState { LOADING, LOADED, ERROR, }
enum class LoopMode { OFF, LOOP_ALL, LOOP_ONE, }
enum class PageState { SONGS, ALBUMS, ARTISTS, GENRES, PLAYLISTS, }