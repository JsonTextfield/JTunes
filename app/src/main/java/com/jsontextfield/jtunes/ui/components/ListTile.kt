package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jsontextfield.jtunes.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListTile(
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit = {},
    trailing: @Composable () -> Unit = {},
    title: String = "",
    subtitle: String = "",
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    selected: Boolean = false,
) {
    Surface(
        modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            leading()
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = if (selected) colorResource(id = R.color.colourAccent) else Color.Unspecified
                )
                Text(
                    subtitle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = if (selected) {
                        colorResource(id = R.color.colourAccent)
                    } else if (isSystemInDarkTheme()) {
                        Color.LightGray
                    } else {
                        Color.DarkGray
                    },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
            }
            trailing()
        }
    }
}