package com.eve.assistant.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eve.assistant.ui.home.EveState
import com.eve.assistant.ui.theme.CyberViolet
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.GlassBorder
import com.eve.assistant.ui.theme.GlassBorderActive
import com.eve.assistant.ui.theme.SpaceBlack
import com.eve.assistant.ui.theme.StateError
import com.eve.assistant.ui.theme.StateListening

/**
 * Futuristic tactile microphone interaction button with state-responsive aura.
 */
@Composable
fun MicInteractionButton(
    eveState: EveState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 82.dp
) {
    val isListening = eveState == EveState.LISTENING

    val infiniteTransition = rememberInfiniteTransition(label = "MicAura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.32f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 900 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraScale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = if (isListening) 0.65f else 0.25f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 900 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraAlpha"
    )

    val buttonGradient = when (eveState) {
        EveState.LISTENING -> Brush.linearGradient(listOf(StateListening, CyberViolet))
        EveState.THINKING -> Brush.linearGradient(listOf(CyberViolet, Color(0xFF6D28D9)))
        EveState.SPEAKING -> Brush.linearGradient(listOf(ElectricCyan, CyberViolet))
        EveState.ERROR -> Brush.linearGradient(listOf(StateError, Color(0xFF991B1B)))
        EveState.IDLE -> Brush.linearGradient(listOf(ElectricCyan, CyberViolet))
    }

    Box(
        modifier = modifier
            .size(size * 1.4f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing pulse ring
        Box(
            modifier = Modifier
                .size(size)
                .scale(auraScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = auraAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        // Core tactile glass button
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(buttonGradient)
                .border(
                    BorderStroke(
                        width = 1.5.dp,
                        brush = if (isListening) Brush.linearGradient(listOf(ElectricCyan, Color.White)) else Brush.linearGradient(listOf(GlassBorderActive, GlassBorder))
                    ),
                    shape = CircleShape
                )
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isListening) EveIcons.Stop else EveIcons.Mic,
                contentDescription = if (isListening) "Stop Listening" else "Activate Voice",
                tint = if (isListening) Color.White else SpaceBlack,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
