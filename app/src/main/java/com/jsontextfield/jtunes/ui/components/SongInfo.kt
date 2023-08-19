package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongInfo(song: Song) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = song.title,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .basicMarquee()
        )
        Text(
            text = song.artist,
            color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .basicMarquee()
        )
        Text(
            text = "${song.trackNumber} • ${song.album}",
            color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .basicMarquee()
        )
    }
}