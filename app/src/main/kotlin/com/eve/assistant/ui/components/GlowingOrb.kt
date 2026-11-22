package com.eve.assistant.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eve.assistant.ui.home.EveState
import com.eve.assistant.ui.theme.CyberViolet
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.NeonAzure
import com.eve.assistant.ui.theme.StateError
import com.eve.assistant.ui.theme.StateListening
import com.eve.assistant.ui.theme.StateSpeaking
import com.eve.assistant.ui.theme.StateThinking
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated central AI orb representing EVE's cognitive presence.
 * Features state-driven shaders, breathing effects, sound-wave ripples,
 * orbital thinking rings, and harmonic speech modulation.
 */
@Composable
fun GlowingOrb(
    eveState: EveState,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbMotion")

    // Gentle breathing scale for IDLE
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingScale"
    )

    // Continuous rotation for THINKING orbital rings
    val orbitalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalRotation"
    )

    // Wave expansion phase for LISTENING and SPEAKING
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    // Harmonic pulse for SPEAKING
    val speechPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SpeechPulse"
    )

    // Smooth color & intensity transition on state change
    val stateTransitionProgress = remember { Animatable(0f) }
    LaunchedEffect(eveState) {
        stateTransitionProgress.snapTo(0f)
        stateTransitionProgress.animateTo(1f, animationSpec = tween(500))
    }

    val primaryColor = when (eveState) {
        EveState.IDLE -> ElectricCyan
        EveState.LISTENING -> StateListening
        EveState.THINKING -> StateThinking
        EveState.SPEAKING -> StateSpeaking
        EveState.ERROR -> StateError
    }

    val secondaryColor = when (eveState) {
        EveState.IDLE -> CyberViolet
        EveState.LISTENING -> NeonAzure
        EveState.THINKING -> CyberViolet
        EveState.SPEAKING -> ElectricCyan
        EveState.ERROR -> Color(0xFFB91C1C)
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 3.4f

            // Dynamic scale factor based on state
            val currentScale = when (eveState) {
                EveState.IDLE -> breathingScale
                EveState.LISTENING -> 1.0f + (wavePhase * 0.08f)
                EveState.THINKING -> 0.96f
                EveState.SPEAKING -> speechPulse
                EveState.ERROR -> 1.02f
            }

            val radius = baseRadius * currentScale

            // 1. Outer Ambient Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.28f),
                        secondaryColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 2.2f
                ),
                radius = radius * 2.2f,
                center = center
            )

            // 2. State-Specific Wave / Ripple Rings
            when (eveState) {
                EveState.LISTENING -> {
                    // Concentric sound-wave ripples expanding outwards
                    for (i in 0..2) {
                        val offsetPhase = (wavePhase + (i * 0.33f)) % 1f
                        val waveRadius = radius * (1.1f + offsetPhase * 0.9f)
                        val alpha = ((1f - offsetPhase) * 0.6f).coerceIn(0f, 1f)
                        drawCircle(
                            color = primaryColor.copy(alpha = alpha),
                            radius = waveRadius,
                            center = center,
                            style = Stroke(width = 2.5.dp.toPx() * (1f - offsetPhase))
                        )
                    }
                }
                EveState.SPEAKING -> {
                    // Harmonic energy modulation rings
                    for (i in 0..1) {
                        val offsetPhase = (wavePhase + (i * 0.5f)) % 1f
                        val waveRadius = radius * (1.15f + offsetPhase * 0.75f)
                        val alpha = ((1f - offsetPhase) * 0.5f).coerceIn(0f, 1f)
                        drawCircle(
                            color = secondaryColor.copy(alpha = alpha),
                            radius = waveRadius,
                            center = center,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), wavePhase * 50f)
                            )
                        )
                    }
                }
                EveState.THINKING -> {
                    // Dual rotating orbital rings with planetary energy beads
                    rotate(orbitalRotation, pivot = center) {
                        drawOval(
                            brush = Brush.sweepGradient(
                                colors = listOf(primaryColor, secondaryColor, Color.Transparent, primaryColor),
                                center = center
                            ),
                            topLeft = Offset(center.x - radius * 1.55f, center.y - radius * 0.6f),
                            size = androidx.compose.ui.geometry.Size(radius * 3.1f, radius * 1.2f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        // Orbital bead
                        val beadAngle = orbitalRotation * (PI / 180.0)
                        val beadX = center.x + (radius * 1.55f * cos(beadAngle)).toFloat()
                        val beadY = center.y + (radius * 0.6f * sin(beadAngle)).toFloat()
                        drawCircle(
                            color = primaryColor,
                            radius = 4.5.dp.toPx(),
                            center = Offset(beadX, beadY)
                        )
                    }

                    rotate(-orbitalRotation * 1.4f, pivot = center) {
                        drawOval(
                            brush = Brush.sweepGradient(
                                colors = listOf(secondaryColor, primaryColor, Color.Transparent, secondaryColor),
                                center = center
                            ),
                            topLeft = Offset(center.x - radius * 0.6f, center.y - radius * 1.55f),
                            size = androidx.compose.ui.geometry.Size(radius * 1.2f, radius * 3.1f),
                            style = Stroke(width = 1.8.dp.toPx())
                        )
                    }
                }
                EveState.IDLE -> {
                    // Subtle glowing ambient perimeter ring
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.25f),
                        radius = radius * 1.25f,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 25f), orbitalRotation * 0.5f)
                        )
                    )
                }
                EveState.ERROR -> {
                    // Error alert ring
                    drawCircle(
                        color = StateError.copy(alpha = 0.45f),
                        radius = radius * 1.3f,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }

            // 3. Central Core Glowing Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        secondaryColor,
                        Color(0xFF030712).copy(alpha = 0.9f)
                    ),
                    center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // 4. Specular Core Highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        primaryColor.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
                    radius = radius * 0.45f
                ),
                radius = radius * 0.45f,
                center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
            )
        }
    }
}
