package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = AgriGreenDarkPrimary,
    onPrimary = AgriGreenDarkOnPrimary,
    primaryContainer = AgriGreenDarkPrimaryContainer,
    onPrimaryContainer = AgriGreenDarkOnPrimaryContainer,
    secondary = AgriGreenDarkPrimary,
    onSecondary = AgriGreenDarkOnPrimary,
    background = AgriBackgroundDark,
    onBackground = AgriOnBackgroundDark,
    surface = AgriSurfaceDark,
    onSurface = AgriOnSurfaceDark,
    surfaceVariant = AgriSurfaceVariantDark,
    onSurfaceVariant = AgriOnSurfaceVariantDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = AgriGreenOnPrimary,
    primaryContainer = AgriGreenPrimaryContainer,
    onPrimaryContainer = AgriGreenOnPrimaryContainer,
    secondary = AgriGreenSecondary,
    onSecondary = AgriGreenOnSecondary,
    secondaryContainer = AgriGreenSecondaryContainer,
    onSecondaryContainer = AgriGreenOnSecondaryContainer,
    tertiary = AgriAmberTertiary,
    tertiaryContainer = AgriAmberTertiaryContainer,
    onTertiaryContainer = AgriAmberOnTertiaryContainer,
    background = AgriBackgroundLight,
    onBackground = AgriOnBackgroundLight,
    surface = AgriSurfaceLight,
    onSurface = AgriOnSurfaceLight,
    surfaceVariant = AgriSurfaceVariantLight,
    onSurfaceVariant = AgriOnSurfaceVariantLight,
    outline = AgriOutlineLight,
    outlineVariant = Color(0xFFE2E9DF),
  )

@Composable
fun CropGuardTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep consistent green agricultural branding
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
