package com.mentora.mobile

import android.app.Application
import android.util.Log
import android.webkit.WebView

class MentoraApplication : Application() {

    companion object {
        private const val TAG = "MentoraApp"

        // Demo API key for on-device AI (not used for cloud operations)
        private const val DEMO_API_KEY = "demo-on-device-key"
    }

    override fun onCreate() {
        super.onCreate()

        // Enable WebView debugging (always enabled for now)
        WebView.setWebContentsDebuggingEnabled(true)

        Log.i(TAG, "MentoraMobile Application started")
        Log.w(TAG, "⚠️ RunAnywhere SDK Note: SDK initialization may be required")
        Log.i(TAG, "If you see 'SDK not initialized' errors, the SDK needs proper setup")
        Log.i(TAG, "For now, using fallback models and best-effort AI operations")

        Log.i(TAG, "RunAnywhere SDK models are available through listAvailableModels()")
    }
}
