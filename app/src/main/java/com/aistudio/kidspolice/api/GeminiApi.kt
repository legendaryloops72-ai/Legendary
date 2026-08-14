package com.aistudio.kidspolice.api

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerationConfig(
    val thinkingConfig: ThinkingConfig? = null,
    val maxOutputTokens: Int? = null,
    val temperature: Double? = null
)

@Serializable
data class ThinkingConfig(
    val thinkingLevel: String? = null
)

@Serializable
data class Tool(
    val googleSearch: GoogleSearch? = null
)

@Serializable
class GoogleSearch()

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}
