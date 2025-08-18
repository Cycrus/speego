package com.speego.speego.ui.theme

import androidx.compose.ui.graphics.Color

// === SPEEGO BRAND COLORS ===

// Primary Blue Palette (for main actions, buttons)
val SpeegoBlue = Color(0xFF2196F3)        // Primary blue - main brand color
val SpeegoBlueLight = Color(0xFF64B5F6)   // Light blue - for light theme containers
val SpeegoBlueAccent = Color(0xFF1976D2)  // Darker blue - for dark theme, emphasis
val SpeegoBlueDeep = Color(0xFF0D47A1)    // Very dark blue - for high contrast

// Secondary Green Palette (for success, secondary actions)
val SpeegoGreen = Color(0xFF4CAF50)       // Success green - confirmations, positive actions
val SpeegoGreenLight = Color(0xFF81C784)  // Light green - success backgrounds
val SpeegoGreenAccent = Color(0xFF388E3C) // Darker green - dark theme success
val SpeegoGreenDeep = Color(0xFF1B5E20)   // Very dark green

// Neutral Gray Palette (for text, borders, backgrounds)
val SpeegoGray = Color(0xFF757575)        // Medium gray - secondary text
val SpeegoGrayLight = Color(0xFFBDBDBD)   // Light gray - borders, dividers
val SpeegoGrayDark = Color(0xFF424242)    // Dark gray - primary text on light
val SpeegoGrayDeep = Color(0xFF212121)    // Very dark gray - headers, emphasis

// Additional Accent Colors
val SpeegoOrange = Color(0xFFFF9800)      // Warning/attention color
val SpeegoOrangeLight = Color(0xFFFFCC02) // Light orange
val SpeegoRed = Color(0xFFF44336)         // Error/danger color
val SpeegoRedLight = Color(0xFFE57373)    // Light red for error backgrounds

// Surface and Background Colors
val SpeegoWhite = Color(0xFFFFFFFF)       // Pure white
val SpeegoOffWhite = Color(0xFFFAFAFA)    // Slightly off-white background
val SpeegoLightGray = Color(0xFFF5F5F5)   // Very light gray surface
val SpeegoBlack = Color(0xFF000000)       // Pure black
val SpeegoNearBlack = Color(0xFF121212)   // Dark theme background
val SpeegoDarkSurface = Color(0xFF1E1E1E) // Dark theme surface

// === ORIGINAL MATERIAL COLORS (for fallback) ===
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// === SEMANTIC COLOR ALIASES ===
// These make it easier to use colors semantically in your code

// Status Colors
val ColorSuccess = SpeegoGreen
val ColorWarning = SpeegoOrange
val ColorError = SpeegoRed
val ColorInfo = SpeegoBlue

// Text Colors (Light Theme)
val TextPrimaryLight = SpeegoGrayDeep
val TextSecondaryLight = SpeegoGray
val TextDisabledLight = SpeegoGrayLight

// Text Colors (Dark Theme)
val TextPrimaryDark = SpeegoWhite
val TextSecondaryDark = SpeegoGrayLight
val TextDisabledDark = SpeegoGray

// Button Colors
val ButtonPrimary = SpeegoBlue
val ButtonSecondary = SpeegoGreen
val ButtonTertiary = SpeegoGray
val ButtonDanger = SpeegoRed