package com.eve.assistant.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// EVE Futuristic AI Visual Identity Palette
// ==========================================

// Deep Space & Midnight Navy Backgrounds
val SpaceBlack = Color(0xFF030712)
val MidnightNavy = Color(0xFF070C1B)
val NebulaDark = Color(0xFF0D1426)
val SurfaceCard = Color(0xFF111A30)
val SurfaceCardElevated = Color(0xFF16203A)

// Electric Blue & Cyan Accents
val ElectricCyan = Color(0xFF00E5FF)
val CyanBright = Color(0xFF64FFDA)
val NeonAzure = Color(0xFF007AFF)
val DeepCyan = Color(0xFF00838F)

// Cyber Violet & Purple Accents
val CyberViolet = Color(0xFF7C4DFF)
val Ultraviolet = Color(0xFF9D4EDD)
val DeepViolet = Color(0xFF4A148C)
val PlasmaPink = Color(0xFFE040FB)

// State-Specific Aura Colors
val StateIdleCyan = Color(0xFF00E5FF)
val StateIdleViolet = Color(0xFF7C4DFF)
val StateListening = Color(0xFF00F5FF)
val StateThinking = Color(0xFFA855F7)
val StateSpeaking = Color(0xFF38BDF8)
val StateError = Color(0xFFFF3366)
val StateSuccess = Color(0xFF10B981)

// Glassmorphism & Borders
val GlassSurface = Color(0x14FFFFFF)
val GlassSurfacePressed = Color(0x24FFFFFF)
val GlassSurfaceHeavy = Color(0x33111A30)
val GlassBorder = Color(0x26FFFFFF)
val GlassBorderActive = Color(0x6600E5FF)
val GlassBorderViolet = Color(0x667C4DFF)

// Typography & Content
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)
val TextDisabled = Color(0xFF475569)

// Gradients
val PrimaryGradient = Brush.linearGradient(
    colors = listOf(ElectricCyan, CyberViolet)
)

val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(MidnightNavy, SpaceBlack)
)

val OrbIdleGradient = Brush.radialGradient(
    colors = listOf(ElectricCyan, CyberViolet, Color.Transparent)
)

val GlassBorderGradient = Brush.linearGradient(
    colors = listOf(Color(0x4DFFFFFF), Color(0x0DFFFFFF))
)
