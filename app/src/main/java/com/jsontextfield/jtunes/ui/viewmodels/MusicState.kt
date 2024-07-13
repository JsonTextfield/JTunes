package com.jsontextfield.jtunes.ui.viewmodels

import androidx.media3.common.Player
import com.jsontextfield.jtunes.entities.Song

data class MusicState(
    val isPlaying: Boolean = false,
    val isShuffling: Boolean = false,
    val loopMode: Int = Player.REPEAT_MODE_OFF,
    val currentSong: Song? = null,
    val nextSong: Song? = null,
    val previousSong: Song? = null,
    val searchText: String = "",
    val songSortMode: SongSortMode = SongSortMode.Title,
)

enum class SongSortMode { Title, Artist, }
enum class AlbumSortMode { Title, Artist, }