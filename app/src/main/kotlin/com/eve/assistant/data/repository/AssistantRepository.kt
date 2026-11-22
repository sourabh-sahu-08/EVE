package com.eve.assistant.data.repository

import com.eve.assistant.data.api.AssistantApiClient
import com.eve.assistant.data.model.AssistantResult
import com.eve.assistant.data.model.ChatMessage

interface AssistantRepository {
    fun setServerUrl(url: String)
    fun getServerUrl(): String
    suspend fun interact(message: String, history: List<ChatMessage> = emptyList()): AssistantResult
}

class DefaultAssistantRepository(
    private val apiClient: AssistantApiClient = AssistantApiClient()
) : AssistantRepository {

    override fun setServerUrl(url: String) {
        apiClient.setBaseUrl(url)
    }

    override fun getServerUrl(): String {
        return apiClient.getBaseUrl()
    }

    override suspend fun interact(
        message: String,
        history: List<ChatMessage>
    ): AssistantResult {
        return apiClient.interact(message, history)
    }
}
