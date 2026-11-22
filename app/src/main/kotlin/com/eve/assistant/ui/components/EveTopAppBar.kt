package com.eve.assistant.ui.components

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eve.assistant.ui.home.ConnectionStatus
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.GlassBorder
import com.eve.assistant.ui.theme.GlassSurface
import com.eve.assistant.ui.theme.PrimaryGradient
import com.eve.assistant.ui.theme.TextPrimary
import com.eve.assistant.ui.theme.TextSecondary

/**
 * Top app bar with glowing EVE monogram, telemetry status, and quick shortcuts.
 */
@Composable
fun EveTopAppBar(
    connectionStatus: ConnectionStatus,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Monogram & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PrimaryGradient)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF070C1B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "E",
                        color = ElectricCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = "EVE",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Personal AI",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Action Tray: Status Pill + History + Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusPill(connectionStatus = connectionStatus)

            // History Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, CircleShape)
                    .clickable(onClick = onHistoryClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = EveIcons.History,
                    contentDescription = "Conversation History",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, CircleShape)
                    .clickable(onClick = onSettingsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = EveIcons.Settings,
                    contentDescription = "Assistant Settings",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
