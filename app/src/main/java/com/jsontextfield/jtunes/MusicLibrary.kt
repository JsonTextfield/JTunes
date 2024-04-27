package com.jsontextfield.jtunes

import android.content.Context
import android.provider.MediaStore.Audio
import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.entities.Artist
import com.jsontextfield.jtunes.entities.Genre
import com.jsontextfield.jtunes.entities.Song

class MusicLibrary private constructor() {
    val songs = ArrayList<Song>()
    var queue = ArrayList<Song>()
    val albums = ArrayList<Album>()
    val artists = ArrayList<Artist>()
    val genres = ArrayList<Genre>()

    val recentlyAddedSongs: List<Song>
        get() = songs.sortedBy { it.dateAdded }.reversed().take(50)

    private fun loadAlbums(context: Context) {
        val projection = arrayOf(
            Audio.Albums.ALBUM,
            Audio.Albums.ARTIST,
            Audio.Albums.FIRST_YEAR,
            Audio.Albums._ID,
        )
        val cursor = context.contentResolver.query(
            Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "LOWER(" + Audio.Albums.ALBUM + ") ASC"
        )
        albums.clear()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                albums.add(
                    Album(
                        title = cursor.getString(0),
                        artist = cursor.getString(1),
                        date = cursor.getLong(2),
                        id = cursor.getLong(3)
                    )
                )
            }
            cursor.close()
        }
    }

    private fun loadSongs(context: Context) {
        val projection = arrayOf(
            Audio.Media.TITLE,
            Audio.Media.ARTIST,
            Audio.Media.ALBUM,
            Audio.Media.DATA,
            Audio.Media.DURATION,
            Audio.Media.YEAR,
            Audio.Media.DATE_ADDED,
            Audio.Media.TRACK,
            Audio.Media._ID,
            Audio.Media.GENRE,
        )
        val cursor = context.contentResolver.query(
            Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            Audio.Media.DURATION + " >= 5000 AND " + Audio.Media.IS_MUSIC + " != 0",
            null,
            "LOWER(" + Audio.Media.TITLE + ") ASC"
        )
        if (cursor != null) {
            songs.clear()
            while (cursor.moveToNext()) {
                val trackString = cursor.getString(7) ?: ""
                var trackNumber = 0
                if (trackString.length == 4) {
                    trackNumber = trackString.substring(1).toInt()
                }
                else if (trackString.isNotEmpty()) {
                    trackNumber = trackString.toInt()
                }
                val song =
                    Song(
                        title = cursor.getString(0),
                        artist = cursor.getString(1),
                        path = cursor.getString(3),
                        album = cursor.getString(2),
                        duration = cursor.getLong(4),
                        date = cursor.getLong(5),
                        dateAdded = cursor.getLong(6),
                        trackNumber = trackNumber,
                        id = cursor.getLong(8),
                        genre = cursor.getString(9) ?: "Other",
                    )
                songs.add(song)
            }
            cursor.close()
        }
    }

    private fun loadArtists(context: Context) {
        val projection = arrayOf(
            Audio.Artists.ARTIST,
            Audio.Artists._ID,
        )
        val cursor = context.contentResolver.query(
            Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "LOWER(" + Audio.Artists.ARTIST + ") ASC"
        )
        artists.clear()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                artists.add(
                    Artist(
                        name = cursor.getString(0),
                        id = cursor.getLong(1),
                    )
                )
            }
            cursor.close()
        }
    }

    private fun loadGenres(context: Context) {
        val projection = arrayOf(
            Audio.Genres.NAME,
            Audio.Genres._ID,
        )
        val cursor = context.contentResolver.query(
            Audio.Genres.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "LOWER(" + Audio.Genres.NAME + ") ASC"
        )
        genres.clear()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                genres.add(
                    Genre(
                        name = cursor.getString(0) ?: "Other",
                        id = cursor.getLong(1),
                    )
                )
            }
            cursor.close()
        }
        genres.sortBy { it.name }
    }

    fun load(context: Context) {
        loadSongs(context)
        loadAlbums(context)
        loadArtists(context)
        loadGenres(context)
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicLibrary? = null
        fun getInstance(): MusicLibrary = INSTANCE ?: synchronized(this) {
            INSTANCE ?: MusicLibrary().also {
                INSTANCE = it
            }
        }
    }
}