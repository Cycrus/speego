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
    primary = SpeegoBlue,              // Main button color
    onPrimary = Color.White,           // Text on primary buttons
    primaryContainer = SpeegoBlueLight,
    onPrimaryContainer = Color.Black,

    secondary = SpeegoGreen,           // Secondary buttons/accents
    onSecondary = Color.White,
    secondaryContainer = SpeegoGreenLight,
    onSecondaryContainer = Color.Black,

    tertiary = SpeegoGray,
    onTertiary = Color.White,
    tertiaryContainer = SpeegoGrayLight,
    onTertiaryContainer = Color.Black,

    background = Color(0xFFFFFBFE),    // App background
    onBackground = Color(0xFF1C1B1F),  // Text on background
    surface = Color.White,             // Card/surface background
    onSurface = Color(0xFF1C1B1F),     // Text on surfaces

    surfaceVariant = Color(0xFFF3F3F3),
    onSurfaceVariant = Color(0xFF49454F),

    outline = SpeegoGray,
    outlineVariant = SpeegoGrayLight,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

// Custom Dark Color Scheme
private val SpeegoDarkColorScheme = darkColorScheme(
    primary = SpeegoBlueLight,         // Main button color (lighter in dark)
    onPrimary = Color.Black,
    primaryContainer = SpeegoBlueAccent,
    onPrimaryContainer = Color.White,

    secondary = SpeegoGreenLight,      // Secondary buttons/accents
    onSecondary = Color.Black,
    secondaryContainer = SpeegoGreen,
    onSecondaryContainer = Color.White,

    tertiary = SpeegoGrayLight,
    onTertiary = Color.Black,
    tertiaryContainer = SpeegoGrayDark,
    onTertiaryContainer = Color.White,

    background = Color(0xFF10131A),    // Dark app background
    onBackground = Color(0xFFE6E1E5),  // Text on dark background
    surface = Color(0xFF1A1C23),       // Dark card/surface background
    onSurface = Color(0xFFE6E1E5),     // Text on dark surfaces

    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

// Keep your original schemes as fallback (optional)
private val OriginalDarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val OriginalLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun SpeeGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamic color to false to use your custom colors
    dynamicColor: Boolean = false,
    // Add option to use original colors
    useOriginalColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useOriginalColors -> {
            if (darkTheme) OriginalDarkColorScheme else OriginalLightColorScheme
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