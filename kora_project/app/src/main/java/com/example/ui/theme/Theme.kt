package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = GoldAccent,
  onPrimary = PitchDarkCanvas,
  primaryContainer = StadiumGreenPrimary,
  onPrimaryContainer = GoldAccentLight,
  secondary = StadiumGreenLight,
  onSecondary = Color.White,
  secondaryContainer = PitchCardContainer,
  onSecondaryContainer = StadiumTextPrimary,
  tertiary = GoldAccentLight,
  onTertiary = PitchDarkCanvas,
  background = PitchDarkCanvas,
  onBackground = StadiumTextPrimary,
  surface = PitchDarkSurface,
  onSurface = StadiumTextPrimary,
  surfaceVariant = PitchCardContainer,
  onSurfaceVariant = StadiumTextSecondary,
  outline = GoldAccent.copy(alpha = 0.3f)
)

private val LightColorScheme = DarkColorScheme // Always enforce Elegant Dark aesthetic


@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Disabled dynamic color to enforce brand Stadium Green & Gold
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
