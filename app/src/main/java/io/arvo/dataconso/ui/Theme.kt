package io.arvo.dataconso.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.arvo.dataconso.AppTheme

enum class PatternType { NONE, DOTS, GRID, DIAGONAL }

data class DataConsColors(
    val gradient: Brush,
    val surface: Color,
    val onSurface: Color,
    val primary: Color,
    val accent: Color,
    val outline: Color,
    val graphLabel: Color,
    val gridLine: Color,
    val patternType: PatternType = PatternType.NONE
)

fun DrawScope.drawAdaptivePattern(colors: DataConsColors) {
    when (colors.patternType) {
        PatternType.DOTS -> {
            val dotDist = 32.dp.toPx()
            for (x in 0..(size.width / dotDist).toInt()) {
                for (y in 0..(size.height / dotDist).toInt()) {
                    drawCircle(
                        color = colors.primary.copy(alpha = 0.04f),
                        radius = 1.5.dp.toPx(),
                        center = Offset(x * dotDist, y * dotDist)
                    )
                }
            }
        }
        PatternType.GRID -> {
            val step = 40.dp.toPx()
            for (x in 0..(size.width / step).toInt()) {
                drawLine(
                    color = colors.primary.copy(alpha = 0.03f),
                    start = Offset(x * step, 0f),
                    end = Offset(x * step, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (y in 0..(size.height / step).toInt()) {
                drawLine(
                    color = colors.primary.copy(alpha = 0.03f),
                    start = Offset(0f, y * step),
                    end = Offset(size.width, y * step),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        PatternType.DIAGONAL -> {
            val step = 60.dp.toPx()
            for (i in -20..((size.width + size.height) / step).toInt()) {
                drawLine(
                    color = colors.primary.copy(alpha = 0.04f),
                    start = Offset(i * step, 0f),
                    end = Offset((i * step) + size.height, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        else -> {}
    }
}

@Composable
fun getDataConsColors(appTheme: AppTheme): DataConsColors {
    return when (appTheme) {
        AppTheme.LIGHT -> DataConsColors(
            gradient = Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC))),
            surface = Color(0xFFF1F5F9), // Un gris très clair pour les cartes
            onSurface = Color(0xFF1E293B),
            primary = Color(0xFF2563EB),
            accent = Color(0xFF10B981),
            outline = Color(0xFFCBD5E1),
            graphLabel = Color(0xFF64748B),
            gridLine = Color(0xFFE2E8F0),
            patternType = PatternType.DOTS
        )
        AppTheme.DARK -> DataConsColors(
            gradient = Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617))),
            surface = Color(0xFF1E293B).copy(alpha = 0.7f),
            onSurface = Color(0xFFF1F5F9),
            primary = Color(0xFF38BDF8),
            accent = Color(0xFF34D399),
            outline = Color(0xFF475569),
            graphLabel = Color(0xFFCBD5E1),
            gridLine = Color(0xFFFFFFFF).copy(alpha = 0.1f),
            patternType = PatternType.GRID
        )
        AppTheme.OCEAN -> DataConsColors(
            gradient = Brush.verticalGradient(listOf(Color(0xFF2E1065), Color(0xFF020617))),
            surface = Color(0xFF4C1D95).copy(alpha = 0.5f),
            onSurface = Color.White,
            primary = Color(0xFFA855F7), // Thème Elite (Violet/Or)
            accent = Color(0xFFF59E0B),
            outline = Color(0xFF8B5CF6).copy(alpha = 0.3f),
            graphLabel = Color(0xFFDDD6FE),
            gridLine = Color(0xFFA855F7).copy(alpha = 0.12f),
            patternType = PatternType.DIAGONAL
        )
    }
}

@Composable
fun DataConsTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.LIGHT -> lightColorScheme(
            primary = Color(0xFF2563EB),
            background = Color(0xFFF8FAFC), // Fond clair garanti
            surface = Color.White,
            onSurface = Color(0xFF0F172A),
            onBackground = Color(0xFF0F172A)
        )
        AppTheme.DARK -> darkColorScheme(
            primary = Color(0xFF38BDF8),
            background = Color(0xFF020617),
            surface = Color(0xFF0F172A)
        )
        AppTheme.OCEAN -> darkColorScheme(
            primary = Color(0xFFA855F7),
            background = Color(0xFF020617),
            surface = Color(0xFF2E1065)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
