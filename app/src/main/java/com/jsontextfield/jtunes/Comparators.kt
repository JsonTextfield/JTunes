package com.jsontextfield.jtunes

import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.entities.Song
import java.util.Locale

val SortSongByTitle = compareBy<Song>(
    { it.title.first().isLetterOrDigit() },
    { it.title.lowercase(Locale.getDefault()) },
    { it.artist.lowercase(Locale.getDefault()) },
)

val SortSongByArtist = compareBy<Song>(
    { it.artist.first().isLetterOrDigit() },
    { it.artist.lowercase(Locale.getDefault()) },
    { it.title.lowercase(Locale.getDefault()) },
)

val SortAlbumByTitle = compareBy<Album>(
    { it.title.first().isLetterOrDigit() },
    { it.title.lowercase(Locale.getDefault()) },
    { it.artist.lowercase(Locale.getDefault()) },
)

val SortAlbumByArtist = compareBy<Album>(
    { it.artist.first().isLetterOrDigit() },
    { it.artist.lowercase(Locale.getDefault()) },
    { it.title.lowercase(Locale.getDefault()) },
)