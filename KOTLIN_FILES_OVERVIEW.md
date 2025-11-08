# Kotlin Files Overview - MentoraMobile

This document provides a comprehensive overview of all Kotlin code files in the MentoraMobile
Android project.

## 📁 Project Structure

```
MentoraMobile/
├── app/
│   ├── src/main/java/com/mentora/mobile/
│   │   ├── MainActivity.kt
│   │   ├── SplashActivity.kt
│   │   ├── MentoraApplication.kt
│   │   ├── WebAppInterface.kt
│   │   └── utils/
│   │       ├── Constants.kt
│   │       ├── NetworkUtil.kt
│   │       └── PreferenceManager.kt
```

---

## 🎯 Core Kotlin Files

### 1. MainActivity.kt

**Location:** `app/src/main/java/com/mentora/mobile/MainActivity.kt`

**Purpose:** Main activity that hosts the WebView to display the React web application.

**Key Features:**

- Sets up and configures WebView with optimal settings
- Enables JavaScript execution and DOM storage
- Implements JavaScript bridge via `WebAppInterface`
- Handles back button navigation within WebView
- Loads local React app from assets folder

**Key Methods:**

- `onCreate()`: Initializes the activity and sets up WebView
- `setupWebView()`: Configures WebView settings and loads content
- `onBackPressed()`: Handles back navigation
- `onDestroy()`: Properly destroys WebView to prevent memory leaks

---

### 2. SplashActivity.kt

**Location:** `app/src/main/java/com/mentora/mobile/SplashActivity.kt`

**Purpose:** Displays a splash screen when the app launches.

**Key Features:**

- Shows app branding during initial load
- 2-second delay before navigating to MainActivity
- Uses Handler for delayed navigation
- Provides smooth user experience on app launch

**Key Methods:**

- `onCreate()`: Displays splash screen and sets up delayed navigation

---

### 3. MentoraApplication.kt

**Location:** `app/src/main/java/com/mentora/mobile/MentoraApplication.kt`

**Purpose:** Custom Application class for app-wide initialization.

**Key Features:**

- Extends Android Application class
- Enables WebView debugging in debug builds
- Entry point for app-wide configurations

**Key Methods:**

- `onCreate()`: Initializes app-wide settings

---

### 4. WebAppInterface.kt

**Location:** `app/src/main/java/com/mentora/mobile/WebAppInterface.kt`

**Purpose:** JavaScript bridge interface for communication between web content and native Android.

**Key Features:**

- Exposes native Android methods to JavaScript
- Enables bidirectional communication
- Uses `@JavascriptInterface` annotation for security

**Available Methods for JavaScript:**

```javascript
// Show toast message
window.Android.showToast("Hello!");

// Get device model
const model = window.Android.getDeviceInfo();

// Check if running on Android
const isAndroid = window.Android.isAndroid();
```

---

## 🛠️ Utility Kotlin Files

### 5. Constants.kt

**Location:** `app/src/main/java/com/mentora/mobile/utils/Constants.kt`

**Purpose:** Central location for all app-wide constants.

**Key Constants:**

- `LOCAL_WEB_APP_URL`: Path to local React app
- `SPLASH_DISPLAY_LENGTH`: Splash screen duration
- `PREFS_NAME`: SharedPreferences file name
- `JS_INTERFACE_NAME`: Name of JavaScript interface
- `BASE_URL`: API base URL (for future use)
- Various WebView configuration constants

**Usage:**

```kotlin
import com.mentora.mobile.utils.Constants

webView.loadUrl(Constants.LOCAL_WEB_APP_URL)
```

---

### 6. NetworkUtil.kt

**Location:** `app/src/main/java/com/mentora/mobile/utils/NetworkUtil.kt`

**Purpose:** Utility object for network connectivity checks.

**Key Features:**

- Singleton object pattern
- Compatible with both old and new Android APIs
- Checks for WiFi, cellular, and ethernet connections

**Available Methods:**

```kotlin
// Check if network is available
val hasNetwork = NetworkUtil.isNetworkAvailable(context)

// Check if connected via WiFi
val isWiFi = NetworkUtil.isWifiConnected(context)
```

---

### 7. PreferenceManager.kt

**Location:** `app/src/main/java/com/mentora/mobile/utils/PreferenceManager.kt`

**Purpose:** Manager class for handling SharedPreferences operations.

**Key Features:**

- Type-safe preference operations
- Support for String, Boolean, and Int values
- First launch detection
- Clear and remove operations

**Available Methods:**

