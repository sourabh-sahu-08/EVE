package com.eve.assistant.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eve.assistant.ui.home.ConnectionStatus
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.GlassBorder
import com.eve.assistant.ui.theme.GlassSurface
import com.eve.assistant.ui.theme.StateError
import com.eve.assistant.ui.theme.StateSuccess
import com.eve.assistant.ui.theme.TextPrimary
import com.eve.assistant.ui.theme.TextTertiary

/**
 * Futuristic telemetry pill showing neural cloud connectivity and latency.
 */
@Composable
fun StatusPill(
    connectionStatus: ConnectionStatus,
    latencyMs: Int = 24,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LedBlink")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotAlpha"
    )

    val (statusText, statusColor) = when (connectionStatus) {
        ConnectionStatus.CONNECTED -> "Cloud AI: ${latencyMs}ms" to StateSuccess
        ConnectionStatus.CONNECTING -> "Connecting..." to ElectricCyan
        ConnectionStatus.OFFLINE -> "Offline" to StateError
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Glowing status LED
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = dotAlpha))
        )

        Text(
            text = statusText,
            color = TextPrimary,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}
