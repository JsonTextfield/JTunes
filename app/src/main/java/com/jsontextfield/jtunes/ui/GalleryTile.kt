package com.jsontextfield.jtunes.ui

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun GalleryTilePreview() {
    GalleryTile(title = "Title")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryTile(title: String, image: Bitmap? = null) {
    Surface(modifier = Modifier
        .combinedClickable { }) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .aspectRatio(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = Icons.Rounded.MusicNote, contentDescription = "",
                )
            }
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color(0x44000000))
                    .fillMaxWidth()
                    .padding(5.dp)
            )
        }
    }
}