package com.mentora.mobile

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import com.mentora.mobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal lateinit var webView: WebView

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable WebView debugging
        WebView.setWebContentsDebuggingEnabled(true)
        Log.d(TAG, "WebView debugging enabled")

        setupWebView()
    }

    private fun setupWebView() {
        webView = binding.webView

        // Configure WebView settings
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = true
            allowContentAccess = true
        }

        // Set WebViewClient to handle navigation within the WebView
        webView.webViewClient = WebViewClient()

        // Set WebChromeClient for console logs
        webView.webChromeClient = WebChromeClient()

        // Add JavaScript interface for native-web communication
        val jsInterface = WebAppInterface(this)
        webView.addJavascriptInterface(jsInterface, "Android")
        Log.d(TAG, "JavaScript interface 'Android' added to WebView")

        // Load the local HTML file from assets
        val url = "file:///android_asset/mentora/index.html"
        Log.d(TAG, "Loading URL: $url")
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
