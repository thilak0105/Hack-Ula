# RunAnywhere AI Integration Guide - MentoraMobile

Complete guide for using on-device AI capabilities in your MentoraMobile app with the RunAnywhere
SDK.

---

## 🎉 What's Been Integrated

Your MentoraMobile app now includes:

✅ **RunAnywhere SDK v0.1.3-alpha** - Core SDK + LlamaCpp module  
✅ **7 Kotlin AI classes** - Complete AI management system  
✅ **JavaScript Bridge** - Access AI from your React web app  
✅ **3 Pre-registered Models** - Ready to download and use  
✅ **Full Documentation** - This guide + code examples

---

## 📦 What's Included

### AAR Files (Downloaded)

- `app/libs/RunAnywhereKotlinSDK-release.aar` (4.0 MB) - Core SDK
- `app/libs/runanywhere-llm-llamacpp-release.aar` (2.1 MB) - LLM module with 7 ARM64 CPU variants

### Kotlin Files

- `MentoraApplication.kt` - SDK initialization & model registration
- `ai/AIManager.kt` - AI operations manager
- `WebAppInterface.kt` - JavaScript bridge with AI methods

### Pre-registered Models

1. **SmolLM2 360M Q8_0** (119 MB) - Fast, basic quality
2. **Qwen 2.5 0.5B Instruct Q6_K** (374 MB) - Balanced
3. **Llama 3.2 1B Instruct Q6_K** (815 MB) - Best quality

---

## 🚀 Quick Start

### Step 1: Build and Run

```bash
cd /Users/thilak/PythonFiles/Competetions/Hack-Ula/MentoraMobile
./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.mentora.mobile/.MainActivity
```

### Step 2: Test from JavaScript Console

Open Chrome DevTools (`chrome://inspect`) and connect to your app:

```javascript
// Check if AI is available
console.log(window.Android);

// Get available models
window.Android.getAvailableModels((modelsJson) => {
  const models = JSON.parse(modelsJson);
  console.log("Available models:", models);
  models.forEach(m => {
    console.log(`${m.name} - ${m.sizeInMB} MB - Downloaded: ${m.isDownloaded}`);
  });
});
```

### Step 3: Download a Model

```javascript
// Download the smallest model (119 MB)
// Get the model ID from step 2
const modelId = "YOUR_MODEL_ID_HERE";

window.Android.downloadModel(
  modelId,
  (progress) => console.log(`Download: ${progress}%`),
  (success) => console.log("Download complete:", success)
);
```

### Step 4: Load and Test

```javascript
// Load the model
window.Android.loadModel(modelId, (success) => {
  console.log("Model loaded:", success);
  
  if (success) {
    // Generate text!
    window.Android.generateText("What is mentoring?", (response) => {
      console.log("AI Response:", response);
    });
  }
});
```

---

## 📱 JavaScript API Reference

### Model Management

#### Get Available Models

```javascript
window.Android.getAvailableModels((modelsJson) => {
  const models = JSON.parse(modelsJson);
  // models is array of: {id, name, type, sizeInMB, isDownloaded, isLoaded}
});
```

#### Download Model

```javascript
window.Android.downloadModel(
  modelId,
  (progress) => {
    // progress: 0-100
    updateProgressBar(progress);
  },
  (success) => {
    // success: boolean
    if (success) console.log("Download complete!");
  }
);
```

#### Load Model

```javascript
window.Android.loadModel(modelId, (success) => {
  if (success) {
    console.log("Model loaded and ready!");
  }
});
```

#### Unload Model

```javascript
window.Android.unloadModel((success) => {
  console.log("Model unloaded:", success);
});
```

#### Check Model Status

```javascript
// Check if any model is loaded
window.Android.isModelLoaded((isLoaded) => {
  console.log("Model loaded:", isLoaded);
});

// Get current model info
window.Android.getCurrentModel((modelJson) => {
  if (modelJson) {
    const model = JSON.parse(modelJson);
    console.log("Current model:", model.name);
  } else {
    console.log("No model loaded");
  }
});
```

---

### Text Generation

#### Simple Generation (Non-Streaming)

```javascript
window.Android.generateText("Your prompt here", (response) => {
  console.log("AI:", response);
  displayResponse(response);
});
```

#### Streaming Generation (Real-time)

```javascript
let fullResponse = "";

window.Android.generateTextStream(
  "Tell me a story",
  (token) => {
    // Called for each token
    fullResponse += token;
    updateUI(fullResponse);
  },
  (success) => {
    // Called when complete
    console.log("Generation complete:", success);
  }
);
```

#### Chat (Convenience Method)

```javascript
window.Android.chat("What is AI?", (response) => {
  console.log("AI:", response);
});
```

---

## 💻 React Integration Example

### AI Chat Component

