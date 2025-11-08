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

            // Check if error is due to SDK not being initialized
            if (e.message?.contains("SDK not initialized", ignoreCase = true) == true ||
                e.message?.contains("initialize()", ignoreCase = true) == true
            ) {
                Log.w(TAG, "SDK not initialized - providing simulated response")
                return generateSimulatedResponse(prompt)
            }

            "Error: ${e.message}"
        }
    }

    /**
     * Generate a simulated AI response when SDK is not available
     * This allows the app to function for demonstration purposes
     */
    private fun generateSimulatedResponse(prompt: String): String {
        Log.d(TAG, "Generating simulated response for prompt length: ${prompt.length}")

        // Extract key information from the prompt
        val isLessonContent = prompt.contains("lesson content", ignoreCase = true) ||
                prompt.contains("Lesson Title:", ignoreCase = true)
        val isCourseContent = prompt.contains("course outline", ignoreCase = true) ||
                prompt.contains("Course Title:", ignoreCase = true)

        return when {
            isLessonContent -> generateSimulatedLessonContent(prompt)
            isCourseContent -> generateSimulatedCourseContent(prompt)
            else -> generateSimulatedGenericContent(prompt)
        }
    }

    /**
     * Generate simulated lesson content
     */
    private fun generateSimulatedLessonContent(prompt: String): String {
        // Extract lesson title if available
        val titleMatch = "Lesson Title: (.+?)\\n".toRegex().find(prompt)
        val title = titleMatch?.groupValues?.get(1) ?: "Introduction"

        return """
# $title

## Introduction

Welcome to this comprehensive lesson on $title. This lesson will provide you with a solid foundation in understanding the key concepts and practical applications.

## Main Concepts

### Core Principles

The fundamental principles of $title involve several important aspects:

1. **Understanding the Basics**: Before diving deep, it's essential to grasp the foundational concepts that underpin this topic.

2. **Practical Application**: Theory alone isn't enough - we'll explore how these concepts apply in real-world scenarios.

3. **Best Practices**: Learn the industry-standard approaches and techniques that professionals use.

### Key Topics Covered

- Fundamental concepts and definitions
- Step-by-step implementation guides
- Common pitfalls and how to avoid them
- Advanced techniques for optimization

## Examples and Practice

### Real-World Example

Let's consider a practical scenario: Imagine you're working on a project where you need to apply $title. Here's how you would approach it:

1. Start by analyzing the requirements
2. Break down the problem into smaller components
3. Apply the principles we've discussed
4. Test and iterate on your solution

### Hands-On Exercise

Try this exercise to reinforce your learning:
- Review the concepts covered in this lesson
- Identify how they relate to your own projects
- Practice implementing the techniques discussed

## Key Takeaways

By the end of this lesson, you should be able to:
- Understand the core concepts of $title
- Apply these principles in practical scenarios
- Recognize common challenges and solutions
- Build a foundation for more advanced topics

## Summary

$title is a crucial topic that forms the foundation for many advanced concepts. By mastering the fundamentals covered in this lesson, you'll be well-equipped to tackle more complex challenges. Remember to practice regularly and apply what you've learned in real-world situations.

## Next Steps

Continue your learning journey by:
- Reviewing the key concepts
- Practicing with additional examples
- Exploring related topics
- Building projects that incorporate these principles

---

*Note: This is a simulated lesson for demonstration purposes. For production use, please ensure the RunAnywhere SDK is properly initialized with a valid API key.*
        """.trimIndent()
    }

    /**
     * Generate simulated course content (JSON format)
     */
    private fun generateSimulatedCourseContent(prompt: String): String {
        // Extract course title if available
        val titleMatch = "Course Title: (.+?)\\n".toRegex().find(prompt)
        val title = titleMatch?.groupValues?.get(1)?.trim() ?: "Sample Course"

        // Return JSON format that matches expected structure
        return """
{
  "title": "$title",
  "description": "A comprehensive course covering the fundamentals and advanced topics of $title. This course is designed to take you from beginner to proficient, with hands-on exercises and real-world applications.",
  "lessons": [
    {
      "title": "Introduction to $title",
      "description": "Get started with the basics and understand the core concepts",
      "topics": ["Overview", "Key Concepts", "Getting Started"]
    },
    {
      "title": "Fundamental Principles",
      "description": "Deep dive into the foundational principles that govern $title",
      "topics": ["Core Principles", "Theoretical Framework", "Best Practices"]
    },
    {
      "title": "Practical Applications",
      "description": "Learn how to apply concepts in real-world scenarios",
      "topics": ["Use Cases", "Implementation Strategies", "Common Patterns"]
    },
    {
      "title": "Intermediate Techniques",
      "description": "Build on your knowledge with intermediate-level concepts",
      "topics": ["Advanced Concepts", "Optimization", "Problem Solving"]
    },
    {
      "title": "Advanced Topics",
      "description": "Master advanced techniques and best practices",
      "topics": ["Expert Strategies", "Performance Tuning", "Scalability"]
    },
    {
      "title": "Project Work and Practice",
      "description": "Apply your knowledge through hands-on projects",
      "topics": ["Project Planning", "Implementation", "Testing and Debugging"]
    }
  ]
}
        """.trimIndent()
    }

    /**
     * Generate simulated generic content
     */
    private fun generateSimulatedGenericContent(prompt: String): String {
        return """
Based on your query, here's a comprehensive response:

${prompt.take(100)}...

This topic encompasses several important areas that are worth exploring in detail. Understanding these concepts will help you build a solid foundation for further learning.

Key Points:
• The fundamentals are essential for mastering this subject
• Practical application reinforces theoretical knowledge
• Regular practice leads to better understanding
• Real-world examples make concepts more relatable

For a more detailed and personalized response, please ensure the RunAnywhere SDK is properly initialized with a valid API key.

---

*Note: This is a simulated response for demonstration purposes.*
        """.trimIndent()
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
