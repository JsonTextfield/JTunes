package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

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
    ListItem(
        modifier = modifier.combinedClickable(onLongClick = onLongClick, onClick = onClick),
        headlineContent = {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    }
                    else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            }
        },
        supportingContent = {
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    }
                    else {
                        Color.Unspecified
                    },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
            }
        },
        leadingContent = leading,
        trailingContent = trailing,
    )
}