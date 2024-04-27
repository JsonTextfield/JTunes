package com.jsontextfield.jtunes.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.jsontextfield.jtunes.R
import com.jsontextfield.jtunes.ui.components.menu.Action


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(actions: List<Action> = ArrayList()) {
    Surface(shadowElevation = 10.dp) {
        TopAppBar(
            title = {},
            actions = {
                actions.map { action ->
                    IconButton(
                        modifier = Modifier.weight(1f),
                        onClick = action.onClick,
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = null,
                            tint = if (action.isChecked) {
                                colorResource(R.color.colourAccent)
                            }
                            else if (isSystemInDarkTheme()) {
                                Color.White
                            }
                            else {
                                Color.Gray
                            }
                        )
                    }
                }
            },
        )
    }
}