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

            // If RunAnywhere SDK returns empty list, provide fallback models
            if (models.isEmpty()) {
                Log.w(TAG, "listAvailableModels returned empty list, using fallback models")
                // Return hardcoded model list as fallback
                return createFallbackModels()
            }

            val jsonArray = JSONArray()

            models.forEach { model ->
                val jsonModel = JSONObject().apply {
                    put("id", model.id)
                    put("name", model.name)
                    put("category", model.category.name)
                    val sizeInMB = (model.downloadSize ?: 0L) / (1024.0 * 1024.0)
                    put("sizeInMB", String.format("%.2f", sizeInMB))
                    put("isDownloaded", model.isDownloaded)
                }
                jsonArray.put(jsonModel)
            }

            jsonArray.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get available models: ${e.message}", e)
            // Return fallback models on error
            createFallbackModels()
        }
    }

    /**
     * Create fallback model list when SDK doesn't provide models
     */
    private fun createFallbackModels(): String {
        val jsonArray = JSONArray()

        // Add TinyLlama as a known downloadable model
        val tinyLlama = JSONObject().apply {
            put("id", "tinyllama-1.1b")
            put("name", "TinyLlama 1.1B")
            put("category", "LLM")
            put("sizeInMB", "637.00")
            put("isDownloaded", false)
        }
        jsonArray.put(tinyLlama)

        Log.d(TAG, "Using fallback model list")
        return jsonArray.toString()
    }

    /**
     * Download a model with progress tracking
     * @param modelId The model ID to download
     * @return Flow of download progress (0.0 to 1.0)
     */
    fun downloadModel(modelId: String): Flow<Float> {
        return kotlinx.coroutines.flow.flow {
            try {
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    emit(progress)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}", e)
                emit(0f)
            }
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
            // Check if we can get the loaded model info
            val currentModel = getCurrentModel()
            currentModel != null
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
            // Assume the most recently used or first downloaded model is loaded
            val loadedModel = models.firstOrNull { it.isDownloaded }

            loadedModel?.let { model ->
                JSONObject().apply {
                    put("id", model.id)
                    put("name", model.name)
                    put("category", model.category.name)
                    val sizeInMB = (model.downloadSize ?: 0L) / (1024.0 * 1024.0)
                    put("sizeInMB", String.format("%.2f", sizeInMB))
                }.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current model: ${e.message}", e)
            null
        }
    }
}
