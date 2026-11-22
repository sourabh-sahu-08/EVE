package com.eve.assistant.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eve.assistant.ui.components.EveIcons
import com.eve.assistant.ui.components.GlassCard
import com.eve.assistant.ui.theme.BackgroundGradient
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.GlassSurface
import com.eve.assistant.ui.theme.SpaceBlack
import com.eve.assistant.ui.theme.TextPrimary
import com.eve.assistant.ui.theme.TextSecondary
import com.eve.assistant.ui.theme.TextTertiary

/**
 * Main Assistant Settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVoiceSettings: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SpaceBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ASSISTANT SETTINGS",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = EveIcons.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceBlack
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundGradient)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Sub-category Links
            Text(
                text = "SPECIALIZED CONFIGURATION",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            NavigationLinkCard(
                icon = EveIcons.RecordVoiceOver,
                title = "Voice Profile & Synthesis",
                subtitle = "Tuning for ${uiState.selectedVoice.displayName}, speech rate and pitch",
                onClick = onNavigateToVoiceSettings
            )

            NavigationLinkCard(
                icon = EveIcons.Security,
                title = "Privacy & Permissions",
                subtitle = "Microphone access, telemetry, and on-device data",
                onClick = onNavigateToPrivacy
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Section 2: General Assistant Toggles
            Text(
                text = "INTERACTION CONTROLS",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingToggleRow(
                        icon = EveIcons.Mic,
                        title = "Wake Phrase \"Hey Eve\"",
                        description = "Enable hands-free wake word listening",
                        checked = uiState.isWakeWordEnabled,
                        onCheckedChange = { viewModel.setWakeWordEnabled(it) }
                    )

                    SettingToggleRow(
                        icon = EveIcons.Vibration,
                        title = "Haptic Response",
                        description = "Vibrate subtly on state transitions",
                        checked = uiState.isHapticFeedbackEnabled,
                        onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) }
                    )

                    SettingToggleRow(
                        icon = EveIcons.RecordVoiceOver,
                        title = "Continuous Conversation",
                        description = "Keep listening after speech completion",
                        checked = uiState.isContinuousListeningEnabled,
                        onCheckedChange = { viewModel.setContinuousListeningEnabled(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section 3: Diagnostic Telemetry Card
            Text(
                text = "SYSTEM ARCHITECTURE",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = EveIcons.Info, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                        Text(text = "EVE Engine Telemetry", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    DiagnosticRow(label = "Platform", value = "Android (API 24+)")
                    DiagnosticRow(label = "Architecture", value = uiState.architecture)
                    DiagnosticRow(label = "Backend Engine", value = "Node.js + Express + TS")
                    DiagnosticRow(label = "Client Version", value = uiState.appVersion)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NavigationLinkCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = EveIcons.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) ElectricCyan else TextTertiary,
            modifier = Modifier.size(22.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SpaceBlack,
                checkedTrackColor = ElectricCyan,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = GlassSurface
            )
        )
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextTertiary, fontSize = 12.sp)
        Text(text = value, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
