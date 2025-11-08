# MentoraMobile - Android Application with On-Device AI

A modern Android application for Mentora, built with Kotlin and featuring:

- **WebView-based interface** that loads a React web application locally
- **On-device AI capabilities** powered by RunAnywhere SDK
- **Privacy-first architecture** - all AI processing happens on-device
- **Offline-capable** - works without internet after initial model download

## 🚀 Features

- **WebView Integration**: Displays React web application in a native Android WebView
- **On-Device AI**: Run AI models directly on the device (SmolLM2, Qwen, Llama)
- **Kotlin**: Modern, concise, and safe programming language
- **Material Design**: Clean and modern UI following Material Design guidelines
- **Splash Screen**: Attractive splash screen on app launch
- **JavaScript Bridge**: Bidirectional communication between native Android and web content
- **Offline Support**: All web assets are bundled locally in the app
- **Privacy-First AI**: 100% private AI inference with no data leaving the device

## 🏗️ Project Structure

```
MentoraMobile/
├── app/
│   ├── build.gradle                    # App-level build configuration (includes RunAnywhere SDK)
│   ├── proguard-rules.pro             # ProGuard rules for code obfuscation
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml    # App manifest
│           ├── assets/
│           │   └── mentora/           # React web app assets
│           ├── java/com/mentora/mobile/
│           │   ├── MainActivity.kt        # Main activity with WebView
│           │   ├── SplashActivity.kt      # Splash screen activity
│           │   ├── MentoraApplication.kt  # Application class (initializes AI SDK)
│           │   ├── WebAppInterface.kt     # JavaScript bridge interface (includes AI methods)
│           │   ├── ai/
│           │   │   └── AIManager.kt       # AI operations manager
│           │   └── utils/
│           │       ├── Constants.kt
│           │       ├── NetworkUtil.kt
│           │       └── PreferenceManager.kt
│           └── res/                   # Android resources
│               ├── layout/
│               │   ├── activity_main.xml
│               │   └── activity_splash.xml
│               ├── values/
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               └── xml/
│                   ├── backup_rules.xml
│                   └── data_extraction_rules.xml
├── build.gradle                       # Project-level build configuration
├── settings.gradle                    # Gradle settings (includes JitPack for RunAnywhere SDK)
├── gradle.properties                  # Gradle properties
├── README.md                          # This file
├── AI_INTEGRATION_GUIDE.md           # Complete guide for AI features
├── KOTLIN_FILES_OVERVIEW.md          # Detailed Kotlin files documentation
└── PROJECT_SUMMARY.md                # Project summary
```

## 🤖 AI Capabilities

### Supported Models

| Model                       | Size   | Quality | Use Case                   |
|-----------------------------|--------|---------|----------------------------|
| SmolLM2 360M Q8_0           | 119 MB | Basic   | Testing, quick responses   |
| Qwen 2.5 0.5B Instruct Q6_K | 374 MB | Good    | General chat, balanced     |
| Llama 3.2 1B Instruct Q6_K  | 815 MB | Best    | High-quality conversations |

### AI Features

- ✅ **Text Generation**: Generate AI responses from prompts
- ✅ **Streaming Inference**: Real-time token-by-token generation
- ✅ **Model Management**: Download, load, and switch between models
- ✅ **JavaScript API**: Full AI access from React/JavaScript code
- ✅ **Privacy-First**: All processing happens on-device
- ✅ **Offline Support**: No internet required after model download

### Using AI from JavaScript

```javascript
// Check if AI is available
if (window.Android) {
  // Get available models
  window.Android.getAvailableModels((modelsJson) => {
    const models = JSON.parse(modelsJson);
    console.log("Available models:", models);
  });
  
  // Generate AI response
  window.Android.generateText("What is AI?", (response) => {
    console.log("AI Response:", response);
  });
  
  // Stream AI response (real-time)
  window.Android.generateTextStream(
    "Tell me a story",
    (token) => console.log("Token:", token),
    () => console.log("Complete!")
  );
}
```

**See [AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md) for complete AI documentation.**

## 📋 Prerequisites

- Android Studio (latest version)
- JDK 17 or higher
- Android SDK (API level 24+)
- Kotlin 1.9.0 or higher

