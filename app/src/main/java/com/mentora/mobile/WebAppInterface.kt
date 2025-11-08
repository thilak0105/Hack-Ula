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
        scope.launch {
            try {
                val modelsJson = withContext(Dispatchers.IO) {
                    aiManager.getAvailableModels()
                }
                executeJavaScript("$callback('$modelsJson')")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get models: ${e.message}", e)
                executeJavaScript("$callback('[]')")
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
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    aiManager.downloadModel(modelId).collect { progress ->
                        val percentage = (progress * 100).toInt()
                        withContext(Dispatchers.Main) {
                            executeJavaScript("$progressCallback($percentage)")
                        }
                    }
                }
                executeJavaScript("$completeCallback(true)")
            } catch (e: Exception) {
                Log.e(TAG, "Download failed: ${e.message}", e)
                executeJavaScript("$completeCallback(false)")
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
        scope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    aiManager.loadModel(modelId)
                }
                executeJavaScript("$callback($success)")
            } catch (e: Exception) {
                Log.e(TAG, "Load failed: ${e.message}", e)
                executeJavaScript("$callback(false)")
            }
        }
    }

    /**
     * Unload the currently loaded model
     * @param callback JavaScript function name to call with success boolean
     */
    @JavascriptInterface
    fun unloadModel(callback: String) {
        scope.launch {
            try {
                val success = withContext(Dispatchers.IO) {
                    aiManager.unloadModel()
                }
                executeJavaScript("$callback($success)")
            } catch (e: Exception) {
                Log.e(TAG, "Unload failed: ${e.message}", e)
                executeJavaScript("$callback(false)")
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
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    aiManager.generateText(prompt)
                }
                val escapedResponse = response.replace("'", "\\'").replace("\n", "\\n")
                executeJavaScript("$callback('$escapedResponse')")
            } catch (e: Exception) {
                Log.e(TAG, "Generation failed: ${e.message}", e)
                executeJavaScript("$callback('Error: ${e.message}')")
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
        scope.launch {
            try {
                var fullResponse = ""
                withContext(Dispatchers.IO) {
                    aiManager.generateTextStream(prompt).collect { token ->
                        fullResponse += token
                        val escapedToken = token.replace("'", "\\'").replace("\n", "\\n")
                        withContext(Dispatchers.Main) {
                            executeJavaScript("$tokenCallback('$escapedToken')")
                        }
                    }
                }
                executeJavaScript("$completeCallback(true)")
            } catch (e: Exception) {
                Log.e(TAG, "Streaming failed: ${e.message}", e)
                executeJavaScript("$completeCallback(false)")
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
        scope.launch {
            try {
                val isLoaded = withContext(Dispatchers.IO) {
                    aiManager.isModelLoaded()
                }
                executeJavaScript("$callback($isLoaded)")
            } catch (e: Exception) {
                Log.e(TAG, "Check failed: ${e.message}", e)
                executeJavaScript("$callback(false)")
            }
        }
    }

    /**
     * Get currently loaded model info
     * @param callback JavaScript function name to call with model JSON or null
     */
    @JavascriptInterface
    fun getCurrentModel(callback: String) {
        scope.launch {
            try {
                val modelJson = withContext(Dispatchers.IO) {
                    aiManager.getCurrentModel()
                }
                if (modelJson != null) {
                    executeJavaScript("$callback('$modelJson')")
                } else {
                    executeJavaScript("$callback(null)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get current model failed: ${e.message}", e)
                executeJavaScript("$callback(null)")
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
