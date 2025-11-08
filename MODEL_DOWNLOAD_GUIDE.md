# AI Model Download Guide

## Overview

Before you can use the AI course generation feature, you **MUST** download at least one AI model.
The models run entirely on-device, so there's no internet required for generation once downloaded.

## Available Android Bridge Methods

The app exposes these methods through the Android bridge (accessible via JavaScript):

### 1. Get Available Models

```javascript
window.Android.getAvailableModels(function(modelsJson) {
    const models = JSON.parse(modelsJson);
    console.log('Available models:', models);
});
```

**Response format:**

```json
[
  {
    "id": "tinyllama-1.1b",
    "name": "TinyLlama 1.1B",
    "category": "TEXT_GENERATION",
    "sizeInMB": "637.00",
    "isDownloaded": false
  },
  {
    "id": "phi-2-2.7b",
    "name": "Phi-2 2.7B",
    "category": "TEXT_GENERATION",
    "sizeInMB": "1680.00",
    "isDownloaded": true
  }
]
```

### 2. Download a Model

```javascript
window.Android.downloadModel(
    'tinyllama-1.1b',  // Model ID
    function(progress) {
        console.log('Download progress:', progress + '%');
        // Update UI with progress
    },
    function(success) {
        if (success) {
            console.log('Download completed!');
        } else {
            console.log('Download failed!');
        }
    }
);
```

### 3. Load a Model

```javascript
window.Android.loadModel('tinyllama-1.1b', function(success) {
    if (success) {
        console.log('Model loaded successfully!');
    } else {
        console.log('Failed to load model');
    }
});
```

### 4. Check if Model is Loaded

```javascript
window.Android.isModelLoaded(function(isLoaded) {
    console.log('Model loaded:', isLoaded);
});
```

### 5. Get Current Model Info

```javascript
window.Android.getCurrentModel(function(modelInfo) {
    if (modelInfo) {
        const model = JSON.parse(modelInfo);
        console.log('Current model:', model.name);
    } else {
        console.log('No model loaded');
    }
});
```

## Recommended Models

### Small & Fast (Best for Testing)

**TinyLlama 1.1B**

- Size: ~637 MB
- Speed: Very fast
- Quality: Basic but functional
- Best for: Quick testing, low-end devices

### Medium (Balanced)

**Phi-2 2.7B**

- Size: ~1.68 GB
- Speed: Moderate
- Quality: Good
- Best for: Production use, mid-range devices

### Large (High Quality)

**LLaMA 7B** (if available)

- Size: ~4-7 GB
- Speed: Slower
- Quality: Excellent
- Best for: High-end devices, best results

## How to Add Model Download UI

If your React app doesn't have a Settings/Models screen yet, here's how to add one:

### Option 1: Simple Test Page

Add this to your React app to test model downloads:

```jsx
import React, { useState, useEffect } from 'react';

function ModelManager() {
    const [models, setModels] = useState([]);
    const [downloading, setDownloading] = useState(null);
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        loadModels();
    }, []);

    const loadModels = () => {
        if (window.Android && window.Android.getAvailableModels) {
            window.Android.getAvailableModels((modelsJson) => {
                const modelList = JSON.parse(modelsJson);
                setModels(modelList);
            });
        }
    };

    const downloadModel = (modelId) => {
        setDownloading(modelId);
        setProgress(0);

        window.Android.downloadModel(
            modelId,
            (prog) => setProgress(prog),
            (success) => {
                if (success) {
                    alert('Download completed!');
                    loadModels(); // Refresh list
                } else {
                    alert('Download failed!');
                }
                setDownloading(null);
                setProgress(0);
            }
        );
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>AI Models</h2>
            {models.map(model => (
                <div key={model.id} style={{ 
                    border: '1px solid #ccc', 
                    padding: '15px', 
                    marginBottom: '10px',
                    borderRadius: '8px'
                }}>
                    <h3>{model.name}</h3>
                    <p>Size: {model.sizeInMB} MB</p>
                    <p>Status: {model.isDownloaded ? '✅ Downloaded' : '⬇️ Not Downloaded'}</p>
                    
                    {!model.isDownloaded && downloading !== model.id && (
                        <button onClick={() => downloadModel(model.id)}>
                            Download
                        </button>
                    )}
                    
                    {downloading === model.id && (
                        <div>
                            <p>Downloading: {progress}%</p>
                            <progress value={progress} max="100" style={{ width: '100%' }} />
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
}

export default ModelManager;
```

