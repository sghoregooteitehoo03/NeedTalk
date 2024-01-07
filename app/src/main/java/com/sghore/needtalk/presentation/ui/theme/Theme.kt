package com.sghore.needtalk.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple80,
    secondary = PurpleGrey80,
)

private val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = Color.White,
    onPrimary = Color.Black,
    secondary = Orange50,
    secondaryVariant = Orange50,
    onSecondary = Color.White
)

@Composable
fun NeedTalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}