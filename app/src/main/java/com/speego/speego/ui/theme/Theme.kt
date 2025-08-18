package com.speego.speego.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Custom Dark Color Scheme
private val SpeegoLightColorScheme = lightColorScheme(
    primary = SpeeGoDarkGreen,         // Main button color (lighter in dark)
    onPrimary = SpeeGoWhite,
    primaryContainer = SpeeGoLightBrown,
    onPrimaryContainer = SpeeGoWhite,

    secondary = SpeeGoBrown,      // Secondary buttons/accents
    onSecondary = SpeeGoWhite,
    secondaryContainer = SpeeGoLightBrown,
    onSecondaryContainer = SpeeGoWhite,

    tertiary = SpeeGoMint,
    onTertiary = SpeeGoWhite,
    tertiaryContainer = SpeeGoLightBrown,
    onTertiaryContainer = SpeeGoWhite,

    background = Color(0xFF10131A),    // Dark app background
    onBackground = Color(0xFFE6E1E5),  // Text on dark background
    surface = Color(0xFF1A1C23),       // Dark card/surface background
    onSurface = Color(0xFFE6E1E5),     // Text on dark surfaces

    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = SpeeGoLightGray,
    outlineVariant = SpeeGoGray,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Custom Dark Color Scheme
private val SpeegoDarkColorScheme = darkColorScheme(
    primary = SpeeGoDarkGreen,         // Main button color (lighter in dark)
    onPrimary = SpeeGoWhite,
    primaryContainer = SpeeGoLightBrown,
    onPrimaryContainer = SpeeGoWhite,

    secondary = SpeeGoBrown,
    onSecondary = SpeeGoWhite,
    secondaryContainer = SpeeGoLightBrown,
    onSecondaryContainer = SpeeGoWhite,

    tertiary = SpeeGoMint,
    onTertiary = SpeeGoWhite,
    tertiaryContainer = SpeeGoLightBrown,
    onTertiaryContainer = SpeeGoWhite,

    background = Color(0xFF10131A),    // Dark app background
    onBackground = Color(0xFFE6E1E5),  // Text on dark background
    surface = Color(0xFF1A1C23),       // Dark card/surface background
    onSurface = Color(0xFFE6E1E5),     // Text on dark surfaces

    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = SpeeGoLightGray,
    outlineVariant = SpeeGoGray,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun SpeeGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamic color to false to use your custom colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            // Use your custom color schemes
            if (darkTheme) SpeegoDarkColorScheme else SpeegoLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}