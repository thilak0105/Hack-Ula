package com.mentora.mobile.utils

/**
 * Application-wide constants
 */
object Constants {

    // WebView URLs
    const val LOCAL_WEB_APP_URL = "file:///android_asset/mentora/index.html"

    // Splash screen
    const val SPLASH_DISPLAY_LENGTH = 2000L // milliseconds

    // SharedPreferences keys
    const val PREFS_NAME = "MentoraPrefs"
    const val KEY_FIRST_LAUNCH = "first_launch"

    // JavaScript Interface name
    const val JS_INTERFACE_NAME = "Android"

    // WebView settings
    const val ENABLE_JAVASCRIPT = true
    const val ENABLE_DOM_STORAGE = true

    // API endpoints (if needed in future)
    const val BASE_URL = "https://api.mentora.com/"

    // App info
    const val APP_NAME = "Mentora"
    const val SUPPORT_EMAIL = "support@mentora.com"
}
