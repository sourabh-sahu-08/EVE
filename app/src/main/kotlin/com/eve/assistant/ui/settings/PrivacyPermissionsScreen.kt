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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
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
import com.eve.assistant.ui.theme.StateError
import com.eve.assistant.ui.theme.StateSuccess
import com.eve.assistant.ui.theme.TextPrimary
import com.eve.assistant.ui.theme.TextSecondary
import com.eve.assistant.ui.theme.TextTertiary

/**
 * Screen presenting Android runtime permission status, privacy guarantees, and data retention policies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPermissionsScreen(
    onNavigateBack: () -> Unit,
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
                        text = "PRIVACY & SECURITY",
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
            // Section 1: Android Runtime Permissions
            Text(
                text = "SYSTEM PERMISSIONS STATUS",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PermissionStatusRow(
                        icon = EveIcons.Mic,
                        title = "Microphone Access",
                        description = "Required for capturing user voice queries",
                        isGranted = uiState.isMicPermissionGranted
                    )

                    PermissionStatusRow(
                        icon = EveIcons.Notifications,
                        title = "Push Notifications",
                        description = "Displays ambient background assistant state",
                        isGranted = uiState.isNotificationPermissionGranted
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section 2: Cloud Telemetry & Sync Policies
            Text(
                text = "DATA HANDLING POLICIES",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = EveIcons.CloudDone,
                            contentDescription = null,
                            tint = if (uiState.isCloudSyncEnabled) ElectricCyan else TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cloud Session Sync",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Synchronize conversation transcripts with encrypted backend",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = uiState.isCloudSyncEnabled,
                            onCheckedChange = { viewModel.setCloudSyncEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpaceBlack,
                                checkedTrackColor = ElectricCyan,
                                uncheckedThumbColor = TextTertiary,
                                uncheckedTrackColor = GlassSurface
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = EveIcons.Security,
                            contentDescription = null,
                            tint = if (uiState.isAnonymousTelemetryEnabled) ElectricCyan else TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Anonymous Diagnostic Telemetry",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Transmit anonymized latency metrics to optimize synthesis",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = uiState.isAnonymousTelemetryEnabled,
                            onCheckedChange = { viewModel.setAnonymousTelemetryEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpaceBlack,
                                checkedTrackColor = ElectricCyan,
                                uncheckedThumbColor = TextTertiary,
                                uncheckedTrackColor = GlassSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Section 3: Data Reset & Purge
            Text(
                text = "DATA RETENTION & RESET",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            Button(
                onClick = { viewModel.resetToDefaults() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x22DC2626),
                    contentColor = StateError
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StateError.copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(imageVector = EveIcons.DeleteForever, contentDescription = null, tint = StateError)
                    Text(text = "Reset All Preferences & Local Data", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PermissionStatusRow(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isGranted) StateSuccess.copy(alpha = 0.15f) else StateError.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) StateSuccess else StateError,
                modifier = Modifier.size(20.dp)
            )
        }

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = EveIcons.CheckCircle,
                contentDescription = null,
                tint = if (isGranted) StateSuccess else TextTertiary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isGranted) "Active" else "Denied",
                color = if (isGranted) StateSuccess else StateError,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
