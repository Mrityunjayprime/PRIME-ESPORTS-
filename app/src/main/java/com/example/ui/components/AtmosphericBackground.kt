package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricViolet

/**
 * Atmospheric animated dark background with subtle drifting neon luminescence
 * and esports melody lighting.
 */
@Composable
fun AtmosphericBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "AtmosphericTransition")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Base dark esports gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    DarkEsportsBg,
                    DarkSurface,
                    DarkEsportsBg
                )
            )
        )

        // Drifting Cyan nebula orb (top-right to mid)
        val center1 = Offset(
            x = width * (0.85f - pulse1 * 0.25f),
            y = height * (0.12f + pulse1 * 0.18f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CyberCyan.copy(alpha = 0.14f),
                    CyberCyan.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = center1,
                radius = width * 0.65f
            ),
            center = center1,
            radius = width * 0.65f
        )

        // Drifting Electric Violet nebula orb (bottom-left to center)
        val center2 = Offset(
            x = width * (0.15f + pulse2 * 0.25f),
            y = height * (0.78f - pulse2 * 0.22f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ElectricViolet.copy(alpha = 0.16f),
                    ElectricViolet.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = center2,
                radius = width * 0.75f
            ),
            center = center2,
            radius = width * 0.75f
        )

        // Subtle tech grid lines
        val step = 80f
        val lineAlpha = 0.025f
        var x = 0f
        while (x < width) {
            drawLine(
                color = Color.White.copy(alpha = lineAlpha),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += step
        }
    }
}
