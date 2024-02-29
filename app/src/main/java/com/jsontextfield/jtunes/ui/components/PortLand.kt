package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PortLand(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    if (isLandscape) {
        Row(modifier = modifier) {
            content()
        }
    } else {
        Column(modifier = modifier) {
            content()
        }
    }
}