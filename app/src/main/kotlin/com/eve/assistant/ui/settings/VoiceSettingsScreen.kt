package com.eve.assistant.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eve.assistant.ui.components.EveIcons
import com.eve.assistant.ui.components.GlassCard
import com.eve.assistant.ui.theme.BackgroundGradient
import com.eve.assistant.ui.theme.CyberViolet
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.GlassBorder
import com.eve.assistant.ui.theme.GlassSurface
import com.eve.assistant.ui.theme.SpaceBlack
import com.eve.assistant.ui.theme.TextPrimary
import com.eve.assistant.ui.theme.TextSecondary
import com.eve.assistant.ui.theme.TextTertiary

/**
 * Screen for configuring synthetic voice personality, speech rate, pitch, and acoustic filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingsScreen(
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
                        text = "VOICE CONFIGURATION",
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
            // Section 1: Voice Personality Models
            Text(
                text = "NEURAL VOICE PERSONALITY",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                VoicePersonality.values().forEach { voice ->
                    val isSelected = uiState.selectedVoice == voice
                    VoiceSelectionCard(
                        voice = voice,
                        isSelected = isSelected,
                        onClick = { viewModel.selectVoice(voice) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Section 2: Synthesis Rate & Pitch Sliders
            Text(
                text = "SYNTHESIS MODULATION",
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
                    // Speech Rate Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = EveIcons.Speed,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Speech Rate",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "%.2fx".format(uiState.speechRate),
                                color = ElectricCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Slider(
                            value = uiState.speechRate,
                            onValueChange = { viewModel.setSpeechRate(it) },
                            valueRange = 0.5f..2.0f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricCyan,
                                activeTrackColor = ElectricCyan,
                                inactiveTrackColor = GlassSurface
                            )
                        )
                    }

                    // Pitch Multiplier Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = EveIcons.VolumeUp,
                                    contentDescription = null,
                                    tint = CyberViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Pitch Frequency",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "%.2fx".format(uiState.pitchMultiplier),
                                color = CyberViolet,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Slider(
                            value = uiState.pitchMultiplier,
                            onValueChange = { viewModel.setPitchMultiplier(it) },
                            valueRange = 0.75f..1.5f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = CyberViolet,
                                activeTrackColor = CyberViolet,
                                inactiveTrackColor = GlassSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Section 3: Acoustic Noise Suppression
            Text(
                text = "ACOUSTIC PROCESSING",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = EveIcons.Hearing,
                        contentDescription = null,
                        tint = if (uiState.isNoiseSuppressionEnabled) ElectricCyan else TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Neural Noise Isolation",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Filter ambient room reverberation and background static",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Switch(
                        checked = uiState.isNoiseSuppressionEnabled,
                        onCheckedChange = { viewModel.setNoiseSuppressionEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SpaceBlack,
                            checkedTrackColor = ElectricCyan,
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = GlassSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun VoiceSelectionCard(
    voice: VoicePersonality,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ElectricCyan.copy(alpha = 0.12f) else GlassSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) ElectricCyan else GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voice.displayName,
                    color = if (isSelected) ElectricCyan else TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = voice.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) ElectricCyan else GlassSurface)
                    .border(1.dp, if (isSelected) ElectricCyan else GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = EveIcons.Check,
                        contentDescription = "Selected",
                        tint = SpaceBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