## 🔧 Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MentoraMobile
   ```

2. **Open in Android Studio**
    - Open Android Studio
    - Select "Open an Existing Project"
    - Navigate to the MentoraMobile directory
    - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
    - Connect an Android device or start an emulator
    - Click the "Run" button in Android Studio
    - Or use the command line:
      ```bash
      ./gradlew installDebug
      ```

## 📱 Key Components

### MainActivity.kt
The main activity that hosts the WebView displaying the React application. Features:
- Configures WebView settings for optimal performance
- Handles back button navigation
- Implements JavaScript bridge for native-web communication
- Registers AI interface for on-device inference

### AIManager.kt

Manages all AI operations using RunAnywhere SDK:

- Model downloading with progress tracking
- Loading/unloading models
- Text generation (streaming and non-streaming)
- Model status checking

### WebAppInterface.kt
Provides a JavaScript interface for communication between the web content and native Android:
- `showToast(message)`: Display Android toast messages from JavaScript
- `getDeviceInfo()`: Get device model information
- `isAndroid()`: Check if running on Android platform
- **AI Methods**:
    - `getAvailableModels(callback)`: List all AI models
    - `downloadModel(id, progress, complete)`: Download a model
    - `loadModel(id, callback)`: Load model into memory
    - `generateText(prompt, callback)`: Generate AI response
    - `generateTextStream(prompt, token, complete)`: Stream AI response
    - `chat(message, callback)`: Chat with AI
    - `getCurrentModel(callback)`: Get loaded model info
    - `unloadModel(callback)`: Unload current model

### SplashActivity.kt
A splash screen displayed when the app launches, providing a smooth user experience.

### MentoraApplication.kt

Custom Application class that:

- Enables WebView debugging in debug builds
- Initializes RunAnywhere SDK
- Registers LLM service provider
- Registers available AI models

## 🌐 JavaScript Bridge Usage

### Basic Methods

```javascript
// Show a toast message
if (window.Android) {
  window.Android.showToast("Hello from JavaScript!");
}

// Get device information
if (window.Android) {
  const deviceModel = window.Android.getDeviceInfo();
  console.log("Device:", deviceModel);
}
```

### AI Methods

See **[AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md)** for complete AI API documentation with
examples.

## 🛠️ Build Variants

- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization

## 📦 Dependencies

### Core Dependencies
- Kotlin Standard Library
- AndroidX Core KTX
- AndroidX AppCompat
- Material Components for Android
- AndroidX ConstraintLayout
- AndroidX WebKit

### AI Dependencies

- **RunAnywhere SDK Core** (v0.1.3-alpha): Component architecture, model management
- **RunAnywhere LLM LlamaCpp** (v0.1.3-alpha): 7 optimized llama.cpp native libraries
- **Kotlin Coroutines**: For async AI operations

### UI Dependencies

- Jetpack Compose (for modern UI)
- Lifecycle ViewModel
- Activity Compose

## 🔐 Permissions

The app requires the following permissions:

- `INTERNET`: For downloading AI models and network features
- `ACCESS_NETWORK_STATE`: To check network connectivity
- `WRITE_EXTERNAL_STORAGE` (API ≤ 28): For model storage

## 🎯 AI Workflow

```
1. App Starts
   ↓
2. SDK Initialized (MentoraApplication)
   ↓
3. Models Registered
   ↓
4. User Opens App
   ↓
5. Download Model (from JavaScript)
   ↓
6. Load Model (into memory)
   ↓
7. Generate AI Response
   ↓
8. Display in UI
```

## 🔒 Privacy & Security

- **100% On-Device AI**: All AI processing happens locally
- **No Data Transmission**: User prompts never leave the device
- **Offline Capable**: Works without internet after model download
- **JavaScript Interface Security**: Uses `@JavascriptInterface` annotation
- **WebView Security**: Configured with appropriate security settings

## 📚 Documentation

- **[AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md)** - Complete AI integration guide
- **[KOTLIN_FILES_OVERVIEW.md](KOTLIN_FILES_OVERVIEW.md)** - Detailed Kotlin code documentation
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Project summary and file list

## 🐛 Troubleshooting

### AI Model Issues

**Model download fails:**

- Check internet connection
- Verify sufficient storage space
- Try a smaller model (SmolLM2 360M)

**Model load fails:**

- Ensure model is fully downloaded
- Check available RAM
- Ensure `android:largeHeap="true"` in manifest

**Generation is slow:**

- Normal for on-device AI
- Try smaller models
- Use streaming for better UX

See **[AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md#troubleshooting)** for more troubleshooting
tips.

## 📄 License

[Add your license information here]

## 👥 Contributors

[Add contributor information here]

## 📞 Support

For issues or questions:

- **General Issues**: Create an issue in this repository
- **AI SDK Issues**: https://github.com/RunanywhereAI/runanywhere-sdks/issues
- **Documentation**: Check [AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md)

---

## 🎓 Additional Resources

- [RunAnywhere SDK Documentation](https://github.com/RunanywhereAI/runanywhere-sdks)
- [Android Quick Start](https://github.com/RunanywhereAI/runanywhere-sdks/blob/main/QUICKSTART_ANDROID.md)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Android Developer Guide](https://developer.android.com/kotlin)
- [WebView Best Practices](https://developer.android.com/guide/webapps/webview)
- [Material Design Guidelines](https://material.io/design)

---

**Built with ❤️ using Kotlin, RunAnywhere SDK, and on-device AI**

*Last Updated: November 2024*  
*Project: MentoraMobile*  
*Language: Kotlin 1.9.10*  
*Min SDK: 24 (Android 7.0)*  
*Target SDK: 35 (Android 14)*  
*RunAnywhere SDK: v0.1.3-alpha*

