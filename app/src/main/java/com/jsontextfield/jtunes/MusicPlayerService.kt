package com.jsontextfield.jtunes

import android.media.MediaPlayer
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.jsontextfield.jtunes.entities.Song
import java.io.FileNotFoundException


class MusicPlayerService : MediaSessionService() {
    private val mediaPlayer = MediaPlayer()
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    fun loadSong(song: Song) {
        Log.w("NOW_PLAYING", song.title)
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(song.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}