```jsx
import React, { useState, useEffect } from 'react';

function AIChatComponent() {
  const [models, setModels] = useState([]);
  const [selectedModel, setSelectedModel] = useState(null);
  const [isLoaded, setIsLoaded] = useState(false);
  const [message, setMessage] = useState('');
  const [response, setResponse] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);

  // Load available models on mount
  useEffect(() => {
    if (window.Android) {
      window.Android.getAvailableModels((modelsJson) => {
        const modelsList = JSON.parse(modelsJson);
        setModels(modelsList);
      });
    }
  }, []);

  const downloadModel = (modelId) => {
    window.Android.downloadModel(
      modelId,
      (progress) => console.log(`Download: ${progress}%`),
      (success) => {
        if (success) {
          alert('Model downloaded successfully!');
        }
      }
    );
  };

  const loadModel = (modelId) => {
    window.Android.loadModel(modelId, (success) => {
      setIsLoaded(success);
      if (success) {
        setSelectedModel(modelId);
        alert('Model loaded!');
      }
    });
  };

  const generateResponse = () => {
    if (!message.trim()) return;
    
    setIsGenerating(true);
    setResponse('');
    
    let fullResponse = '';
    
    window.Android.generateTextStream(
      message,
      (token) => {
        fullResponse += token;
        setResponse(fullResponse);
      },
      (success) => {
        setIsGenerating(false);
        if (success) {
          console.log('Generation complete');
        }
      }
    );
  };

  return (
    <div className="ai-chat">
      <h2>On-Device AI Chat</h2>
      
      {/* Model Selection */}
      <div className="model-selector">
        <h3>Available Models</h3>
        {models.map(model => (
          <div key={model.id} className="model-item">
            <span>{model.name} ({model.sizeInMB} MB)</span>
            {!model.isDownloaded ? (
              <button onClick={() => downloadModel(model.id)}>
                Download
              </button>
            ) : !model.isLoaded ? (
              <button onClick={() => loadModel(model.id)}>
                Load
              </button>
            ) : (
              <span>✓ Loaded</span>
            )}
          </div>
        ))}
      </div>

      {/* Chat Interface */}
      {isLoaded && (
        <div className="chat-interface">
          <textarea
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Type your message..."
            rows={4}
          />
          <button 
            onClick={generateResponse}
            disabled={isGenerating}
          >
            {isGenerating ? 'Generating...' : 'Send'}
          </button>
          
          {response && (
            <div className="response">
              <strong>AI:</strong>
              <p>{response}</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AIChatComponent;
```

---

## 🎯 Complete Workflow Example

### 1. App Startup (Automatic)

```
User opens app
  ↓
MentoraApplication.onCreate()
  ↓
Initialize RunAnywhere SDK
  ↓
Register LlamaCpp provider
  ↓
Register 3 AI models
  ↓
Scan for downloaded models
  ↓
SDK ready!
```

### 2. User Downloads Model

```javascript
// User clicks "Download" button
window.Android.getAvailableModels((modelsJson) => {
  const models = JSON.parse(modelsJson);
  const smallModel = models[0]; // SmolLM2 360M
  
  window.Android.downloadModel(
    smallModel.id,
    (progress) => {
      updateProgressBar(progress);
    },
    (success) => {
      if (success) {
        showNotification("Model downloaded!");
      }
    }
  );
});
```

### 3. User Loads Model

```javascript
window.Android.loadModel(modelId, (success) => {
  if (success) {
    enableChatInterface();
  }
});
```

### 4. User Chats with AI

```javascript
function sendMessage(userMessage) {
  addMessageToUI(userMessage, 'user');
  
  let aiResponse = '';
  
  window.Android.generateTextStream(
    userMessage,
    (token) => {
      aiResponse += token;
      updateAIMessage(aiResponse);
    },
    (success) => {
      if (success) {
        finalizeMessage(aiResponse);
      }
    }
  );
}
```

---

## 📊 Model Recommendations

### For Testing & Development

**SmolLM2 360M Q8_0** (119 MB)

- Fastest download
- Quick inference
- Good for prototyping
- Lower quality responses

### For Production (Balanced)

**Qwen 2.5 0.5B Instruct Q6_K** (374 MB)

- Good balance of size/quality
- Fast enough for real-time chat
- Better instruction following
- Recommended for most apps

### For Best Quality

**Llama 3.2 1B Instruct Q6_K** (815 MB)

- Highest quality responses
- Slower inference
- More memory usage
- Use if quality is critical

---

## 🔧 Advanced Usage

### Custom System Prompts

```javascript
const systemPrompt = `You are a helpful mentoring assistant. 
Keep responses concise and encouraging.`;

const userMessage = "How do I learn programming?";
const fullPrompt = `${systemPrompt}\n\nUser: ${userMessage}\nAssistant:`;

window.Android.generateText(fullPrompt, (response) => {
  console.log(response);
});
```

