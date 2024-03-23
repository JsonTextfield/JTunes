package com.jsontextfield.jtunes.ui.components.menu

import androidx.compose.runtime.Composable

@Composable
fun IconMenu(showMenu: Boolean, action: Action, onItemSelected: () -> Unit) {
    action.menuContent?.invoke(showMenu)
    MenuItem(
        icon = action.icon,
        tooltip = action.tooltip,
        visible = true
    ) {
        onItemSelected()
    }
}