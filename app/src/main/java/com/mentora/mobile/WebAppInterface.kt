package com.mentora.mobile

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

/**
 * JavaScript Interface for communication between WebView and native Android code.
 * This allows the JavaScript code in the WebView to call native Android methods.
 */
class WebAppInterface(private val context: Context) {

    /**
     * Show a toast message from JavaScript
     * Usage in JavaScript: Android.showToast("Your message");
     */
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Get device information
     * Usage in JavaScript: var info = Android.getDeviceInfo();
     */
    @JavascriptInterface
    fun getDeviceInfo(): String {
        return android.os.Build.MODEL
    }

    /**
     * Check if running on Android
     * Usage in JavaScript: var isAndroid = Android.isAndroid();
     */
    @JavascriptInterface
    fun isAndroid(): Boolean {
        return true
    }
}
