package com.jsontextfield.jtunes.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.FileNotFoundException

@Composable
fun CoverArt(bitmap: Bitmap?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .aspectRatio(1f)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
                    .align(Alignment.Center)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(.5f),
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                )
            }
        }
    }
}

@Composable
fun CoverArtSmall(bitmap: Bitmap?) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .widthIn(max = 50.dp)
            .aspectRatio(1f)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray,
                )
            }
        }
    }
}

fun getCoverArt(context: Context, uri: Uri): Bitmap? = try {
    context.contentResolver.loadThumbnail(
        uri,
        Size(512, 512),
        null
    )
} catch (e: FileNotFoundException) {
    null
}