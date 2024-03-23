package com.jsontextfield.jtunes.ui.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Action(
    val icon: ImageVector,
    val tooltip: String,
    val isVisible: Boolean = true,
    var isChecked: Boolean = false,
    val onClick: (() -> Unit) = {},
    val menuContent: (@Composable (expanded: Boolean) -> Unit)? = null,
)