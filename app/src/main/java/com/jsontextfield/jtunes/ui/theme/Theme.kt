package com.jsontextfield.jtunes.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.jsontextfield.jtunes.R

@Composable
fun JTunesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = colorResource(id = R.color.colourAccent),
            secondary = colorResource(id = R.color.colourAccent),
            tertiary = colorResource(id = R.color.colourAccent),
            // Other default colors to override
            //onSurface = colorResource(id = R.color.colourAccent),
            //background = colorResource(id = R.color.colourAccent),
            //surface = colorResource(id = R.color.colourAccent),
            //onPrimary = colorResource(id = R.color.colourAccent),
            //onSecondary = colorResource(id = R.color.colourAccent),
            //onTertiary = colorResource(id = R.color.colourAccent),
            //onBackground = colorResource(id = R.color.colourAccent),
        )
    } else {
        lightColorScheme(
            primary = colorResource(id = R.color.colourAccent),
            secondary = colorResource(id = R.color.colourAccent),
            tertiary = colorResource(id = R.color.colourAccent),
            // Other default colors to override
            //onSurface = colorResource(id = R.color.colourAccent),
            //background = colorResource(id = R.color.colourAccent),
            //surface = colorResource(id = R.color.colourAccent),
            //onPrimary = colorResource(id = R.color.colourAccent),
            //onSecondary = colorResource(id = R.color.colourAccent),
            //onTertiary = colorResource(id = R.color.colourAccent),
            //onBackground = colorResource(id = R.color.colourAccent),
        )
    }

    val colors = if (!darkTheme) lightColorScheme() else darkColorScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(10.dp))
    )
}