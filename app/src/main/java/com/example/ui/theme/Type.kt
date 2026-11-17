package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSansFontFamily = FontFamily(
  Font(R.font.plus_jakarta_sans, FontWeight.Normal),
  Font(R.font.plus_jakarta_sans, FontWeight.Medium),
  Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
  Font(R.font.plus_jakarta_sans, FontWeight.Bold),
  Font(R.font.plus_jakarta_sans, FontWeight.ExtraBold),
)

val ManropeFontFamily = FontFamily(
  Font(R.font.manrope, FontWeight.Medium),
  Font(R.font.manrope, FontWeight.SemiBold),
  Font(R.font.manrope, FontWeight.Bold),
)

val WorkEnglishTypography = Typography(
  displayMedium = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp,
  ),
  headlineLarge = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.4).sp,
  ),
  headlineMedium = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = 30.sp,
    letterSpacing = (-0.25).sp,
  ),
  headlineSmall = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.15).sp,
  ),
  titleMedium = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
  ),
  bodyLarge = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 26.sp,
  ),
  bodyMedium = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
  ),
  bodySmall = TextStyle(
    fontFamily = PlusJakartaSansFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
  ),
  labelLarge = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp,
  ),
  labelMedium = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.2.sp,
  ),
  labelSmall = TextStyle(
    fontFamily = ManropeFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.4.sp,
  ),
)