### Multi-Turn Conversations

```javascript
let conversationHistory = [];

function chat(message) {
  conversationHistory.push(`User: ${message}`);
  
  const prompt = conversationHistory.join('\n') + '\nAssistant:';
  
  window.Android.generateText(prompt, (response) => {
    conversationHistory.push(`Assistant: ${response}`);
    displayResponse(response);
  });
}
```

### Progress Tracking

```javascript
function downloadWithProgress(modelId) {
  const progressBar = document.getElementById('progressBar');
  const statusText = document.getElementById('statusText');
  
  window.Android.downloadModel(
    modelId,
    (progress) => {
      progressBar.style.width = `${progress}%`;
      statusText.textContent = `Downloading: ${progress}%`;
    },
    (success) => {
      if (success) {
        statusText.textContent = 'Download complete!';
        progressBar.style.width = '100%';
      } else {
        statusText.textContent = 'Download failed';
      }
    }
  );
}
```

---

## 🐛 Troubleshooting

### Model Download Fails

**Problem:** Download progress stops or fails

**Solutions:**

- Check internet connection
- Ensure storage space available (models are 100MB-1GB)
- Try downloading again
- Check Android logs: `adb logcat -s MentoraApp:I AIManager:I`

### Model Load Fails

**Problem:** Model loaded = false

**Solutions:**

- Ensure model is fully downloaded first
- Close other apps to free memory
- Try a smaller model (SmolLM2 360M)
- Enable `largeHeap` in AndroidManifest (already done)
- Restart the app

### Generation is Slow

**Problem:** AI responses take too long

**Solutions:**

- Use a smaller model (SmolLM2 360M)
- Shorten prompts
- Use streaming for better UX
- On-device AI is slower than cloud - this is normal

### JavaScript Bridge Not Working

**Problem:** `window.Android` is undefined

**Solutions:**

- Check if running on Android device/emulator
- Wait for page load: `window.onload = () => { /* use Android */ }`
- Ensure WebView JavaScript is enabled (already done)
- Check Chrome DevTools console for errors

### App Crashes During Generation

**Problem:** App closes unexpectedly

**Solutions:**

- Use a smaller model
- Close other apps
- Check available device memory
- Review crash logs: `adb logcat *:E`

---

## 📱 Testing Checklist

Before deploying to your team:

- [ ] App builds successfully
- [ ] `window.Android` is available in console
- [ ] Can list available models
- [ ] Can download a model (try SmolLM2 360M)
- [ ] Download progress updates work
- [ ] Can load the downloaded model
- [ ] Can generate text responses
- [ ] Streaming generation works
- [ ] Can unload model
- [ ] Error handling works

---

## 🔐 Privacy & Security

### What's Private

✅ **All AI processing happens on-device**  
✅ **No data sent to external servers**  
✅ **User prompts never leave the device**  
✅ **Full offline operation after model download**  
✅ **No analytics or tracking from SDK**

### What Requires Network

⚠️ **Model downloads** - Only during initial download  
⚠️ **Your web app** - If it connects to your backend

---

## 📚 Additional Resources

### Official Documentation

- [RunAnywhere SDK GitHub](https://github.com/RunanywhereAI/runanywhere-sdks)
- [Android Quick Start](https://github.com/RunanywhereAI/runanywhere-sdks/blob/main/QUICKSTART_ANDROID.md)
- [Example Apps](https://github.com/RunanywhereAI/runanywhere-sdks/tree/main/examples/android)

### Model Sources

- [Hugging Face GGUF Models](https://huggingface.co/models?library=gguf)
- [Recommended Models List](https://github.com/RunanywhereAI/runanywhere-sdks/blob/main/docs/recommended-models.md)

### Support

- GitHub Issues: [runanywhere-sdks/issues](https://github.com/RunanywhereAI/runanywhere-sdks/issues)
- Your team: See `SETUP_GUIDE.md`

---

## 🎉 Next Steps

1. ✅ **Build and test** the app on your device
2. ✅ **Download a model** (start with SmolLM2 360M)
3. ✅ **Test generation** from Chrome DevTools
4. ✅ **Integrate into React** using the examples above
5. ✅ **Push to GitHub** for your team
6. ✅ **Share this guide** with teammates

---

## 💡 Tips for Your Team

- **Start with the smallest model** for quick testing
- **Use streaming** for better user experience
- **Show download progress** to keep users informed
- **Handle errors gracefully** (network failures, out of memory)
- **Test on real devices** - emulators may be slower
- **Monitor memory usage** - AI models need RAM
- **Provide model selection** - let users choose based on their device

---

**Your app now has powerful on-device AI! 🎊**

All the code is integrated and ready to use. Just build, run, and start chatting with AI!
