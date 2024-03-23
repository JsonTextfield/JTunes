package com.jsontextfield.jtunes.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable

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
                tooltip = action.tooltip,
                checked = action.isChecked,
            ) {
                action.onClick()
                onItemSelected()
            }
        }
    }
}