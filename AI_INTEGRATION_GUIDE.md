# AI Integration Guide - MentoraMobile

Complete guide for using on-device AI capabilities in MentoraMobile using the RunAnywhere SDK.

## 📋 Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Setup](#setup)
4. [Using AI from JavaScript/React](#using-ai-from-javascriptreact)
5. [Available Models](#available-models)
6. [API Reference](#api-reference)
7. [Examples](#examples)
8. [Troubleshooting](#troubleshooting)

---

## Overview

MentoraMobile now includes **on-device AI capabilities** powered by
the [RunAnywhere SDK](https://github.com/RunanywhereAI/runanywhere-sdks). This enables:

- ✅ **100% Private** - All AI processing happens on-device
- ✅ **Offline-First** - No internet required after model download
- ✅ **Fast Response** - Low latency on-device inference
- ✅ **Multiple Models** - Choose from various AI models
- ✅ **Streaming Support** - Real-time token generation
- ✅ **JavaScript Bridge** - Full AI access from React code

### Key Features

- **Text Generation**: Generate AI responses from prompts
- **Streaming Inference**: Receive AI responses token-by-token
- **Model Management**: Download, load, and switch between models
- **Privacy-First**: All data stays on device
- **Cross-Platform**: Works on Android 7.0+ (API 24+)

---

## Architecture

```
React/JavaScript App (WebView)
        ↓
JavaScript Bridge (WebAppInterface)
        ↓
AI Manager (Kotlin)
        ↓
RunAnywhere SDK (Core + LlamaCpp)
        ↓
On-Device AI Models
```

### Components

1. **MentoraApplication** - Initializes RunAnywhere SDK on app start
2. **AIManager** - Manages all AI operations (singleton)
3. **WebAppInterface** - Exposes AI methods to JavaScript
4. **MainActivity** - Hosts WebView with React app

---

## Setup

### 1. SDK Initialization (Already Done)

The RunAnywhere SDK is automatically initialized when the app starts:

```kotlin
// In MentoraApplication.kt
RunAnywhere.initialize(
    context = this,
    apiKey = "dev",
    environment = SDKEnvironment.DEVELOPMENT
)

// Register LLM service provider
LlamaCppServiceProvider.register()

// Register available models
registerModels()
```

### 2. Build Configuration (Already Done)

Dependencies are already configured in `app/build.gradle`:

```gradle
dependencies {
    // RunAnywhere SDK - Core
    implementation "com.github.RunanywhereAI.runanywhere-sdks:runanywhere-kotlin:android-v0.1.3-alpha"
    
    // RunAnywhere SDK - LLM Module (includes llama.cpp)
    implementation "com.github.RunanywhereAI.runanywhere-sdks:runanywhere-llm-llamacpp:android-v0.1.3-alpha"
    
    // Required: Kotlin Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
}
```

### 3. Permissions (Already Done)

Required permissions are set in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />

<application android:largeHeap="true" ...>
```

---

## Using AI from JavaScript/React

### Accessing the Android Bridge

The Android interface is available at `window.Android`:

```javascript
if (window.Android) {
  // Android interface is available
  console.log("Running on Android with AI capabilities!");
}
```

### Basic Workflow

1. **Get available models**
2. **Download a model** (only once)
3. **Load the model** into memory
4. **Generate AI responses**

---

## Available Models

Three models are pre-registered (download required):

| Model | Size | Quality | Use Case |
|-------|------|---------|----------|
| **SmolLM2 360M Q8_0** | 119 MB | Basic | Testing, quick responses |
| **Qwen 2.5 0.5B Instruct Q6_K** | 374 MB | Good | General chat, balanced |
| **Llama 3.2 1B Instruct Q6_K** | 815 MB | Best | High-quality conversations |

**Recommendation**: Start with **SmolLM2 360M** for testing, upgrade to **Qwen 2.5 0.5B** for
production.

---

## API Reference

### 1. Get Available Models

```javascript
// Get list of all available models
window.Android.getAvailableModels((modelsJson) => {
  const models = JSON.parse(modelsJson);
  models.forEach(model => {
    console.log(`${model.name} - ${model.sizeInMB}`);
    console.log(`Downloaded: ${model.isDownloaded}, Loaded: ${model.isLoaded}`);
  });
});
```

**Model Object Structure:**

```javascript
{
  id: "string",
  name: "string",
  type: "LLM",
  isDownloaded: boolean,
  isLoaded: boolean,
  size: number,
  sizeInMB: "XXX.XX MB"
}
```

### 2. Download a Model

```javascript
// Download a model with progress tracking
const modelId = "model-id-here";

window.Android.downloadModel(
  modelId,
  // Progress callback (0-100%)
  (progress) => {
    console.log(`Download progress: ${progress}%`);
    // Update your UI progress bar
  },
  // Complete callback
  (success) => {
    if (success === "true") {
      console.log("Download complete!");
    } else {
      console.error("Download failed!");
    }
  }
);
```

### 3. Load a Model

```javascript
// Load a model into memory (required before generation)
window.Android.loadModel(modelId, (success) => {
  if (success === "true") {
    console.log("Model loaded successfully!");
    // Now you can generate text
  } else {
    console.error("Failed to load model");
  }
});
```

**Note**: Only one model can be loaded at a time. Loading a new model automatically unloads the
previous one.

### 4. Generate Text (Simple)

```javascript
// Generate AI response from a prompt
const prompt = "What is artificial intelligence?";

window.Android.generateText(prompt, (response) => {
  console.log("AI Response:", response);
  // Display response in your UI
});
```

### 5. Generate Text (Streaming)

```javascript
// Generate text with real-time token streaming
let fullResponse = "";

window.Android.generateTextStream(
  "Tell me a short story about AI",
  // Token callback (called for each token)
  (token) => {
    fullResponse += token;
    console.log("Token:", token);
    // Update UI in real-time
    document.getElementById("response").textContent = fullResponse;
  },
  // Complete callback
  (success) => {
    console.log("Generation complete!");
  }
);
```

### 6. Chat with AI

```javascript
// Convenience method for chat
window.Android.chat("Hello, how are you?", (response) => {
  console.log("AI:", response);
});
```

### 7. Check Model Status

```javascript
// Check if a model is currently loaded
window.Android.isModelLoaded((loaded) => {
  if (loaded === "true") {
    console.log("A model is loaded and ready");
  } else {
    console.log("No model loaded - load one first");
  }
});

// Get currently loaded model info
window.Android.getCurrentModel((modelJson) => {
  if (modelJson !== "null") {
    const model = JSON.parse(modelJson);
    console.log(`Current model: ${model.name}`);
  } else {
    console.log("No model currently loaded");
  }
});
```

### 8. Unload Model

```javascript
// Unload current model to free memory
window.Android.unloadModel((success) => {
  console.log("Model unloaded:", success);
});
```

---

## Examples

### Complete AI Chat Component (React)

```javascript
import React, { useState, useEffect } from 'react';

function AIChat() {
  const [models, setModels] = useState([]);
  const [currentModel, setCurrentModel] = useState(null);
  const [prompt, setPrompt] = useState('');
  const [response, setResponse] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadModels();
  }, []);

  const loadModels = () => {
    if (window.Android) {
      window.Android.getAvailableModels((modelsJson) => {
        const modelsList = JSON.parse(modelsJson);
        setModels(modelsList);
      });
    }
  };

  const downloadModel = (modelId) => {
    if (window.Android) {
      setLoading(true);
      window.Android.downloadModel(
        modelId,
        (progress) => {
          console.log(`Downloading: ${progress}%`);
        },
        (success) => {
          setLoading(false);
          if (success === "true") {
            alert("Model downloaded successfully!");
            loadModels(); // Refresh model list
          }
        }
      );
    }
  };

  const loadModel = (modelId) => {
    if (window.Android) {
      setLoading(true);
      window.Android.loadModel(modelId, (success) => {
        setLoading(false);
        if (success === "true") {
          setCurrentModel(modelId);
          alert("Model loaded successfully!");
        }
      });
    }
  };

  const generateResponse = () => {
    if (!window.Android || !prompt.trim()) return;

    setLoading(true);
    setResponse('');

    // Use streaming for real-time updates
    let fullResponse = '';
    window.Android.generateTextStream(
      prompt,
      (token) => {
        fullResponse += token;
        setResponse(fullResponse);
      },
      (success) => {
        setLoading(false);
        console.log("Generation complete:", success);
      }
    );
  };

  return (
    <div className="ai-chat">
      <h2>AI Chat</h2>

      {/* Model Selection */}
      <div className="models">
        <h3>Available Models:</h3>
        {models.map(model => (
          <div key={model.id} className="model-item">
            <span>{model.name} ({model.sizeInMB})</span>
            {!model.isDownloaded && (
              <button onClick={() => downloadModel(model.id)}>
                Download
              </button>
            )}
            {model.isDownloaded && !model.isLoaded && (
              <button onClick={() => loadModel(model.id)}>
                Load
              </button>
            )}
            {model.isLoaded && <span>✓ Loaded</span>}
          </div>
        ))}
      </div>

      {/* Chat Interface */}
      {currentModel && (
        <div className="chat">
          <textarea
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            placeholder="Type your message..."
            rows={4}
          />
          <button onClick={generateResponse} disabled={loading}>
            {loading ? 'Generating...' : 'Send'}
          </button>

          {response && (
            <div className="response">
              <h4>AI Response:</h4>
              <p>{response}</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AIChat;
```

### Simple Question-Answer

```javascript
// Ask a single question
function askAI(question) {
  if (window.Android) {
    window.Android.generateText(question, (response) => {
      alert(`AI says: ${response}`);
    });
  }
}

// Usage
askAI("What is the capital of France?");
```

### Batch Processing

```javascript
// Process multiple prompts
async function processBatch(prompts) {
  for (const prompt of prompts) {
    await new Promise((resolve) => {
      window.Android.generateText(prompt, (response) => {
        console.log(`Q: ${prompt}`);
        console.log(`A: ${response}`);
        resolve();
      });
    });
  }
}

// Usage
processBatch([
  "What is AI?",
  "Explain machine learning",
  "What is deep learning?"
]);
```

---

## Troubleshooting

### Model Download Fails

**Symptoms**: Download callback returns `false`

**Solutions**:

- Check internet connection
- Ensure `INTERNET` permission is granted
- Verify sufficient storage space
- Try a smaller model first (SmolLM2 360M)

### Model Load Fails

**Symptoms**: `loadModel` callback returns `false`

**Solutions**:

- Ensure model is fully downloaded first
- Check available device RAM (models need memory to load)
- Close other apps to free memory
- Verify `android:largeHeap="true"` is set in manifest

### Generation is Slow

**Normal Behavior**: On-device AI is slower than cloud APIs

**Optimization Tips**:

- Use smaller models (SmolLM2 360M is fastest)
- Keep prompts concise
- Use streaming for better UX (shows progress)
- Ensure no other heavy apps are running

### App Crashes During Generation

**Symptoms**: App closes unexpectedly during AI generation

**Solutions**:

- Use a smaller model
- Ensure `android:largeHeap="true"` is enabled
- Close background apps
- Try on a device with more RAM

### JavaScript Bridge Not Working

**Symptoms**: `window.Android` is undefined

**Solutions**:

- Ensure WebView has loaded completely
- Check that JavaScript is enabled
- Verify WebAppInterface is registered correctly
- Test on physical device (emulator may have issues)

### Callback Functions Not Executing

**Issue**: Callbacks not being called

**Solutions**:

- Make sure callback function is defined in window scope:
  ```javascript
  window.myCallback = function(result) {
    console.log(result);
  };
  window.Android.generateText(prompt, "myCallback");
  ```
- Or use inline callbacks (preferred method shown in examples above)

---

## Performance Tips

1. **Load Once**: Only load a model once per session
2. **Unload When Done**: Free memory by unloading unused models
3. **Use Streaming**: Better UX and perceived performance
4. **Cache Responses**: Store common AI responses locally
5. **Smaller Models**: Start with smaller models, upgrade as needed

---

## Privacy & Security

- ✅ **All AI processing happens on-device**
- ✅ **No data is sent to external servers**
- ✅ **Models are downloaded once and cached**
- ✅ **User prompts never leave the device**
- ✅ **Fully offline after model download**

---

## Further Reading

- [RunAnywhere SDK Documentation](https://github.com/RunanywhereAI/runanywhere-sdks)
- [Android Quick Start](https://github.com/RunanywhereAI/runanywhere-sdks/blob/main/QUICKSTART_ANDROID.md)
- [Hugging Face Models](https://huggingface.co/models?library=gguf)

---

## Support

For issues or questions:

- Check the [Troubleshooting](#troubleshooting) section
- Review code examples in this guide
- Check SDK repository issues: https://github.com/RunanywhereAI/runanywhere-sdks/issues

---

**Last Updated**: November 2024  
**RunAnywhere SDK Version**: v0.1.3-alpha  
**Minimum Android Version**: API 24 (Android 7.0)
