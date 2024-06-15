package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.entities.Song

@Composable
fun SongInfo(song: Song, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 10.dp)) {
        Text(
            text = song.title,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
            //.basicMarquee()
        )
        Text(
            text = song.artist,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
            //.basicMarquee()
        )
        Text(
            text = song.album,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
            //.basicMarquee()
        )
    }
}