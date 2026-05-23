package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    secondary = SecondaryPurple,
    tertiary = TertiaryIndigo,
    background = ThemeBackgroundDark,
    surface = ThemeSurfaceDark,
    surfaceVariant = ThemeSurfaceVariantDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = BodyLight,
    onSurface = BodyLight,
    onSurfaceVariant = SubtitleGray
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
    background = ThemeBackgroundLight,
    surface = ThemeSurfaceLight,
    surfaceVariant = ThemeSurfaceVariantLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = BodyDark,
    onSurface = BodyDark,
    onSurfaceVariant = SubtitleSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
