package io.arvo.dataconso.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Graphique prédictif ARVO.
 * Dessine la courbe de consommation estimée sur 7 jours.
 */
@Composable
fun PredictionChart(
    predictions: List<Double>,
    budgetThreshold: Double,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = Color.Red

    Canvas(modifier = modifier.fillMaxWidth().height(150.dp)) {
        if (predictions.isEmpty()) return@Canvas

        val spacing = size.width / (predictions.size - 1)
        val maxVal = (predictions.maxOrNull() ?: 1.0).coerceAtLeast(budgetThreshold)
        
        // 1. Dessiner la ligne de budget (Seuil critique)
        val budgetY = size.height - (budgetThreshold / maxVal * size.height).toFloat()
        drawLine(
            color = errorColor.copy(alpha = 0.5f),
            start = Offset(0f, budgetY),
            end = Offset(size.width, budgetY),
            strokeWidth = 2.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )

        // 2. Dessiner la courbe de prédiction
        val path = Path().apply {
            predictions.forEachIndexed { index, value ->
                val x = index * spacing
                val y = size.height - (value / maxVal * size.height).toFloat()
                if (index == 0) moveTo(x, y.toFloat()) else lineTo(x, y.toFloat())
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // 3. Dessiner les points
        predictions.forEachIndexed { index, value ->
            val x = index * spacing
            val y = size.height - (value / maxVal * size.height).toFloat()
            drawCircle(
                color = if (value > budgetThreshold) errorColor else primaryColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y.toFloat())
            )
        }
    }
}
