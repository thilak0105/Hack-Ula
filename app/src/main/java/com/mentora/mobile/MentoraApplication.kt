package com.mentora.mobile

import android.app.Application
import android.util.Log
import android.webkit.WebView

class MentoraApplication : Application() {

    companion object {
        private const val TAG = "MentoraApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Enable WebView debugging (always enabled for now)
        WebView.setWebContentsDebuggingEnabled(true)

        Log.i(TAG, "MentoraMobile Application started")
        Log.i(TAG, "RunAnywhere SDK models are available through listAvailableModels()")
    }
}
