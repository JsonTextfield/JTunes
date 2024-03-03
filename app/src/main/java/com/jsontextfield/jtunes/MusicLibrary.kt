package com.jsontextfield.jtunes

import android.content.Context
import android.provider.MediaStore
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

    private fun loadAlbums(context: Context) {
        val projection = arrayOf(
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.FIRST_YEAR,
            MediaStore.Audio.Albums._ID,
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "LOWER(" + MediaStore.Audio.Albums.ALBUM + ") ASC"
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
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.GENRE,
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            MediaStore.Audio.Media.DURATION + ">= 5000",
            null,
            "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC"
        )
        if (cursor != null) {
            songs.clear()
            queue.clear()
            while (cursor.moveToNext()) {
                val trackString = cursor.getString(6) ?: ""
                var trackNumber = 0
                if (trackString.length == 4) {
                    trackNumber = trackString.substring(1).toInt()
                } else if (trackString.isNotEmpty()) {
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
                        trackNumber = trackNumber,
                        id = cursor.getLong(7),
                        genre = cursor.getString(8) ?: "Other",
                    )
                songs.add(song)
                queue.add(song)
            }
            cursor.close()
        }
    }

    private fun loadArtists(context: Context) {
        val projection = arrayOf(
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists._ID,
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "LOWER(" + MediaStore.Audio.Artists.ARTIST + ") ASC"
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
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID,
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "LOWER(" + MediaStore.Audio.Genres.NAME + ") ASC"
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