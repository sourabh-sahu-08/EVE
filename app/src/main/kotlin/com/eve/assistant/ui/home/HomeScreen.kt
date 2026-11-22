package com.eve.assistant.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eve.assistant.ui.components.EveIcons
import com.eve.assistant.ui.components.EveTopAppBar
import com.eve.assistant.ui.components.GlassCard
import com.eve.assistant.ui.components.GlowingOrb
import com.eve.assistant.ui.components.MicInteractionButton
import com.eve.assistant.ui.components.WaveformVisualizer
import com.eve.assistant.ui.theme.BackgroundGradient
import com.eve.assistant.ui.theme.ElectricCyan
import com.eve.assistant.ui.theme.GlassBorder
import com.eve.assistant.ui.theme.GlassSurface
import com.eve.assistant.ui.theme.SpaceBlack
import com.eve.assistant.ui.theme.StateError
import com.eve.assistant.ui.theme.StateListening
import com.eve.assistant.ui.theme.StateSpeaking
import com.eve.assistant.ui.theme.StateThinking
import com.eve.assistant.ui.theme.TextPrimary
import com.eve.assistant.ui.theme.TextSecondary
import com.eve.assistant.ui.theme.TextTertiary

/**
 * Premium Home Screen interface for EVE.
 * Houses the animated central AI orb, voice status telemetry,
 * sound-wave visualizer, conversation preview, and interactive state switcher.
 */
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SpaceBlack,
        topBar = {
            EveTopAppBar(
                connectionStatus = uiState.connectionStatus,
                onHistoryClick = onNavigateToHistory,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Greeting & Branding Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = uiState.greeting,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How can I assist you?",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Central Animated Glowing Orb
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    GlowingOrb(
                        eveState = uiState.eveState,
                        size = 250.dp,
                        onClick = { viewModel.onMicClicked() }
                    )
                }

                // 3. Voice Status & State Indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        val stateColor = when (uiState.eveState) {
                            EveState.IDLE -> ElectricCyan
                            EveState.LISTENING -> StateListening
                            EveState.THINKING -> StateThinking
                            EveState.SPEAKING -> StateSpeaking
                            EveState.ERROR -> StateError
                        }

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(stateColor)
                        )

                        Text(
                            text = uiState.statusMessage.uppercase(),
                            color = stateColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // 4. Waveform Audio Visualizer (active during Listening / Speaking)
                    WaveformVisualizer(
                        eveState = uiState.eveState,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Conversation Preview or Wake-Word Tip Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    onClick = onNavigateToHistory
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LATEST SESSION",
                                color = TextTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.0.sp
                            )
                            Icon(
                                imageVector = EveIcons.ChevronRight,
                                contentDescription = "View History",
                                tint = TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.lastUserQuery != null && uiState.lastEveResponse != null) {
                            Text(
                                text = "\"${uiState.lastUserQuery}\"",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.lastEveResponse ?: "",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                maxLines = 2
                            )
                        } else {
                            Text(
                                text = "Say \"Hey Eve\" or tap the microphone to begin voice interaction.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // 6. Error Banner (Animated Visibility)
                AnimatedVisibility(
                    visible = uiState.eveState == EveState.ERROR && uiState.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        backgroundColor = Color(0x33DC2626),
                        borderGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(StateError, Color(0x44DC2626))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = EveIcons.ErrorOutline,
                                contentDescription = "Error",
                                tint = StateError
                            )
                            Text(
                                text = uiState.errorMessage ?: "An unexpected error occurred",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = EveIcons.Refresh,
                                contentDescription = "Retry",
                                tint = ElectricCyan,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.retryConnection() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 7. Interactive State Testing Selector (for Phase 1 UI Verification)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "UI STATE PREVIEW",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.0.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EveState.values().forEach { state ->
                            val isSelected = uiState.eveState == state
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ElectricCyan.copy(alpha = 0.22f) else GlassSurface)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) ElectricCyan else GlassBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setEveState(state) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.name.take(4),
                                    color = if (isSelected) ElectricCyan else TextTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 8. Large Central Tactile Microphone Button
                MicInteractionButton(
                    eveState = uiState.eveState,
                    onClick = { viewModel.onMicClicked() },
                    size = 80.dp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
