package io.arvo.dataconso.ui

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView

/**
 * Moteur d'effets visuels ARVO (Phase 4.3)
 */

// 1. Retour Haptique "Staff Level"
@Composable
fun rememberArvoHaptic(): () -> Unit {
    val view = LocalView.current
    return {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}

// 2. Modificateur de Shimmer (Lumière circulante)
fun Modifier.arvoShimmer(): Modifier = composed {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.2f),
        Color.White.copy(alpha = 0.0f),
    )

    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerAnimation"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    background(brush)
}
