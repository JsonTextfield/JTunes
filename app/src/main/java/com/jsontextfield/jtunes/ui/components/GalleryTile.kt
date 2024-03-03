package com.jsontextfield.jtunes.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun GalleryTile(title: String, bitmap: Bitmap? = null, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
    ) {

        Surface(modifier = Modifier
            .combinedClickable {
                onClick()
            }) {
            CoverArt(bitmap = bitmap)
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color(0x99000000))
                    .fillMaxWidth()
                    .padding(5.dp)
            )
        }
    }
}