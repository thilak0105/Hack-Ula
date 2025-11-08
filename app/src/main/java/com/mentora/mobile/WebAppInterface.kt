package com.mentora.mobile

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.util.Log
import android.widget.Toast
import com.mentora.mobile.ai.AIManager
import com.mentora.mobile.utils.NetworkUtil
import com.mentora.mobile.utils.PreferenceManager
import org.json.JSONObject
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.URL
import org.jsoup.Jsoup

class WebAppInterface(private val context: Context) {

    private val aiManager = AIManager(context)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val preferenceManager = PreferenceManager(context)

    companion object {
        private const val TAG = "WebAppInterface"
    }

    // ========== Basic Native Features ==========

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun getDeviceInfo(): String {
        return """
            {
                "manufacturer": "${android.os.Build.MANUFACTURER}",
                "model": "${android.os.Build.MODEL}",
                "version": "${android.os.Build.VERSION.RELEASE}",
                "sdkInt": ${android.os.Build.VERSION.SDK_INT}
            }
        """.trimIndent()
    }

    @JavascriptInterface
    fun isAndroid(): Boolean = true

    @JavascriptInterface
    fun checkNetworkConnection(): Boolean {
        return NetworkUtil.isNetworkAvailable(context)
    }

    // ========== Preference Storage ==========

    @JavascriptInterface
    fun saveString(key: String, value: String) {
        preferenceManager.saveString(key, value)
    }

    @JavascriptInterface
    fun getString(key: String, defaultValue: String): String {
        return preferenceManager.getString(key, defaultValue)
    }

    @JavascriptInterface
    fun saveBoolean(key: String, value: Boolean) {
        preferenceManager.saveBoolean(key, value)
    }

