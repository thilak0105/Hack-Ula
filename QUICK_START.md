# Quick Start Guide - MentoraMobile with AI

Get up and running with MentoraMobile and on-device AI in 5 minutes!

## 📦 What You Have

- ✅ Complete Android app with Kotlin
- ✅ WebView hosting your React app
- ✅ RunAnywhere SDK integrated (v0.1.3-alpha)
- ✅ 3 AI models pre-registered
- ✅ JavaScript bridge with AI methods
- ✅ Full documentation

---

## 🚀 Quick Build & Run

### Step 1: Open in Android Studio

```bash
cd /path/to/MentoraMobile
# Open this directory in Android Studio
```

### Step 2: Sync Gradle

- Android Studio will prompt to sync Gradle
- Click "Sync Now"
- **⏱️ First sync takes 2-3 minutes** (JitPack builds RunAnywhere SDK)
- Subsequent syncs are instant

### Step 3: Build the App

```bash
./gradlew build
```

Or in Android Studio: `Build > Make Project`

### Step 4: Run on Device

```bash
./gradlew installDebug
```

Or in Android Studio: Click the green "Run" button ▶️

---

## 🤖 Test AI Features

### From Android Studio Logcat

After the app starts, you should see:

```
I/MentoraApp: Initializing RunAnywhere SDK...
I/MentoraApp: RunAnywhere SDK initialized successfully
I/MentoraApp: AI models registered successfully
```

### From Your React App

Add this test code to your React component:

```javascript
import React, { useEffect, useState } from 'react';

function AITest() {
  const [models, setModels] = useState([]);
  const [aiReady, setAiReady] = useState(false);

  useEffect(() => {
    // Check if AI is available
    if (window.Android) {
      console.log("✅ AI Bridge is available!");
      setAiReady(true);
      
      // Get available models
      window.Android.getAvailableModels((modelsJson) => {
        const modelsList = JSON.parse(modelsJson);
        console.log("📦 Available models:", modelsList);
        setModels(modelsList);
      });
    } else {
      console.log("❌ AI Bridge not available (not running on Android?)");
    }
  }, []);

  const testAI = () => {
    if (window.Android) {
      window.Android.generateText("Say hello!", (response) => {
        alert(`AI Response: ${response}`);
      });
    }
  };

  return (
    <div>
      <h2>AI Status: {aiReady ? "✅ Ready" : "❌ Not Available"}</h2>
      <h3>Models: {models.length}</h3>
      <button onClick={testAI}>Test AI</button>
    </div>
  );
}

export default AITest;
```

---

## 📱 Download and Use AI

### Step 1: Get Model ID

```javascript
window.Android.getAvailableModels((modelsJson) => {
  const models = JSON.parse(modelsJson);
  console.log("First model ID:", models[0].id);
  // Use this ID for next steps
});
```

### Step 2: Download Model

```javascript
const modelId = "model-id-from-step-1";

window.Android.downloadModel(
  modelId,
  (progress) => console.log(`Downloading: ${progress}%`),
  (success) => console.log(`Download ${success === "true" ? "succeeded" : "failed"}`)
);
```

### Step 3: Load Model

```javascript
window.Android.loadModel(modelId, (success) => {
  if (success === "true") {
    console.log("✅ Model loaded! Ready to use AI");
  }
});
```

### Step 4: Generate AI Response

```javascript
window.Android.generateText("What is mentoring?", (response) => {
  console.log("AI says:", response);
});
```

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| **[README.md](README.md)** | Project overview, features, setup |
| **[AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md)** | Complete AI API reference & examples |
| **[RUNANYWHERE_INTEGRATION_SUMMARY.md](RUNANYWHERE_INTEGRATION_SUMMARY.md)** | Integration details & what was added |
| **[KOTLIN_FILES_OVERVIEW.md](KOTLIN_FILES_OVERVIEW.md)** | Kotlin code documentation |
| **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** | File list & project statistics |

---

## 🔍 Key Files

### Kotlin Files