### Option 2: Quick Terminal Test

You can also test from Chrome DevTools console (if WebView debugging is enabled):

```javascript
// 1. Check available models
window.Android.getAvailableModels((models) => {
    console.log('Available models:', models);
});

// 2. Download TinyLlama (smallest model)
window.Android.downloadModel(
    'tinyllama-1.1b',
    (progress) => console.log('Progress:', progress + '%'),
    (success) => console.log('Done:', success)
);

// 3. After download, load the model
window.Android.loadModel('tinyllama-1.1b', (success) => {
    console.log('Model loaded:', success);
});
```

## Manual Testing Steps

### Step 1: Enable WebView Debugging

The app already has this enabled in `MainActivity.kt`:

```kotlin
WebView.setWebContentsDebuggingEnabled(true)
```

### Step 2: Connect Chrome DevTools

1. Connect your Android device via USB
2. Open Chrome on your computer
3. Go to `chrome://inspect`
4. Find your app's WebView
5. Click "Inspect"

### Step 3: Download a Model via Console

```javascript
// Download TinyLlama (recommended for first test)
window.Android.downloadModel(
    'tinyllama-1.1b',
    (p) => console.log('Progress:', p + '%'),
    (s) => console.log('Success:', s)
);
```

### Step 4: Verify Download

```javascript
window.Android.getAvailableModels((models) => {
    const modelList = JSON.parse(models);
    modelList.forEach(m => {
        console.log(`${m.name}: ${m.isDownloaded ? 'Downloaded ✅' : 'Not downloaded'}`);
    });
});
```

### Step 5: Test Course Generation

Once a model is downloaded, try generating a course:

1. Go to course creation screen
2. Enter course details
3. Click "Generate Content"
4. Wait for AI to process
5. Check the generated course structure

## Troubleshooting

### "No downloaded models available" Error

- **Cause**: No models have been downloaded yet
- **Solution**: Download at least one model using the steps above

### Download Stuck at 0%

- **Cause**: Network issue or storage permission
- **Solution**:
    - Check internet connection
    - Verify app has storage permissions
    - Check available storage space (need at least 1-2 GB free)

### Model Download Failed

- **Check Logcat**:
  ```bash
  adb logcat | grep "AIManager\|RunAnywhere"
  ```
- **Common issues**:
    - Insufficient storage
    - Network timeout
    - SDK initialization issue

### Model Won't Load

- **Cause**: Model file corrupted or incomplete
- **Solution**:
    - Delete and re-download the model
    - Try a different model
    - Check Logcat for specific error

## Storage Requirements

Make sure your device has enough storage:

- **TinyLlama 1.1B**: ~700 MB free
- **Phi-2 2.7B**: ~2 GB free
- **LLaMA 7B**: ~8 GB free

## Performance Expectations

### Download Times (on good WiFi)

- TinyLlama: 2-5 minutes
- Phi-2: 5-10 minutes
- LLaMA 7B: 15-30 minutes

### Generation Times

- TinyLlama: 10-30 seconds
- Phi-2: 20-60 seconds
- LLaMA 7B: 60-180 seconds

## Next Steps

After downloading a model:

1. Test basic generation with the `generateText` method
2. Try course generation from the UI
3. Experiment with different prompts
4. Consider downloading a larger model for better quality

## Support

If you need help:

1. Check Android Logcat for errors
2. Verify WebView debugging is enabled
3. Test with Chrome DevTools console
4. Try the smallest model (TinyLlama) first
5. Check the AI_COURSE_GENERATION.md for usage details
