package com.jsontextfield.jtunes

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@UnstableApi
class MusicPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this@MusicPlayerService, player).build()
        setUpMediaSession()
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                setUpMediaSession()
            }
        })
    }

    private fun setUpMediaSession() {
        mediaSession?.setSessionActivity(
            PendingIntent.getActivity(
                this@MusicPlayerService,
                1,
                Intent(this@MusicPlayerService, MainActivity::class.java).apply {
                    putExtra("song", mediaSession?.player?.currentMediaItemIndex)
                    putExtra("fromService", true)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service if not playing, continue playing in the background otherwise.
            stopSelf()
        }
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
}