```
app/src/main/java/com/mentora/mobile/
├── MainActivity.kt              # Main WebView activity
├── MentoraApplication.kt        # SDK initialization
├── WebAppInterface.kt           # JavaScript bridge (AI methods)
├── SplashActivity.kt            # Splash screen
├── ai/
│   └── AIManager.kt             # AI operations manager
└── utils/
    ├── Constants.kt             # App constants
    ├── NetworkUtil.kt           # Network utilities
    └── PreferenceManager.kt     # Preferences manager
```

### Configuration Files

```
app/build.gradle                 # Dependencies (RunAnywhere SDK)
settings.gradle                  # JitPack repository
AndroidManifest.xml              # Permissions & settings
```

---

## 💡 Example Use Cases

### Simple Q&A

```javascript
function askAI(question) {
  window.Android.generateText(question, (answer) => {
    document.getElementById("answer").textContent = answer;
  });
}
```

### Streaming Chat

```javascript
function streamChat(message) {
  let response = "";
  window.Android.generateTextStream(
    message,
    (token) => {
      response += token;
      document.getElementById("chat").textContent = response;
    },
    () => console.log("Done!")
  );
}
```

### Mentoring Assistant

```javascript
function getMentoringAdvice(situation) {
  const prompt = `As a mentoring assistant, provide advice for: ${situation}`;
  
  window.Android.generateText(prompt, (advice) => {
    displayAdvice(advice);
  });
}
```

---

## ⚠️ Common Issues

### Issue: `window.Android` is undefined

**Fix:** You're either:

- Not running on Android
- WebView hasn't loaded yet
- JavaScript is disabled

**Solution:**

```javascript
// Wait for window to load
window.addEventListener('load', () => {
  if (window.Android) {
    // Now safe to use
  }
});
```

### Issue: Model download fails

**Fix:**

- Check internet connection
- Ensure INTERNET permission
- Verify storage space

### Issue: Model load fails

**Fix:**

- Ensure model is fully downloaded
- Check device has enough RAM
- Try smaller model (SmolLM2 360M)

---

## 🎯 Next Steps

1. **✅ Build & Run** - Get the app running on your device
2. **✅ Test AI Bridge** - Verify `window.Android` is available
3. **✅ Download a Model** - Start with SmolLM2 360M (119 MB)
4. **✅ Generate First Response** - Test AI with a simple prompt
5. **✅ Build Your Feature** - Integrate AI into your mentoring app

---

## 🆘 Need Help?

1. **Check Documentation** - [AI_INTEGRATION_GUIDE.md](AI_INTEGRATION_GUIDE.md)
2. **Review Examples** - See code examples above
3. **Check Logs** - Android Studio Logcat for errors
4. **SDK Issues** - https://github.com/RunanywhereAI/runanywhere-sdks/issues

---

## 📊 Quick Reference

### Available AI Models

| Model | Size | Speed | Quality |
|-------|------|-------|---------|
| SmolLM2 360M | 119 MB | Fast | Basic |
| Qwen 2.5 0.5B | 374 MB | Medium | Good |
| Llama 3.2 1B | 815 MB | Slow | Best |

### JavaScript Bridge Methods

```javascript
// Model Management
window.Android.getAvailableModels(callback)
window.Android.downloadModel(id, progressCb, completeCb)
window.Android.loadModel(id, callback)
window.Android.unloadModel(callback)

// AI Generation
window.Android.generateText(prompt, callback)
window.Android.generateTextStream(prompt, tokenCb, completeCb)
window.Android.chat(message, callback)

// Status
window.Android.isModelLoaded(callback)
window.Android.getCurrentModel(callback)

// Utilities
window.Android.showToast(message)
window.Android.getDeviceInfo()
window.Android.isAndroid()
```

---

**Ready to build?** 🚀

```bash
./gradlew installDebug && adb logcat -s MentoraApp:I AIManager:I
```

This will build, install, and show AI-related logs.

---

**Last Updated:** November 2024  
**Status:** ✅ Ready for Development  
**RunAnywhere SDK:** v0.1.3-alpha
