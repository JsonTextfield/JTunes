package com.jsontextfield.jtunes

import android.content.Context
import android.provider.MediaStore
import com.jsontextfield.jtunes.entities.Album
import com.jsontextfield.jtunes.entities.Artist
import com.jsontextfield.jtunes.entities.Genre
import com.jsontextfield.jtunes.entities.Song

class MusicLibrary private constructor() {
    val songs: ArrayList<Song> = ArrayList<Song>()
    val albums: ArrayList<Album> = ArrayList<Album>()
    val artists: ArrayList<Artist> = ArrayList<Artist>()
    val genres: ArrayList<Genre> = ArrayList<Genre>()
    val playedSongs = ArrayList<Song>()

    fun loadAlbums(context: Context, onComplete: () -> Unit = {}) {
        val projection = arrayOf(
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.ALBUM_ART,
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
        if (cursor != null) {
            while (cursor.moveToNext()) {
                albums.add(
                    Album(
                        title = cursor.getString(0),
                        artist = cursor.getString(1),
                        coverArt = cursor.getString(2) ?: "",
                        date = cursor.getInt(3).toLong(),
                        id = cursor.getLong(4)
                    )
                )
            }
            cursor.close()
        }
        onComplete.invoke()
    }

    fun loadSongs(context: Context, onComplete: () -> Unit = {}) {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media._ID,
        )
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            MediaStore.Audio.Media.DURATION + ">= 5000",
            null,
            "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val trackString = cursor.getString(6) ?: ""
                var trackNumber = 0
                if (trackString.length == 4) {
                    trackNumber = trackString.substring(1).toInt()
                } else if (trackString.isNotEmpty()) {
                    trackNumber = trackString.toInt()
                }
                songs.add(
                    Song(
                        title = cursor.getString(0),
                        artist = cursor.getString(1),
                        path = cursor.getString(3),
                        album = cursor.getString(2),
                        duration = cursor.getInt(4).toLong(),
                        date = cursor.getLong(5),
                        trackNumber = trackNumber,
                        id = cursor.getLong(7),
                    )
                )
            }
            cursor.close()
        }
        onComplete.invoke()
    }

    fun loadArtists(context: Context, onComplete: () -> Unit = {}) {
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
        onComplete.invoke()
    }
    fun loadGenres(context: Context, onComplete: () -> Unit = {}) {
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
        if (cursor != null) {
            while (cursor.moveToNext()) {
                genres.add(
                    Genre(
                        name = cursor.getString(0),
                        id = cursor.getLong(1),
                    )
                )
            }
            cursor.close()
        }
        onComplete.invoke()
    }

    fun load(context: Context, onComplete: () -> Unit = {}) {
        loadSongs(context)
        loadAlbums(context)
        loadArtists(context)
        //loadGenres(context)
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