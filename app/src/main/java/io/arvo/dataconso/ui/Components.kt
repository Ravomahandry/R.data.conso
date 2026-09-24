package io.arvo.dataconso.ui

import androidx.compose.ui.platform.LocalContext
import io.arvo.dataconso.DataUsageManager
import io.arvo.dataconso.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.arvo.dataconso.MainViewModel
import io.arvo.dataconso.AppTheme
import java.util.Locale

@Composable
fun GlassCardSommite(
    colors: DataConsColors, 
    modifier: Modifier = Modifier, 
    isLoading: Boolean = false, 
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val haptic = rememberArvoHaptic()
    val mainVm: MainViewModel = hiltViewModel()
    val settings by mainVm.settings.collectAsStateWithLifecycle()
    val isLowPerf = settings.lowPerformanceMode

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLoading) Modifier.arvoShimmer() else Modifier)
            .then(
                if (!isLowPerf) {
                    Modifier.shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = colors.primary.copy(alpha = 0.2f),
                        spotColor = colors.primary.copy(alpha = 0.3f)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(if (isLowPerf) 16.dp else 28.dp),
        color = if (isLowPerf) colors.surface else colors.surface.copy(alpha = 0.65f), 
        border = if (isLowPerf) BorderStroke(1.dp, colors.outline.copy(alpha = 0.2f)) else BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.05f),
                    colors.primary.copy(alpha = 0.4f)
                )
            )
        ),
        tonalElevation = if (isLowPerf) 2.dp else 10.dp
    ) {
        Box(modifier = Modifier.clickable(enabled = onClick != null || !isLoading) { 
            haptic()
            onClick?.invoke() 
        }) {
            content()
        }
    }
}

@Composable
fun LuminousGaugeSommite(
    used: Double, 
    quota: Double, 
    primaryColor: Color,
    isLowPerf: Boolean,
    onSurfaceColor: Color,
    outlineColor: Color
) {
    val context = LocalContext.current
    val formatter = remember { DataUsageManager(context) }
    
    val progress = if (quota > 0) (used / quota).toFloat() else 0f
    
    val accessibilityDesc = "Consommation : ${formatter.formatData((used * 1024 * 1024 * 1024).toLong())} sur un quota de ${formatter.formatData((quota * 1024 * 1024 * 1024).toLong())}. Progression : ${(progress * 100).toInt()}%"

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1.2f),
        animationSpec = if (isLowPerf) tween(1000) else tween(2500, easing = FastOutSlowInEasing),
        label = "gauge"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val pulseAlpha by if (isLowPerf) remember { mutableStateOf(0.5f) } else {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    }

    val gaugeColor by animateColorAsState(
        targetValue = when {
            progress >= 1f -> Color(0xFFFF3D00)
            progress >= 0.8f -> Color(0xFFFFAB00)
            else -> primaryColor
        },
        label = "color"
    )

    // Sommité : Mise en cache des dégradés pour la performance de rendu
    val neonGradient = remember(gaugeColor) {
        Brush.sweepGradient(
            0.0f to gaugeColor.copy(alpha = 0.2f),
            0.5f to gaugeColor.copy(alpha = 0.6f),
            0.8f to gaugeColor,
            1.0f to gaugeColor
        )
    }

    Box(
        contentAlignment = Alignment.Center, 
        modifier = Modifier
            .size(300.dp)
            .semantics { contentDescription = accessibilityDesc }
    ) {
        Canvas(modifier = Modifier.size(260.dp)) {
            val strokeWidth = 16.dp.toPx()
            
            // 1. Background Track
            drawArc(
                color = if (isLowPerf) outlineColor.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. Neon Glow layer
            if (!isLowPerf) {
                drawArc(
                    color = gaugeColor.copy(alpha = pulseAlpha * 0.15f),
                    startAngle = 140f,
                    sweepAngle = animatedProgress.coerceAtMost(1f) * 260f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth + 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 3. Main Progress
            if (isLowPerf) {
                drawArc(
                    color = gaugeColor,
                    startAngle = 140f,
                    sweepAngle = animatedProgress.coerceAtMost(1f) * 260f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            } else {
                rotate(degrees = 140f) {
                    drawArc(
                        brush = neonGradient,
                        startAngle = 0f,
                        sweepAngle = animatedProgress.coerceAtMost(1f) * 260f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // 4. Highlight
            if (!isLowPerf) {
                drawArc(
                    color = Color.White.copy(alpha = 0.3f),
                    startAngle = 140f,
                    sweepAngle = (animatedProgress * 260f).coerceAtMost(15f),
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val formatted = formatter.formatData((used * 1073741824.0).toLong())
            val parts = formatted.split(" ")
            val valText = parts.getOrNull(0) ?: "0"
            val unitText = parts.getOrNull(1) ?: "o"
            
            Text(
                text = valText,
                fontSize = if (valText.length > 5) 64.sp else 88.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-4).sp,
                color = onSurfaceColor
            )
            Text(
                text = unitText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = gaugeColor.copy(alpha = 0.9f),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = gaugeColor.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, gaugeColor.copy(alpha = 0.3f)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.limit_label).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = gaugeColor.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    val limitText = if (quota > 0) formatter.formatData((quota * 1024 * 1024 * 1024).toLong()) else "--"
                    Text(
                        text = limitText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = gaugeColor
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitleSommite(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) { 
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 4.dp), verticalAlignment = Alignment.CenterVertically) { 
        Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = color, letterSpacing = 1.5.sp) 
    } 
}

@Composable
fun ControlRowSommite(
    title: String, 
    desc: String, 
    checked: Boolean, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    colors: DataConsColors, 
    onClick: (() -> Unit)? = null, 
    onCheckedChange: (Boolean) -> Unit
) { 
    val haptic = rememberArvoHaptic()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { 
                haptic()
                onClick?.invoke() 
            }
            .padding(20.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.12f)) { 
            Icon(icon, null, tint = colors.primary, modifier = Modifier.padding(12.dp)) 
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.weight(1f)) { 
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = colors.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Medium) 
        }
        Switch(
            checked = checked, 
            onCheckedChange = { 
                haptic()
                onCheckedChange(it) 
            }, 
            colors = SwitchDefaults.colors(checkedThumbColor = colors.primary)
        )
    }
}

@Composable
fun SecurityErrorSommite(title: String, message: String) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020617)), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Rounded.Lock, null, tint = Color.Red, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(title, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 22.sp)
        }
    }
}
