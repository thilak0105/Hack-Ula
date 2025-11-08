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

                // Step 2: Check if model is downloaded, if not download it
                Log.d(TAG, "Checking for downloaded models...")
                val modelsJson = withContext(Dispatchers.IO) {
                    aiManager.getAvailableModels()
                }

                val models = JSONArray(modelsJson)
                var downloadedModelId: String? = null
                var needsDownload = false
                var modelToDownload: String? = null

                // Find first downloaded model or identify one to download
                for (i in 0 until models.length()) {
                    val model = models.getJSONObject(i)
                    if (model.getBoolean("isDownloaded")) {
                        downloadedModelId = model.getString("id")
                        break
                    } else if (modelToDownload == null) {
                        modelToDownload = model.getString("id")
                    }
                }

                // If no model downloaded, download one
                if (downloadedModelId == null && modelToDownload != null) {
                    Log.d(TAG, "No model downloaded. Auto-downloading: $modelToDownload")
                    needsDownload = true

                    // Notify user that download is starting
                    showToast("Downloading AI model (first time only)...")

                    // Download the model
                    withContext(Dispatchers.IO) {
                        aiManager.downloadModel(modelToDownload).collect { progress ->
                            val percentage = (progress * 100).toInt()
                            Log.d(TAG, "Model download progress: $percentage%")
                            if (percentage % 20 == 0) { // Show toast every 20%
                                withContext(Dispatchers.Main) {
                                    showToast("Downloading model: $percentage%")
                                }
                            }
                        }
                    }
                    downloadedModelId = modelToDownload
                    showToast("Model downloaded successfully!")
                    Log.d(TAG, "Model downloaded: $modelToDownload")
                }

                // Step 3: Check if model is loaded, if not load one
                Log.d(TAG, "Checking if AI model is loaded...")
                val modelLoaded = withContext(Dispatchers.IO) {
                    aiManager.isModelLoaded()
                }

                if (!modelLoaded) {
                    if (downloadedModelId != null) {
                        Log.d(TAG, "Loading model: $downloadedModelId")
                        val loadSuccess = withContext(Dispatchers.IO) {
                            aiManager.loadModel(downloadedModelId)
                        }

                        if (!loadSuccess) {
                            throw Exception("Failed to load model: $downloadedModelId")
                        }
                        Log.d(TAG, "Model loaded successfully!")
                    } else {
                        throw Exception("No models available. Please check your internet connection and try again.")
                    }
                }

                // Step 4: Create prompt for AI
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

                // Step 5: Generate course using AI
                val aiResponse = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                Log.d(TAG, "AI Response received: ${aiResponse.take(200)}...")

                // Step 6: Parse AI response and format for frontend
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

                // Step 7: Create response
                val response = JSONObject().apply {
                    put("success", true)
                    put("course_id", "ai-${System.currentTimeMillis()}")
                    put("extracted_text", contentText)
                    put("course", courseData)
                    put(
                        "message",
                        if (needsDownload) "Course generated successfully! Model was downloaded." else "Course generated successfully using on-device AI"
                    )
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("if(typeof window.$callback === 'function') { window.$callback(JSON.parse('$jsonString')); }")
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
                    executeJavaScript("if(typeof window.$callback === 'function') { window.$callback(JSON.parse('$jsonString')); }")
                }
            }
        }
    }

    /**
     * Generate detailed lesson content for a specific lesson
     * Called when user clicks "Read" button on a lesson
     */
    @JavascriptInterface
    fun generateLessonContent(
        lessonTitle: String,
        lessonDescription: String,
        courseContext: String,
        callback: String
    ) {
        Log.d(TAG, "Generating lesson content for: $lessonTitle")

        scope.launch {
            try {
                // Step 1: Check for downloaded models
                Log.d(TAG, "Checking for downloaded models...")
                val modelsJson = withContext(Dispatchers.IO) {
                    aiManager.getAvailableModels()
                }

                val models = JSONArray(modelsJson)
                var downloadedModelId: String? = null
                var modelToDownload: String? = null

                // Find first downloaded model or identify one to download
                for (i in 0 until models.length()) {
                    val model = models.getJSONObject(i)
                    if (model.getBoolean("isDownloaded")) {
                        downloadedModelId = model.getString("id")
                        Log.d(TAG, "Found downloaded model: $downloadedModelId")
                        break
                    } else if (modelToDownload == null) {
                        modelToDownload = model.getString("id")
                    }
                }

                // If no model downloaded, download one first
                if (downloadedModelId == null) {
                    if (modelToDownload != null) {
                        Log.d(TAG, "No model downloaded. Auto-downloading: $modelToDownload")
                        showToast("Downloading AI model... This may take a few minutes.")

                        // Download the model
                        withContext(Dispatchers.IO) {
                            aiManager.downloadModel(modelToDownload).collect { progress ->
                                val percentage = (progress * 100).toInt()
                                Log.d(TAG, "Model download progress: $percentage%")
                                if (percentage % 25 == 0) { // Show toast every 25%
                                    withContext(Dispatchers.Main) {
                                        showToast("Downloading model: $percentage%")
                                    }
                                }
                            }
                        }
                        downloadedModelId = modelToDownload
                        showToast("Model downloaded! Generating lesson content...")
                        Log.d(TAG, "Model downloaded: $modelToDownload")
                    } else {
                        throw Exception("No AI models available. Please check your internet connection.")
                    }
                }

                // Step 2: Load the model if not already loaded
                Log.d(TAG, "Ensuring model is loaded: $downloadedModelId")
                withContext(Dispatchers.IO) {
                    try {
                        val loadSuccess = aiManager.loadModel(downloadedModelId)
                        if (loadSuccess) {
                            Log.d(TAG, "Model loaded successfully")
                        } else {
                            Log.w(TAG, "Model load returned false, but continuing anyway")
                        }
                    } catch (e: Exception) {
                        Log.w(
                            TAG,
                            "Model load threw exception (may already be loaded): ${e.message}"
                        )
                        // Continue anyway - model might already be loaded
                    }
                }

                // Step 3: Create prompt for lesson content
                val prompt = buildString {
                    append("Generate detailed lesson content for:\n\n")
                    append("Lesson Title: $lessonTitle\n")
                    if (lessonDescription.isNotEmpty() && lessonDescription != "null") {
                        append("Lesson Description: $lessonDescription\n")
                    }
                    if (courseContext.isNotEmpty() && courseContext != "null") {
                        append("Course Context: $courseContext\n\n")
                    }
                    append("\nCreate comprehensive lesson content with:\n")
                    append("1. Introduction (2-3 paragraphs)\n")
                    append("2. Main concepts (detailed explanation)\n")
                    append("3. Examples and practical applications\n")
                    append("4. Key takeaways\n")
                    append("5. Summary\n\n")
                    append("Write in a clear, educational style suitable for learning.\n")
                    append("Format the content with clear sections and paragraphs.")
                }

                Log.d(TAG, "Generating lesson content with AI...")
                Log.d(TAG, "Prompt length: ${prompt.length} characters")

                // Step 4: Generate content
                val lessonContent = withContext(Dispatchers.IO) {
                    try {
                        aiManager.generateText(prompt)
                    } catch (e: Exception) {
                        Log.e(TAG, "Generation error: ${e.message}", e)
                        throw Exception("AI generation failed: ${e.message}. Please try again.")
                    }
                }

                Log.d(TAG, "Lesson content generated: ${lessonContent.length} characters")

                // Step 5: Create response
                val response = JSONObject().apply {
                    put("success", true)
                    put("lessonTitle", lessonTitle)
                    put("content", lessonContent)
                    put("message", "Lesson content generated successfully")
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                    executeJavaScript("if(typeof window.$callback === 'function') { window.$callback(JSON.parse('$jsonString')); }")
                }

                Log.d(TAG, "Lesson content delivered successfully")

            } catch (e: Exception) {
                Log.e(TAG, "Error generating lesson content", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Failed to generate lesson content")
                    put("lessonTitle", lessonTitle)
                    put("message", "Error: ${e.message}")
                }

                withContext(Dispatchers.Main) {
                    val jsonString = errorResponse.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("if(typeof window.$callback === 'function') { window.$callback(JSON.parse('$jsonString')); }")
                }
            }
        }
    }

    // ========== NEW AI FEATURES ==========

    /**
     * Translate text from one language to another
     * @param text Text to translate
     * @param targetLanguage Target language (e.g., "Spanish", "French", "German")
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun translateText(text: String, targetLanguage: String, callback: String) {
        Log.d(TAG, "Translating text to $targetLanguage...")
        scope.launch {
            try {
                val prompt = """
                    Translate the following text to $targetLanguage. 
                    Only provide the translation, no explanations:
                    
                    $text
                """.trimIndent()

                val translation = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("originalText", text)
                    put("translatedText", translation)
                    put("targetLanguage", targetLanguage)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Translation failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Translation failed")
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
     * Generate a summary of long text content
     * @param text Text to summarize
     * @param maxLength Maximum summary length (in words)
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun summarizeText(text: String, maxLength: Int, callback: String) {
        Log.d(TAG, "Summarizing text (max $maxLength words)...")
        scope.launch {
            try {
                val prompt = """
                    Summarize the following text in approximately $maxLength words.
                    Keep the most important information and key points:
                    
                    $text
                """.trimIndent()

                val summary = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("originalText", text)
                    put("summary", summary)
                    put("summaryLength", summary.split(" ").size)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Summarization failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Summarization failed")
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
     * Generate quiz questions from lesson content
     * @param lessonContent The lesson content to generate quiz from
     * @param numQuestions Number of questions to generate
     * @param difficulty Quiz difficulty (easy, medium, hard)
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun generateQuiz(
        lessonContent: String,
        numQuestions: Int,
        difficulty: String,
        callback: String
    ) {
        Log.d(TAG, "Generating $numQuestions quiz questions ($difficulty difficulty)...")
        scope.launch {
            try {
                val prompt = """
                    Generate $numQuestions multiple choice quiz questions based on this lesson content.
                    Difficulty level: $difficulty
                    
                    Lesson Content:
                    ${lessonContent.take(2000)}
                    
                    Format your response as a JSON array with this structure:
                    [
                      {
                        "question": "Question text here",
                        "options": ["Option A", "Option B", "Option C", "Option D"],
                        "correctAnswer": 0,
                        "explanation": "Brief explanation of the correct answer"
                      }
                    ]
                    
                    Only return the JSON array, no additional text.
                """.trimIndent()

                val quizJson = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                // Try to parse JSON from AI response
                val jsonStart = quizJson.indexOf("[")
                val jsonEnd = quizJson.lastIndexOf("]") + 1
                val cleanJson = if (jsonStart != -1 && jsonEnd > jsonStart) {
                    quizJson.substring(jsonStart, jsonEnd)
                } else {
                    quizJson
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("quiz", JSONArray(cleanJson))
                    put("difficulty", difficulty)
                    put("numQuestions", numQuestions)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Quiz generation failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Quiz generation failed")
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
     * Smart chat assistant for answering student questions
     * @param question Student's question
     * @param context Optional context (e.g., current lesson/course)
     * @param conversationHistory Optional previous messages for context
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun chatWithAI(
        question: String,
        context: String,
        conversationHistory: String,
        callback: String
    ) {
        Log.d(TAG, "Processing chat question: ${question.take(50)}...")
        scope.launch {
            try {
                val prompt = buildString {
                    append("You are a helpful educational assistant. Answer the student's question clearly and accurately.\n\n")

                    if (context.isNotEmpty() && context != "null") {
                        append("Context: $context\n\n")
                    }

                    if (conversationHistory.isNotEmpty() && conversationHistory != "null") {
                        append("Previous conversation:\n$conversationHistory\n\n")
                    }

                    append("Student's question: $question\n\n")
                    append("Provide a clear, educational response:")
                }

                val answer = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("question", question)
                    put("answer", answer)
                    put("timestamp", System.currentTimeMillis())
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Chat failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Chat failed")
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
     * Generate flashcards from lesson content
     * @param lessonContent The lesson content to generate flashcards from
     * @param numCards Number of flashcards to generate
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun generateFlashcards(lessonContent: String, numCards: Int, callback: String) {
        Log.d(TAG, "Generating $numCards flashcards...")
        scope.launch {
            try {
                val prompt = """
                    Generate $numCards educational flashcards from this lesson content.
                    
                    Lesson Content:
                    ${lessonContent.take(2000)}
                    
                    Format your response as a JSON array:
                    [
                      {
                        "front": "Question or concept",
                        "back": "Answer or explanation"
                      }
                    ]
                    
                    Only return the JSON array, no additional text.
                """.trimIndent()

                val flashcardsJson = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                // Try to parse JSON from AI response
                val jsonStart = flashcardsJson.indexOf("[")
                val jsonEnd = flashcardsJson.lastIndexOf("]") + 1
                val cleanJson = if (jsonStart != -1 && jsonEnd > jsonStart) {
                    flashcardsJson.substring(jsonStart, jsonEnd)
                } else {
                    flashcardsJson
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("flashcards", JSONArray(cleanJson))
                    put("numCards", numCards)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Flashcard generation failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Flashcard generation failed")
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
     * Explain complex concepts in simpler terms
     * @param concept The complex concept to explain
     * @param targetAge Target age/level (e.g., "10 year old", "beginner")
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun simplifyExplanation(concept: String, targetAge: String, callback: String) {
        Log.d(TAG, "Simplifying explanation for $targetAge...")
        scope.launch {
            try {
                val prompt = """
                    Explain the following concept as if you're teaching it to a $targetAge.
                    Use simple language, analogies, and examples:
                    
                    Concept: $concept
                    
                    Provide a clear, simple explanation:
                """.trimIndent()

                val explanation = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("originalConcept", concept)
                    put("simplifiedExplanation", explanation)
                    put("targetAge", targetAge)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Simplification failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Simplification failed")
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
     * Generate study notes from lesson content
     * @param lessonContent The lesson content to create notes from
     * @param format Note format (bullet, outline, summary)
     * @param callback JavaScript callback function name
     */
    @JavascriptInterface
    fun generateStudyNotes(lessonContent: String, format: String, callback: String) {
        Log.d(TAG, "Generating study notes in $format format...")
        scope.launch {
            try {
                val formatInstruction = when (format) {
                    "bullet" -> "Format as concise bullet points highlighting key information"
                    "outline" -> "Format as a hierarchical outline with main topics and subtopics"
                    "summary" -> "Format as a comprehensive summary with paragraphs"
                    else -> "Format as organized study notes"
                }

                val prompt = """
                    Create study notes from this lesson content.
                    $formatInstruction
                    
                    Lesson Content:
                    ${lessonContent.take(2000)}
                    
                    Generate clear, organized study notes:
                """.trimIndent()

                val notes = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }

                val response = JSONObject().apply {
                    put("success", true)
                    put("notes", notes)
                    put("format", format)
                }

                withContext(Dispatchers.Main) {
                    val jsonString = response.toString()
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                    executeJavaScript("window.$callback(JSON.parse('$jsonString'))")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Note generation failed: ${e.message}", e)
                val errorResponse = JSONObject().apply {
                    put("success", false)
                    put("error", e.message ?: "Note generation failed")
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
     * Get AI status including RunAnywhere SDK availability
     * @return JSON string with AI status information
     */
    @JavascriptInterface
    fun getRunAnywhereAIStatus(): String {
        return try {
            val status = JSONObject().apply {
                put("installed", true) // SDK is integrated
                put("available", true)
                put("features", JSONArray().apply {
                    put("Course Generation")
                    put("Lesson Content")
                    put("Translation")
                    put("Summarization")
                    put("Quiz Generation")
                    put("Chat Assistant")
                    put("Flashcards")
                    put("Study Notes")
                })
            }
            status.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting AI status: ${e.message}", e)
            JSONObject().apply {
                put("installed", false)
                put("error", e.message)
            }.toString()
        }
    }

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
