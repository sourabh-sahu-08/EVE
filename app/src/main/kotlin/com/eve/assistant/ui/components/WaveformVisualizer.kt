package com.eve.assistant.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eve.assistant.ui.home.EveState
import com.eve.assistant.ui.theme.CyberViolet
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.StateError
import com.eve.assistant.ui.theme.StateSpeaking

/**
 * Animated sound-wave frequency spectrum equalizer for voice input/output.
 */
@Composable
fun WaveformVisualizer(
    eveState: EveState,
    modifier: Modifier = Modifier,
    barCount: Int = 9,
    maxHeight: Dp = 38.dp,
    barWidth: Dp = 3.5.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformBars")

    val baseDelays = listOf(150, 320, 0, 480, 220, 600, 100, 390, 260)
    val durations = listOf(420, 560, 480, 620, 380, 510, 460, 590, 430)

    val isActive = eveState == EveState.LISTENING || eveState == EveState.SPEAKING

    val primaryColor = when (eveState) {
        EveState.LISTENING -> ElectricCyan
        EveState.SPEAKING -> StateSpeaking
        EveState.ERROR -> StateError
        else -> CyberViolet
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.25f))
    )

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(4.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val animDuration = durations[i % durations.size]
            val animDelay = baseDelays[i % baseDelays.size]

            val targetHeightFraction by infiniteTransition.animateFloat(
                initialValue = if (isActive) 0.18f else 0.08f,
                targetValue = if (isActive) 0.95f else 0.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animDuration,
                        delayMillis = animDelay,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "BarHeight_$i"
            )

            val currentHeight = maxHeight * targetHeightFraction

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(currentHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(gradient)
            )
        }
    }
}
