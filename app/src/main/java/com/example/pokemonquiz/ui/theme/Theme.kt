package com.example.pokemonquiz.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light blue minimalist palette
val LightBlue = Color(0xFF4FC3F7)
val LightBlueDark = Color(0xFF039BE5)
val LightBlueSurface = Color(0xFFF1F8FE)
val LightBlueBackground = Color(0xFFF8FBFF)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1F5FE),
    onPrimaryContainer = Color(0xFF01579B),
    secondary = LightBlueDark,
    onSecondary = Color.White,
    background = LightBlueBackground,
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = LightBlueSurface,
    onSurfaceVariant = Color(0xFF757575),
)

@Composable
fun PokemonQuizTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
