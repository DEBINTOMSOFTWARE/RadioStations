package com.example.radiostations.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RadioStationsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkThemeColors else LightThemeColors

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = Typography,
        content = content
    )
}