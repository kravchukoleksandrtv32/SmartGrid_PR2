package ua.kpi.smartgrid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Кольорова схема для темної теми
private val DarkColorScheme = darkColorScheme(
    primary = GridBlue80,
    secondary = GridTeal80,
    tertiary = Amber80
)

// Кольорова схема для світлої теми
private val LightColorScheme = lightColorScheme(
    primary = GridBlue40,
    secondary = GridTeal40,
    tertiary = Amber40
)

@Composable
fun SmartGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // темна тема визначається системою
    // Динамічні кольори (Android 12+) вимкнено, щоб застосовувалась власна палітра Smart Grid
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Вибір кольорової схеми залежно від налаштувань і версії Android
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Застосовуємо кольорову схему та типографіку до всього вмісту
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
