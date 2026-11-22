package com.eve.assistant.data.model

data class ChatMessage(
    val role: String,
    val content: String
)

data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val isEstimated: Boolean
)

data class FallbackStep(
    val provider: String,
    val model: String,
    val reason: String,
    val category: String,
    val durationMs: Long
)

data class InteractResponse(
    val text: String,
    val provider: String,
    val model: String,
    val latencyMs: Long,
    val fallbackCount: Int,
    val fallbackHistory: List<FallbackStep>,
    val tokens: TokenUsage
)

sealed interface AssistantResult {
    data class Success(val response: InteractResponse) : AssistantResult
    data class Error(val message: String, val fallbackHistory: List<FallbackStep> = emptyList()) : AssistantResult
}
