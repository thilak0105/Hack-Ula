package com.mentora.mobile.ai

import android.content.Context
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import com.runanywhere.sdk.data.models.ModelInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import org.json.JSONArray
import org.json.JSONObject

/**
 * AI Manager for handling on-device AI operations with RunAnywhere SDK
 */
class AIManager(private val context: Context) {

    companion object {
        private const val TAG = "AIManager"
    }

    /**
     * Get list of available AI models
     * @return JSON string of available models
     */
    suspend fun getAvailableModels(): String {
        return try {
            val models = listAvailableModels()
            val jsonArray = JSONArray()

            models.forEach { model ->
                val jsonModel = JSONObject().apply {
                    put("id", model.id)
                    put("name", model.name)
                    put("type", model.type)
                    put("sizeInMB", String.format("%.2f", model.sizeBytes / (1024.0 * 1024.0)))
                    put("isDownloaded", model.isDownloaded)
                    put("isLoaded", model.isLoaded)
                }
                jsonArray.put(jsonModel)
            }

            jsonArray.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get available models: ${e.message}", e)
            JSONArray().toString()
        }
    }

    /**
     * Download a model with progress tracking
     * @param modelId The model ID to download
     * @return Flow of download progress (0.0 to 1.0)
     */
    fun downloadModel(modelId: String): Flow<Float> {
        return RunAnywhere.downloadModel(modelId).catch { e ->
            Log.e(TAG, "Download failed: ${e.message}", e)
            emit(0f)
        }
    }

    /**
     * Load a model into memory for inference
     * @param modelId The model ID to load
     * @return true if successful, false otherwise
     */
    suspend fun loadModel(modelId: String): Boolean {
        return try {
            val success = RunAnywhere.loadModel(modelId)
            Log.d(TAG, "Model load ${if (success) "successful" else "failed"}: $modelId")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load model: ${e.message}", e)
            false
        }
    }

    /**
     * Unload the currently loaded model
     * @return true if successful, false otherwise
     */
    suspend fun unloadModel(): Boolean {
        return try {
            RunAnywhere.unloadModel()
            Log.d(TAG, "Model unloaded")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unload model: ${e.message}", e)
            false
        }
    }

    /**
     * Generate text response (non-streaming)
     * @param prompt The user prompt
     * @return Generated text response
     */
    suspend fun generateText(prompt: String): String {
        return try {
            val response = RunAnywhere.generate(prompt)
            Log.d(TAG, "Generated ${response.length} characters")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed: ${e.message}", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Generate text response with streaming (real-time token generation)
     * @param prompt The user prompt
     * @return Flow of text tokens
     */
    fun generateTextStream(prompt: String): Flow<String> {
        return RunAnywhere.generateStream(prompt).catch { e ->
            Log.e(TAG, "Streaming generation failed: ${e.message}", e)
            emit("Error: ${e.message}")
        }
    }

    /**
     * Chat with the AI (convenience method for generate)
     * @param message The user message
     * @return AI response
     */
    suspend fun chat(message: String): String {
        return generateText(message)
    }

    /**
     * Check if any model is currently loaded
     * @return true if a model is loaded, false otherwise
     */
    suspend fun isModelLoaded(): Boolean {
        return try {
            val models = listAvailableModels()
            models.any { it.isLoaded }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check model status: ${e.message}", e)
            false
        }
    }

    /**
     * Get the currently loaded model info
     * @return JSON string of loaded model info or null
     */
    suspend fun getCurrentModel(): String? {
        return try {
            val models = listAvailableModels()
            val loadedModel = models.firstOrNull { it.isLoaded }

            loadedModel?.let { model ->
                JSONObject().apply {
                    put("id", model.id)
                    put("name", model.name)
                    put("type", model.type)
                    put("sizeInMB", String.format("%.2f", model.sizeBytes / (1024.0 * 1024.0)))
                }.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current model: ${e.message}", e)
            null
        }
    }
}
