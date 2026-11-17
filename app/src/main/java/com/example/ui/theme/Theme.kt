package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryFixedDim,
  onPrimary = OnPrimaryFixed,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  secondary = SecondaryFixedDim,
  onSecondary = OnSecondaryFixed,
  secondaryContainer = OnSecondaryFixedVariant,
  onSecondaryContainer = SecondaryFixed,
  tertiary = TertiaryFixedDim,
  onTertiary = OnTertiaryFixed,
  tertiaryContainer = TertiaryContainer,
  onTertiaryContainer = OnTertiaryContainer,
  background = Color(0xFF0E1424),
  onBackground = InverseOnSurface,
  surface = Color(0xFF0E1424),
  onSurface = InverseOnSurface,
  surfaceVariant = Color(0xFF283044),
  onSurfaceVariant = OutlineVariant,
  surfaceContainerLowest = Color(0xFF131B2E),
  surfaceContainerLow = Color(0xFF1A233A),
  surfaceContainer = Color(0xFF202A44),
  surfaceContainerHigh = Color(0xFF283044),
  surfaceContainerHighest = Color(0xFF323C54),
  outline = Outline,
  outlineVariant = Color(0xFF3D455C),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005),
  errorContainer = OnErrorContainer,
  onErrorContainer = ErrorContainer,
)

private val LightColorScheme = lightColorScheme(
  primary = Primary,
  onPrimary = OnPrimary,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  secondary = Secondary,
  onSecondary = OnSecondary,
  secondaryContainer = SecondaryContainer,
  onSecondaryContainer = OnSecondaryContainer,
  tertiary = Tertiary,
  onTertiary = OnTertiary,
  tertiaryContainer = TertiaryContainer,
  onTertiaryContainer = OnTertiaryContainer,
  background = Background,
  onBackground = OnBackground,
  surface = Surface,
  onSurface = OnSurface,
  surfaceVariant = SurfaceVariant,
  onSurfaceVariant = OnSurfaceVariant,
  surfaceContainerLowest = SurfaceContainerLowest,
  surfaceContainerLow = SurfaceContainerLow,
  surfaceContainer = SurfaceContainer,
  surfaceContainerHigh = SurfaceContainerHigh,
  surfaceContainerHighest = SurfaceContainerHighest,
  outline = Outline,
  outlineVariant = OutlineVariant,
  error = ErrorColor,
  onError = OnError,
  errorContainer = ErrorContainer,
  onErrorContainer = OnErrorContainer,
  inverseSurface = InverseSurface,
  inverseOnSurface = InverseOnSurface,
  inversePrimary = InversePrimary,
)

@Composable
fun WorkEnglishTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = WorkEnglishTypography,
    content = content,
  )
}
