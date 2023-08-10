package com.jsontextfield.jtunes.ui.menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarAction(action: Action) {
    PlainTooltipBox(
        tooltip = {
            Text(
                text = action.toolTip,
                modifier = Modifier.padding(10.dp)
            )
        },
        modifier = Modifier.padding(10.dp),
    ) {
        IconButton(
            onClick = { action.onClick?.invoke() },
        ) {
            Icon(action.icon, action.toolTip)
        }
    }
}