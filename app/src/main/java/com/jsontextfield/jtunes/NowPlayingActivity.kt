package com.jsontextfield.jtunes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jsontextfield.jtunes.entities.Song
import com.jsontextfield.jtunes.ui.NowPlayingScreen
import com.jsontextfield.jtunes.ui.theme.JTunesTheme

class NowPlayingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val song = intent.getParcelableExtra("song")
            ?: Song("This is the name of the song", "This is the Artist")
        setContent {
            JTunesTheme {
                NowPlayingScreen(song = song, onBackPressed = {
                    super.onBackPressed()
                })
            }
        }
    }
}