```kotlin
val prefManager = PreferenceManager(context)

// First launch check
if (prefManager.isFirstLaunch()) {
    // Show onboarding
    prefManager.setFirstLaunch(false)
}

// Save/retrieve values
prefManager.saveString("key", "value")
val value = prefManager.getString("key")

prefManager.saveBoolean("flag", true)
val flag = prefManager.getBoolean("flag")

prefManager.saveInt("count", 42)
val count = prefManager.getInt("count")

// Clear all or specific preference
prefManager.remove("key")
prefManager.clearAll()
```

---

## 📊 File Statistics

| File | Lines of Code | Primary Purpose |
|------|---------------|-----------------|
| MainActivity.kt | ~65 | WebView hosting & configuration |
| SplashActivity.kt | ~27 | Splash screen display |
| MentoraApplication.kt | ~17 | App initialization |
| WebAppInterface.kt | ~40 | JavaScript bridge |
| Constants.kt | ~32 | Centralized constants |
| NetworkUtil.kt | ~57 | Network connectivity checks |
| PreferenceManager.kt | ~92 | Preferences management |
| **Total** | **~330** | **7 Kotlin files** |

---

## 🔗 Dependencies Between Files

```
MentoraApplication
    └── Uses: BuildConfig

MainActivity
    ├── Uses: WebAppInterface
    ├── Uses: ActivityMainBinding (generated)
    └── Could use: Constants, NetworkUtil, PreferenceManager

SplashActivity
    ├── Navigates to: MainActivity
    └── Could use: Constants

WebAppInterface
    └── Standalone (minimal dependencies)

PreferenceManager
    └── Uses: Constants

NetworkUtil
    └── Standalone utility

Constants
    └── Standalone (no dependencies)
```

---

## 🚀 Quick Start Guide

### Building the Project

```bash
# Open in Android Studio and sync Gradle
# Or use command line:
./gradlew build
```

### Running on Device/Emulator

```bash
./gradlew installDebug
adb shell am start -n com.mentora.mobile/.SplashActivity
```

### Debugging

```bash
# Enable WebView debugging (already enabled in MentoraApplication.kt)
# Then use Chrome DevTools: chrome://inspect
```

---

## 📝 Code Quality & Best Practices

### Implemented Best Practices:

✅ **Kotlin Coding Standards**: Official Kotlin style guide followed
✅ **Documentation**: All classes and public methods documented
✅ **Null Safety**: Leveraging Kotlin's null safety features
✅ **Object-Oriented**: Proper use of classes, objects, and interfaces
✅ **Memory Management**: Proper WebView cleanup in `onDestroy()`
✅ **Android Lifecycle**: Correct handling of Activity lifecycle
✅ **Modern APIs**: Using AndroidX and modern Android APIs
✅ **ViewBinding**: Enabled for type-safe view access

### Package Organization:

```
com.mentora.mobile/
├── MainActivity.kt           (UI - Activities)
├── SplashActivity.kt        (UI - Activities)
├── MentoraApplication.kt    (Core - Application)
├── WebAppInterface.kt       (Bridge - JavaScript Interface)
└── utils/                   (Utilities)
    ├── Constants.kt
    ├── NetworkUtil.kt
    └── PreferenceManager.kt
```

---

## 🔐 Security Considerations

1. **JavaScript Interface**: Uses `@JavascriptInterface` annotation (required for API 17+)
2. **WebView Security**: Configured with appropriate security settings
3. **HTTPS**: Mixed content mode set for flexibility (adjust for production)
4. **ProGuard**: Rules configured to protect JavaScript interfaces

---

## 🎨 UI/UX Features

- **Splash Screen**: Professional first impression
- **Material Design**: Modern, clean interface
- **Back Navigation**: Intuitive WebView navigation
- **Full Screen WebView**: Maximum content area
- **NoActionBar Theme**: Clean, modern appearance

---

## 📚 Additional Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Android Developer Guide](https://developer.android.com/kotlin)
- [WebView Best Practices](https://developer.android.com/guide/webapps/webview)
- [Material Design Guidelines](https://material.io/design)

---

## 🔄 Future Enhancements

Potential additions to consider:

- Push notifications support
- Offline caching strategy
- Analytics integration
- Crash reporting
- Deep linking support
- File upload/download handling
- Camera/photo access
- Location services
- In-app updates

---

*Last Updated: November 2024*
*Project: MentoraMobile*
*Language: Kotlin 1.9.0*
*Min SDK: 24 (Android 7.0)*
*Target SDK: 34 (Android 14)*
