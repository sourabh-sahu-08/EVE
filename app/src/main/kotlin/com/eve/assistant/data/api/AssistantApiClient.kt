package com.eve.assistant.data.api

import com.eve.assistant.data.model.AssistantResult
import com.eve.assistant.data.model.ChatMessage
import com.eve.assistant.data.model.FallbackStep
import com.eve.assistant.data.model.InteractResponse
import com.eve.assistant.data.model.TokenUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AssistantApiClient(
    private var baseUrl: String = "http://127.0.0.1:3000"
) {

    fun setBaseUrl(url: String) {
        this.baseUrl = url.trimEnd('/')
    }

    fun getBaseUrl(): String = baseUrl

    suspend fun interact(
        message: String,
        history: List<ChatMessage> = emptyList()
    ): AssistantResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("$baseUrl/api/assistant/interact")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 8000
                readTimeout = 40000 // Allow up to 40s for local Ollama on laptop
                doOutput = true
                doInput = true
            }

            // Build JSON payload
            val rootObj = JSONObject().apply {
                put("message", message)
                val historyArray = JSONArray()
                for (item in history) {
                    val msgObj = JSONObject().apply {
                        put("role", item.role)
                        put("content", item.content)
                    }
                    historyArray.put(msgObj)
                }
                put("history", historyArray)
            }

            // Send request body
            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(rootObj.toString())
                writer.flush()
            }

            val statusCode = connection.responseCode
            val isSuccess = statusCode in 200..299

            val inputStream = if (isSuccess) connection.inputStream else connection.errorStream
            val responseText = BufferedReader(InputStreamReader(inputStream ?: connection.inputStream, "UTF-8")).use {
                it.readText()
            }

            val json = JSONObject(responseText)

            if (isSuccess && json.optBoolean("success", false)) {
                val respObj = json.getJSONObject("response")
                val text = respObj.getString("text")
                val provider = respObj.getString("provider")
                val model = respObj.getString("model")
                val latencyMs = respObj.optLong("latencyMs", 0)
                val fallbackCount = respObj.optInt("fallbackCount", 0)

                val tokensObj = respObj.optJSONObject("tokens")
                val tokens = TokenUsage(
                    promptTokens = tokensObj?.optInt("promptTokens", 0) ?: 0,
                    completionTokens = tokensObj?.optInt("completionTokens", 0) ?: 0,
                    totalTokens = tokensObj?.optInt("totalTokens", 0) ?: 0,
                    isEstimated = tokensObj?.optBoolean("isEstimated", true) ?: true
                )

                val fallbackArray = respObj.optJSONArray("fallbackHistory")
                val fallbackHistory = mutableListOf<FallbackStep>()
                if (fallbackArray != null) {
                    for (i in 0 until fallbackArray.length()) {
                        val stepObj = fallbackArray.getJSONObject(i)
                        fallbackHistory.add(
                            FallbackStep(
                                provider = stepObj.optString("provider", "unknown"),
                                model = stepObj.optString("model", ""),
                                reason = stepObj.optString("reason", ""),
                                category = stepObj.optString("category", ""),
                                durationMs = stepObj.optLong("durationMs", 0)
                            )
                        )
                    }
                }

                AssistantResult.Success(
                    InteractResponse(
                        text = text,
                        provider = provider,
                        model = model,
                        latencyMs = latencyMs,
                        fallbackCount = fallbackCount,
                        fallbackHistory = fallbackHistory,
                        tokens = tokens
                    )
                )
            } else {
                val errorMsg = json.optString("error", "HTTP error $statusCode")
                val fallbackArray = json.optJSONArray("fallbackHistory")
                val fallbackHistory = mutableListOf<FallbackStep>()
                if (fallbackArray != null) {
                    for (i in 0 until fallbackArray.length()) {
                        val stepObj = fallbackArray.getJSONObject(i)
                        fallbackHistory.add(
                            FallbackStep(
                                provider = stepObj.optString("provider", "unknown"),
                                model = stepObj.optString("model", ""),
                                reason = stepObj.optString("reason", ""),
                                category = stepObj.optString("category", ""),
                                durationMs = stepObj.optLong("durationMs", 0)
                            )
                        )
                    }
                }
                AssistantResult.Error(errorMsg, fallbackHistory)
            }
        } catch (e: Exception) {
            val message = if (e is java.net.ConnectException || e is java.net.SocketTimeoutException) {
                "Cannot connect to EVE backend at $baseUrl. Ensure the backend server is running."
            } else {
                e.localizedMessage ?: "Network error during AI interaction"
            }
            AssistantResult.Error(message)
        } finally {
            connection?.disconnect()
        }
    }
}