    @JavascriptInterface
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferenceManager.getBoolean(key, defaultValue)
    }

    // ========== AI Features ==========

    /**
     * Get list of available AI models
     * Callback receives JSON string of models array
     */
    @JavascriptInterface
    fun getAvailableModels(callback: String) {
        Log.d(TAG, "Getting available models...")
        scope.launch {
            try {
                val modelsJson = withContext(Dispatchers.IO) {
                    aiManager.getAvailableModels()
                }
                Log.d(TAG, "Models JSON: $modelsJson")
                // Pass the JSON as a parameter to the callback function
                executeJavaScript("window.$callback($modelsJson)")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get models: ${e.message}", e)
                executeJavaScript("window.$callback([])")
            }
        }
    }

    /**
     * Download a model
     * @param modelId The model ID to download
     * @param progressCallback JavaScript function name to call with progress (0-100)
     * @param completeCallback JavaScript function name to call when complete
     */
    @JavascriptInterface
    fun downloadModel(modelId: String, progressCallback: String, completeCallback: String) {
        Log.d(TAG, "Downloading model: $modelId")
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    aiManager.downloadModel(modelId).collect { progress ->
                        val percentage = (progress * 100).toInt()
                        withContext(Dispatchers.Main) {
                            executeJavaScript("window.$progressCallback($percentage)")
                        }
                    }
                }
                executeJavaScript("window.$completeCallback(true)")
            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}", e)
                executeJavaScript("window.$completeCallback(false)")
            }
        }
    }

    /**
     * Load a model for inference
     * @param modelId The model ID to load
     * @param callback JavaScript function name to call with success boolean
     */
    @JavascriptInterface
    fun loadModel(modelId: String, callback: String) {
        Log.d(TAG, "Loading model: $modelId")
        scope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    aiManager.loadModel(modelId)
                }
                executeJavaScript("window.$callback($success)")
            } catch (e: Exception) {
                Log.e(TAG, "Load failed: ${e.message}", e)
                executeJavaScript("window.$callback(false)")
            }
        }
    }

    /**
     * Unload the currently loaded model
     * @param callback JavaScript function name to call with success boolean
     */
    @JavascriptInterface
    fun unloadModel(callback: String) {
        Log.d(TAG, "Unloading model")
        scope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    aiManager.unloadModel()
                }
                executeJavaScript("window.$callback($success)")
            } catch (e: Exception) {
                Log.e(TAG, "Unload failed: ${e.message}", e)
                executeJavaScript("window.$callback(false)")
            }
        }
    }

    /**
     * Generate text (non-streaming)
     * @param prompt The user prompt
     * @param callback JavaScript function name to call with response
     */
    @JavascriptInterface
    fun generateText(prompt: String, callback: String) {
        Log.d(TAG, "Generating text...")
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }
                // Escape the response properly for JavaScript string
                val escapedResponse = response
                    .replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\"", "\\\"")
                executeJavaScript("window.$callback('$escapedResponse')")
            } catch (e: Exception) {
                Log.e(TAG, "Generation failed: ${e.message}", e)
                val errorMsg = (e.message ?: "Generation failed").replace("'", "\\'")
                executeJavaScript("window.$callback('Error: $errorMsg')")
            }
        }
    }

    /**
     * Generate text with streaming (real-time tokens)
     * @param prompt The user prompt
     * @param tokenCallback JavaScript function name to call for each token
     * @param completeCallback JavaScript function name to call when complete
     */
    @JavascriptInterface
    fun generateTextStream(prompt: String, tokenCallback: String, completeCallback: String) {
        Log.d(TAG, "Generating text stream...")
        scope.launch {
            try {
                var fullResponse = ""
                withContext(Dispatchers.IO) {
                    aiManager.generateTextStream(prompt).collect { token ->
                        fullResponse += token
                        val escapedToken = token.replace("'", "\\'").replace("\n", "\\n")
                        withContext(Dispatchers.Main) {
                            executeJavaScript("window.$tokenCallback('$escapedToken')")
                        }
                    }
                }
                executeJavaScript("window.$completeCallback(true)")
            } catch (e: Exception) {
                Log.e(TAG, "Streaming failed: ${e.message}", e)
                executeJavaScript("window.$completeCallback(false)")
            }
        }
    }

    /**
     * Chat with AI (convenience method)
     * @param message The user message
     * @param callback JavaScript function name to call with response
     */
    @JavascriptInterface
    fun chat(message: String, callback: String) {
        generateText(message, callback)
    }

    /**
     * Check if a model is currently loaded
     * @param callback JavaScript function name to call with boolean result
     */
    @JavascriptInterface
    fun isModelLoaded(callback: String) {
        Log.d(TAG, "Checking if model is loaded...")
        scope.launch {
            try {
                val isLoaded = withContext(Dispatchers.IO) {
                    aiManager.isModelLoaded()
                }
                executeJavaScript("window.$callback($isLoaded)")
            } catch (e: Exception) {
                Log.e(TAG, "Check failed: ${e.message}", e)
                executeJavaScript("window.$callback(false)")
            }
        }
    }

    /**
     * Get currently loaded model info
     * @param callback JavaScript function name to call with model JSON or null
     */
    @JavascriptInterface
    fun getCurrentModel(callback: String) {
        Log.d(TAG, "Getting current model...")
        scope.launch {
            try {
                val modelJson = withContext(Dispatchers.IO) {
                    aiManager.getCurrentModel()
                }
                if (modelJson != null) {
                    executeJavaScript("window.$callback($modelJson)")
                } else {
                    executeJavaScript("window.$callback(null)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get current model failed: ${e.message}", e)
                executeJavaScript("window.$callback(null)")
            }
        }
    }

    /**
     * Fetch and extract text from a website URL
     * @param url Website URL to extract content from
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun extractWebsiteContent(url: String, callback: String) {
        Log.d(TAG, "Extracting content from URL: $url")

        scope.launch(Dispatchers.IO) {
            try {
                // Fetch the website content
                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
                    .timeout(10000)
                    .get()

                // Extract text content
                val title = doc.title()
                val bodyText = doc.body().text()

                // Create response
                val response = JSONObject().apply {
                    put("success", true)
                    put("url", url)
                    put("title", title)
                    put("text", bodyText)
                    put("length", bodyText.length)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString().replace("\\", "\\\\").replace("'", "\\'")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error extracting website content", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Failed to extract content")
                }

                withContext(Dispatchers.Main) {
                    val jsonString =
                        errorResponse.toString().replace("\\", "\\\\").replace("'", "\\'")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            }
        }
    }

    /**
     * Extract text from uploaded file (PDF, TXT, etc.)
     * @param fileUri File URI to extract content from
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun extractFileContent(fileUri: String, callback: String) {
        Log.d(TAG, "Extracting content from file: $fileUri")

        scope.launch(Dispatchers.IO) {
            try {
                // This would need PDF parsing library for PDFs
                // For now, return a placeholder
                val response = JSONObject().apply {
                    put("success", true)
                    put("fileUri", fileUri)
                    put("text", "File extraction not yet implemented. Use website URL for now.")
                    put("message", "PDF extraction requires additional library")
                }

                withContext(Dispatchers.Main) {
                    executeJavaScript("$callback(${response.toString()})")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error extracting file content", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Failed to extract file content")
                }

                withContext(Dispatchers.Main) {
                    executeJavaScript("$callback(${errorResponse.toString()})")
                }
            }
        }
    }

    /**
     * Generate course content using AI from extracted website content
     * This method handles the complete workflow: extract content -> load model -> generate course
     * @param url Website URL to extract content from (optional if extractedText provided)
     * @param extractedText Pre-extracted text content (optional if url provided)
     * @param courseTitle Title for the course
     * @param difficulty Course difficulty level
     * @param audience Target audience
     * @param prerequisites Course prerequisites
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun generateCourseContent(
        url: String,
        extractedText: String,
        courseTitle: String,
        difficulty: String,
        audience: String,
        prerequisites: String,
        callback: String
    ) {
        Log.d(TAG, "Starting course generation workflow...")
        Log.d(TAG, "URL: $url, Title: $courseTitle, Difficulty: $difficulty")

        scope.launch {
            try {
                // Step 1: Get content (either from URL or use provided text)
                var contentText = extractedText
                var contentTitle = courseTitle

                if (url.isNotEmpty() && url != "null") {
                    Log.d(TAG, "Extracting content from URL: $url")
                    val extractResult = withContext(Dispatchers.IO) {
                        try {
                            val doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
                                .timeout(10000)
                                .get()

                            val title = doc.title()
                            val bodyText = doc.body().text()
                            Pair(title, bodyText)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error extracting website: ${e.message}")
                            Pair(courseTitle, "")
                        }
                    }
                    contentTitle = extractResult.first
                    contentText = extractResult.second
                }

                // Step 2: Check if model is loaded, if not load one
                Log.d(TAG, "Checking if AI model is loaded...")
                val modelLoaded = withContext(Dispatchers.IO) {
                    aiManager.isModelLoaded()
                }

                if (!modelLoaded) {
                    Log.d(TAG, "No model loaded, loading default model...")
                    // Try to load the first available downloaded model
                    val modelsJson = withContext(Dispatchers.IO) {
                        aiManager.getAvailableModels()
                    }

                    val models = JSONObject("{\"models\": $modelsJson}").getJSONArray("models")
                    var modelId: String? = null

                    // Find first downloaded model
                    for (i in 0 until models.length()) {
                        val model = models.getJSONObject(i)
                        if (model.getBoolean("isDownloaded")) {
                            modelId = model.getString("id")
                            break
                        }
                    }

                    if (modelId != null) {
                        Log.d(TAG, "Loading model: $modelId")
                        val loadSuccess = withContext(Dispatchers.IO) {
                            aiManager.loadModel(modelId)
                        }

                        if (!loadSuccess) {
                            throw Exception("Failed to load model: $modelId")
                        }
                        Log.d(TAG, "Model loaded successfully!")
                    } else {
                        throw Exception("No downloaded models available. Please download a model first from Settings.")
                    }
                }

                // Step 3: Create prompt for AI
                val truncatedContent = if (contentText.length > 2000) {
                    contentText.substring(0, 2000) + "..."
                } else {
                    contentText
                }

                val prompt = buildString {
                    append("Create a comprehensive course outline based on the following information:\n\n")
                    append("Course Title: ${courseTitle.ifEmpty { contentTitle }}\n")
                    append("Difficulty Level: $difficulty\n")
                    append("Target Audience: $audience\n")
                    append("Prerequisites: $prerequisites\n\n")

                    if (contentText.isNotEmpty()) {
                        append("Source Content:\n$truncatedContent\n\n")
                    }

                    append("Generate a structured course with:\n")
                    append("1. An engaging title\n")
                    append("2. A comprehensive description\n")
                    append("3. 5-7 lessons, each containing:\n")
                    append("   - Lesson title\n")
                    append("   - Lesson description\n")
                    append("   - 3-5 topics to cover\n\n")
                    append("Format your response as a valid JSON object with this structure:\n")
                    append("{\n")
                    append("  \"title\": \"Course Title\",\n")
                    append("  \"description\": \"Course Description\",\n")
                    append("  \"lessons\": [\n")
                    append("    {\n")
                    append("      \"title\": \"Lesson Title\",\n")
                    append("      \"description\": \"Lesson Description\",\n")
                    append("      \"topics\": [\"Topic 1\", \"Topic 2\", \"Topic 3\"]\n")
                    append("    }\n")
                    append("  ]\n")
                    append("}\n\n")
                    append("Only return the JSON, no additional text.")
                }

                Log.d(TAG, "Generating course with AI...")
                Log.d(TAG, "Prompt length: ${prompt.length} characters")

                // Step 4: Generate course using AI
                val aiResponse = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                Log.d(TAG, "AI Response received: ${aiResponse.take(200)}...")

                // Step 5: Parse AI response and format for frontend
                val courseData = try {
                    // Try to find JSON in the response
                    val jsonStart = aiResponse.indexOf("{")
                    val jsonEnd = aiResponse.lastIndexOf("}") + 1

                    if (jsonStart != -1 && jsonEnd > jsonStart) {
                        val jsonStr = aiResponse.substring(jsonStart, jsonEnd)
                        JSONObject(jsonStr)
                    } else {
                        throw Exception("No JSON found in AI response")
                    }
                } catch (e: Exception) {
                    Log.w(
                        TAG,
                        "AI didn't return valid JSON, creating structured response: ${e.message}"
                    )

                    // Create a basic course structure from AI response
                    JSONObject().apply {
                        put("title", courseTitle.ifEmpty { contentTitle })
                        put("description", aiResponse.take(200))
                        put("lessons", JSONArray().apply {
                            // Create lessons from AI response by splitting into sections
                            val sections = aiResponse.split("\n\n").filter { it.length > 50 }
                            sections.take(6).forEachIndexed { index, section ->
                                put(JSONObject().apply {
                                    put("title", "Lesson ${index + 1}: ${section.take(50)}")
                                    put("description", section.take(150))
                                    put("topics", JSONArray().apply {
                                        put("Introduction")
                                        put("Core Concepts")
                                        put("Practice")
                                    })
                                })
                            }
                        })
                    }
                }

                // Step 6: Create response
                val response = JSONObject().apply {
                    put("success", true)
                    put("course_id", "ai-${System.currentTimeMillis()}")
                    put("extracted_text", contentText)
                    put("course", courseData)
                    put("message", "Course generated successfully using on-device AI")
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }

                Log.d(TAG, "Course generation completed successfully!")

            } catch (e: Exception) {
                Log.e(TAG, "Error generating course content", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Failed to generate course content")
                    put("message", "Error: ${e.message}")
                }

                withContext(Dispatchers.Main) {
                    val jsonString = errorResponse.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            }
        }
    }

    // ========== Helper Methods ==========

    /**
     * Execute JavaScript code in the WebView
     * Must be called from main thread
     */
    private fun executeJavaScript(script: String) {
        if (context is MainActivity) {
            context.runOnUiThread {
                context.webView.evaluateJavascript(script, null)
            }
        }
    }
}
