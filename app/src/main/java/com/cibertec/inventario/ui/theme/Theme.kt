package com.cibertec.inventario.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DoradoPrimario,
    onPrimary = FondoOscuro,
    primaryContainer = SuperficieElevada,
    onPrimaryContainer = DoradoClaro,
    secondary = DoradoSecundario,
    onSecondary = FondoOscuro,
    background = FondoOscuro,
    onBackground = TextoBlanco,
    surface = SuperficieOscura,
    onSurface = TextoBlanco,
    surfaceVariant = SuperficieCard,
    onSurfaceVariant = TextoGrisClaro,
    outline = BordeSutil,
    error = ErrorRojo
)

private val LightColorScheme = lightColorScheme(
    primary = DoradoPrimario,
    onPrimary = FondoOscuro,
    primaryContainer = SuperficieElevada,
    onPrimaryContainer = DoradoClaro,
    secondary = DoradoSecundario,
    onSecondary = FondoOscuro,
    background = FondoOscuro,
    onBackground = TextoBlanco,
    surface = SuperficieOscura,
    onSurface = TextoBlanco,
    surfaceVariant = SuperficieCard,
    onSurfaceVariant = TextoGrisClaro,
    outline = BordeSutil,
    error = ErrorRojo
)

@Composable
fun VentaRopaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = FondoOscuro.toArgb()
                it.navigationBarColor = FondoOscuro.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
