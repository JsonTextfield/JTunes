package com.jsontextfield.jtunes.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource

@Composable
fun OverflowMenu(
    expanded: Boolean,
    actions: List<Action>,
    onItemSelected: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onItemSelected() },
    ) {
        for (action in actions) {
            OverflowAction(
                icon = action.icon,
                tooltip = action.toolTip,
                checked = action.checked,
            ) {
                action.onClick?.invoke()
                onItemSelected()
            }
        }
    }
}