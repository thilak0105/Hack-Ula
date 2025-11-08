package com.mentora.mobile

import android.app.Application
import android.util.Log
import android.webkit.WebView
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MentoraApplication : Application() {

    companion object {
        private const val TAG = "MentoraApp"
    }

    override fun onCreate() {
        super.onCreate()

        // Enable WebView debugging (always enabled for now)
        WebView.setWebContentsDebuggingEnabled(true)

        Log.i(TAG, "MentoraMobile Application started")
    }
}
