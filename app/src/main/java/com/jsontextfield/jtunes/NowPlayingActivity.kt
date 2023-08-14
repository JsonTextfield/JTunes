package com.jsontextfield.jtunes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.NowPlayingScreen
import com.jsontextfield.jtunes.ui.theme.JTunesTheme

class NowPlayingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val song = intent.getParcelableExtra("song")
            ?: Song("This is the name of the song", "This is the Artist")
        val currentPosition = intent.getIntExtra("position", 0)
        setContent {
            JTunesTheme {
                NowPlayingScreen(
                    song = song,
                    position = currentPosition.toFloat(),
                    onBackPressed = {
                        super.onBackPressed()
                    },
                )
            }
        }
    }
}
