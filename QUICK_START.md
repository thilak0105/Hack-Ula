# Quick Start Guide - AI Course Generation

## 🚀 Get Started in 5 Steps

### Step 1: Build & Install the App
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Step 2: Enable Chrome DevTools (For Testing)

1. Connect your Android device via USB (enable USB debugging)
2. Open Chrome browser on your computer
3. Navigate to `chrome://inspect`
4. Find "Mentora Mobile" WebView
5. Click **"Inspect"**

### Step 3: Download an AI Model

In the Chrome DevTools console, run:

```javascript
// Option A: Quick download (smallest model - recommended for testing)
window.Android.downloadModel(
    'tinyllama-1.1b',
    (progress) => console.log('⬇️ Progress:', progress + '%'),
    (success) => console.log(success ? '✅ Download complete!' : '❌ Download failed')
);
```

**Expected output:**
```
⬇️ Progress: 0%
⬇️ Progress: 5%
⬇️ Progress: 10%
...
⬇️ Progress: 100%
✅ Download complete!
```

**⏱️ Wait time:** 2-5 minutes on good WiFi

### Step 4: Verify Model Downloaded

```javascript
window.Android.getAvailableModels((models) => {
    const modelList = JSON.parse(models);
    modelList.forEach(m => {
        console.log(`${m.name}: ${m.isDownloaded ? '✅ Downloaded' : '❌ Not downloaded'}`);
    });
});
```

**Expected output:**
```
TinyLlama 1.1B: ✅ Downloaded
Phi-2 2.7B: ❌ Not downloaded
```

### Step 5: Test Course Generation

Now use the app UI:

1. **Navigate to Course Creation** in the app
2. **Fill in the form:**
    - **Website URL** (optional): `https://en.wikipedia.org/wiki/Machine_learning`
    - **Course Title**: "Introduction to Machine Learning"
    - **Difficulty**: "Beginner"
    - **Audience**: "Students"
    - **Prerequisites**: "Basic programming"

3. **Click "Generate Content"**

4. **Wait 10-30 seconds** for AI to generate the course

5. **View the generated course** with lessons and topics!

---

## 🎯 Quick Test (Without UI)

If you want to test AI generation directly from console:

```javascript
// Test course generation
window.Android.generateCourseContent(
    'https://en.wikipedia.org/wiki/Python_(programming_language)',  // URL
    '',                          // extractedText (empty, will scrape from URL)
    'Python Programming',        // title
    'Beginner',                  // difficulty
    'Students',                  // audience
    'None',                      // prerequisites
    function(response) {
        console.log('🎓 Generated Course:', response);
        if (response.success) {
            console.log('✅ Course Title:', response.course.title);
            console.log('📚 Number of Lessons:', response.course.lessons.length);
            console.log('📝 Lessons:', response.course.lessons);
        } else {
            console.log('❌ Error:', response.error);
        }
    }
);
```

**Expected output:**
```
[WebAppInterface] Starting course generation workflow...
[WebAppInterface] Extracting content from URL: https://en.wikipedia.org/wiki/Python_(programming_language)
[WebAppInterface] Checking if AI model is loaded...
[WebAppInterface] Loading model: tinyllama-1.1b
[WebAppInterface] Model loaded successfully!
[WebAppInterface] Generating course with AI...
[WebAppInterface] AI Response received: {...
[WebAppInterface] Course generation completed successfully!

🎓 Generated Course: {
  success: true,
  course_id: "ai-1699876543210",
  course: {
    title: "Python Programming Fundamentals",
    description: "A comprehensive introduction to Python...",
    lessons: [
      {
        title: "Lesson 1: Introduction to Python",
        description: "Learn Python basics...",
        topics: ["What is Python", "Installing Python", "First Program"]
      },
      // ... more lessons
    ]
  }
}
```

---

## 📱 Alternative: Download Model from React App

If your React app has a Settings screen, add this component:

```jsx
// ModelDownloadButton.jsx
import React, { useState } from 'react';

function ModelDownloadButton() {
    const [downloading, setDownloading] = useState(false);
    const [progress, setProgress] = useState(0);

    const handleDownload = () => {
        if (!window.Android?.downloadModel) {
            alert('Not running in Android app');
            return;
        }

        setDownloading(true);
        window.Android.downloadModel(
            'tinyllama-1.1b',
            (prog) => setProgress(prog),
            (success) => {
                setDownloading(false);
                alert(success ? 'Model downloaded!' : 'Download failed');
            }
        );
    };

    return (
        <div style={{ padding: '20px' }}>
            <h3>Download AI Model</h3>
            <p>Required for course generation</p>
            
            {!downloading ? (
                <button 
                    onClick={handleDownload}
                    style={{
                        padding: '10px 20px',
                        fontSize: '16px',
                        background: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: 'pointer'
                    }}
                >
                    Download TinyLlama (637 MB)
                </button>
            ) : (
                <div>
                    <p>Downloading: {progress}%</p>
                    <progress 
                        value={progress} 
                        max="100" 
                        style={{ width: '100%', height: '30px' }}
                    />
                </div>
            )}
        </div>
    );
}

export default ModelDownloadButton;
```

---

## 🐛 Troubleshooting

### Issue: "No downloaded models available"

**Solution:** Follow Step 3 above to download a model

### Issue: Model download stuck at 0%

**Check:**

- Internet connection
- Available storage (need ~1 GB free)
- App storage permissions

**Debug:**

```bash
adb logcat | grep "AIManager\|RunAnywhere\|WebAppInterface"
```

### Issue: Generation takes too long

**Normal:** TinyLlama takes 10-30 seconds
**If longer:**

- Try reloading the model
- Check CPU usage
- Try restarting the app

### Issue: "window.Android is undefined"

**Solution:**

- Make sure you're running in the Android app, not a web browser
- Check WebView debugging is enabled

---

## 📊 What Happens Behind the Scenes

```
User clicks "Generate Content"
    ↓
Android Bridge intercepts request
    ↓
WebAppInterface.generateCourseContent() called
    ↓
1. Extract website content (if URL provided)
    ↓
2. Check if model loaded
    ↓
3. Load model if needed (auto-loads first downloaded model)
    ↓
4. Create structured prompt with course info + content
    ↓
5. Call RunAnywhere SDK: aiManager.generateText(prompt)
    ↓
6. Parse AI response (try JSON, fallback to structured)
    ↓
7. Return course structure to frontend
    ↓
UI displays generated course!
```

---

## 📚 Documentation Files

- **`MODEL_DOWNLOAD_GUIDE.md`** - Detailed guide on downloading and managing models
- **`AI_COURSE_GENERATION.md`** - Technical documentation of the feature
- **`SETUP_SUMMARY.md`** - Implementation summary and testing instructions
- **`QUICK_START.md`** - This file

---

## ✅ Success Checklist

- [ ] App built and installed
- [ ] WebView debugging enabled
- [ ] Chrome DevTools connected
- [ ] At least one model downloaded
- [ ] Model download verified (isDownloaded: true)
- [ ] Course generation tested
- [ ] Generated course has lessons and topics

---

## 🎉 You're Ready!

Once you complete the steps above, your AI course generation feature is fully functional! Users can:

1. Enter a website URL or course topic
2. Provide course details (difficulty, audience, etc.)
3. Click "Generate Content"
4. Get a complete course structure with lessons and topics
5. All processing happens **on-device** - no server required!

Need help? Check the other documentation files or the logs:
```bash
adb logcat | grep -E "WebAppInterface|AIManager|AndroidBridge"
```