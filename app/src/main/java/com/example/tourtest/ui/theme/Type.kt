package com.example.tourtest.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.tourtest.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val InterFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Bold)
)

val MontserratFontFamily = FontFamily(
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Montserrat"), fontProvider = provider, weight = FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = InterFontFamily),
    displayMedium = TextStyle(fontFamily = InterFontFamily),
    displaySmall = TextStyle(fontFamily = InterFontFamily),
    headlineLarge = TextStyle(fontFamily = InterFontFamily),
    headlineMedium = TextStyle(fontFamily = InterFontFamily),
    headlineSmall = TextStyle(fontFamily = InterFontFamily),
    titleLarge = TextStyle(fontFamily = InterFontFamily),
    titleMedium = TextStyle(fontFamily = InterFontFamily),
    titleSmall = TextStyle(fontFamily = InterFontFamily),

    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = InterFontFamily),
    bodySmall = TextStyle(fontFamily = InterFontFamily),
    labelLarge = TextStyle(fontFamily = InterFontFamily),
    labelMedium = TextStyle(fontFamily = InterFontFamily),
    labelSmall = TextStyle(fontFamily = InterFontFamily)
)