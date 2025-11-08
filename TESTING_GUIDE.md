# Testing Guide - How to Run and Check On-Device AI

## ✅ Current Status

Your app is now running on your phone with:
- ✅ RunAnywhere SDK integrated
- ✅ 3 AI models registered
- ✅ JavaScript bridge active
- ✅ Web app loaded

---

## 🧪 Testing Steps

### Step 1: Open Chrome DevTools (Most Important\!)

This is where you'll interact with the AI:

1. **On your computer**, open Google Chrome
2. Go to: `chrome://inspect`
3. You should see your device listed
4. Under "WebView in com.mentora.mobile", click **"inspect"**

A DevTools window will open - this is your control center\!

---

### Step 2: Check if AI is Available

In the **Console** tab of DevTools, type:

```javascript
console.log(window.Android);
```

**Expected output:** You should see an object with all the AI methods

**If you see `undefined`:**
- Make sure you're inspecting the correct WebView
- Try refreshing the page
- Check if app is still running

---

### Step 3: Get Available AI Models

```javascript
window.Android.getAvailableModels(function(modelsJson) {
  const models = JSON.parse(modelsJson);
  console.table(models);
  
  // Save first model ID for next steps
  window.testModelId = models[0].id;
  console.log('Saved model ID:', window.testModelId);
});
```

**Expected output:** Table showing 3 models:
- SmolLM2 360M Q8_0 (119 MB)
- Qwen 2.5 0.5B Instruct Q6_K (374 MB)
- Llama 3.2 1B Instruct Q6_K (815 MB)

---

### Step 4: Download a Model (Start Small\!)

**Download the smallest model first (119 MB):**

```javascript
// Use the model ID we saved earlier
window.Android.downloadModel(
  window.testModelId,
  function(progress) {
    console.log('Download progress: ' + progress + '%');
  },
  function(success) {
    console.log('Download complete\! Success: ' + success);
    if (success) {
      console.log('✅ Model downloaded\! Ready to load.');
    }
  }
);
```

**Expected behavior:**
- You'll see progress updates: 0%, 10%, 20%... 100%
- Takes 20-60 seconds depending on your connection
- Final message: "Download complete\! Success: true"

**Watch on your phone:**
- You might see a download notification
- App should stay responsive

---

### Step 5: Load the Model

After download completes:

```javascript
window.Android.loadModel(window.testModelId, function(success) {
  console.log('Model loaded: ' + success);
  if (success) {
    console.log('🎉 Model is ready for AI generation\!');
    window.modelReady = true;
  }
});
```

**Expected output:** `Model loaded: true`

**This step might take 10-30 seconds** - the model is being loaded into RAM.

---

### Step 6: Test AI Generation\! 🎉

Now the fun part - generate text with AI:

```javascript
window.Android.generateText(
  "Explain what mentoring means in 2 sentences.",
  function(response) {
    console.log('AI Response:');
    console.log(response);
  }
);
```

**Expected output:** A 2-3 sentence explanation about mentoring from the AI\!

**Takes 5-15 seconds** depending on the model and device.

---

### Step 7: Test Streaming (Real-time Generation)

This is even cooler - watch the AI generate text token by token:

```javascript
let fullResponse = '';

window.Android.generateTextStream(
  "Write a short poem about learning",
  function(token) {
    fullResponse += token;
    console.log(token); // Each piece as it's generated
  },
  function(success) {
    console.log('\n✅ Generation complete\!');
    console.log('Full poem:\n' + fullResponse);
  }
);
```

**Expected behavior:**
- You'll see text appear word by word in real-time\!
- Each token prints as it's generated
- Final poem appears at the end

---

### Step 8: Test Course Generation (Advanced)

Generate a course structure:

```javascript
const coursePrompt = `Create a structured course in JSON format about "Introduction to Programming".

Return a JSON object with this structure:
{
  "course": "Course Title",
  "modules": [
    {
      "title": "Module Title",
      "lessons": [
        {
          "title": "Lesson Title",
          "summary": "Brief summary",
          "detail": "Detailed content"
        }
      ]
    }
  ]
}

Return ONLY valid JSON, no other text.`;

window.Android.generateText(coursePrompt, function(response) {
  try {
    // Extract JSON from response
    const jsonMatch = response.match(/\{[\s\S]*\}/);
    if (jsonMatch) {
      const course = JSON.parse(jsonMatch[0]);
      console.log('✅ Course generated\!');
      console.log('Course title:', course.course);
      console.log('Modules:', course.modules.length);
      console.log(JSON.stringify(course, null, 2));
    } else {
      console.log('Response (may need manual parsing):', response);
    }
  } catch (e) {
    console.error('Parse error:', e);
    console.log('Raw response:', response);
  }
});
```

**Expected output:** A structured course with modules and lessons\!

**Note:** This takes longer (30-60 seconds) as it generates more content.

---

## 🔍 Testing Checklist

Copy these into DevTools console one by one:

### ✅ Basic Tests

```javascript
// 1. Check AI is available
console.log('AI available:', typeof window.Android \!== 'undefined');

// 2. Test toast notification
window.Android.showToast('Hello from AI\!');

// 3. Get device info
console.log('Device:', window.Android.getDeviceInfo());

// 4. Check network
console.log('Network available:', window.Android.checkNetworkConnection());
```

### ✅ AI Model Tests

```javascript
// 5. List models
window.Android.getAvailableModels(m => console.table(JSON.parse(m)));

// 6. Check if model is loaded
window.Android.isModelLoaded(loaded => console.log('Model loaded:', loaded));

// 7. Get current model
window.Android.getCurrentModel(m => console.log('Current:', m ? JSON.parse(m) : 'None'));
```

### ✅ AI Generation Tests

```javascript
// 8. Simple generation
window.Android.generateText('Say hello', r => console.log('AI:', r));

// 9. Streaming
let text = '';
window.Android.generateTextStream(
  'Count to 5',
  t => { text += t; console.log(t); },
  s => console.log('Done\!', text)
);

// 10. Chat
window.Android.chat('What is AI?', r => console.log('Chat:', r));
```

---

## 📊 Expected Results Summary

| Test | Expected Result | Time |
|------|----------------|------|
| Check window.Android | Object with methods | Instant |
| Get models | 3 models listed | < 1 sec |
| Download model | Progress 0-100% | 20-60 sec |
| Load model | Success: true | 10-30 sec |
| Simple generation | 1-2 sentences | 5-15 sec |
| Streaming | Real-time tokens | 5-15 sec |
| Course generation | JSON structure | 30-60 sec |

---

## 🐛 Troubleshooting

### Problem: `window.Android` is undefined

**Solutions:**
1. Make sure you're inspecting the correct WebView (`chrome://inspect`)
2. Refresh the page in the WebView
3. Check if app is still running on phone
4. Restart the app: `adb shell am force-stop com.mentora.mobile && adb shell am start -n com.mentora.mobile/.MainActivity`

### Problem: Download fails or hangs

**Solutions:**
1. Check internet connection on phone
2. Make sure you have enough storage space (need 119MB-815MB)
3. Try again - sometimes it times out
4. Check logs: `adb logcat -s MentoraApp:I AIManager:I`

### Problem: Load model fails

**Solutions:**
1. Make sure model is fully downloaded first
2. Close other apps to free RAM
3. Try the smallest model (SmolLM2 360M)
4. Restart the app
5. Check device memory: Models need 150MB-900MB RAM

### Problem: Generation is slow

**Solution:** This is normal for on-device AI\!
- SmolLM2 360M: ~10-15 tokens/second
- Qwen 2.5 0.5B: ~5-10 tokens/second
- Use streaming to see progress in real-time

### Problem: Response is garbled or invalid JSON

**Solutions:**
1. Use clear, structured prompts
2. Explicitly ask for JSON format
3. Use `response.match(/\{[\s\S]*\}/)` to extract JSON
4. Smaller models (SmolLM2) might need simpler prompts
5. Try Qwen 2.5 0.5B for better quality

---

## 📱 Watch the Logs

In another terminal, watch the app logs:

```bash
adb logcat -s MentoraApp:I AIManager:I MainActivity:I
```

You'll see:
```
I/MentoraApp: Mentora Application started
I/MentoraApp: Initializing RunAnywhere SDK...
I/MentoraApp: RunAnywhere SDK initialized successfully
I/AIManager: Model load successful: model-id
I/AIManager: Generated 156 characters
```

---

## 🎯 Success Criteria

You've successfully tested everything when:

- [x] App launches without crashes
- [x] `window.Android` is defined in DevTools
- [x] Can see 3 AI models
- [x] Can download a model (progress updates work)
- [x] Can load a model (returns true)
- [x] Can generate text (gets a response)
- [x] Streaming works (see tokens in real-time)
- [x] Toast notifications work
- [x] No errors in logs

---

## 🚀 Next: Integrate into Your React App

Once basic testing works, update your React components:

```jsx
import React, { useState, useEffect } from 'react';

function AITest() {
  const [models, setModels] = useState([]);
  const [response, setResponse] = useState('');

  useEffect(() => {
    if (window.Android) {
      window.Android.getAvailableModels((json) => {
        setModels(JSON.parse(json));
      });
    }
  }, []);

  const testAI = () => {
    window.Android.generateText(
      'What is mentoring?',
      (resp) => setResponse(resp)
    );
  };

  return (
    <div>
      <h2>AI Test</h2>
      <p>Models: {models.length}</p>
      <button onClick={testAI}>Test AI</button>
      <p>Response: {response}</p>
    </div>
  );
}
```

---

## 📚 Full Testing Script

Copy this entire script into DevTools console for complete testing:

```javascript
// Complete AI Testing Script
console.log('🚀 Starting AI Tests...\n');

// Test 1: Check AI availability
console.log('Test 1: Checking AI availability...');
if (typeof window.Android \!== 'undefined') {
  console.log('✅ AI is available\!');
} else {
  console.error('❌ AI not available');
}

// Test 2: Get models
console.log('\nTest 2: Getting available models...');
window.Android.getAvailableModels(function(json) {
  const models = JSON.parse(json);
  console.log('✅ Found', models.length, 'models:');
  models.forEach(m => {
    console.log(`  - ${m.name} (${m.sizeInMB} MB) - Downloaded: ${m.isDownloaded}`);
  });
  
  // Save first model for testing
  if (models.length > 0) {
    window.testModelId = models[0].id;
    console.log('\n💡 Saved model ID for testing:', window.testModelId);
    console.log('\n📝 Next steps:');
    console.log('1. Download model: window.Android.downloadModel(window.testModelId, (p) => console.log(p + "%"), (s) => console.log("Done:", s))');
    console.log('2. Load model: window.Android.loadModel(window.testModelId, (s) => console.log("Loaded:", s))');
    console.log('3. Test generation: window.Android.generateText("Hello", (r) => console.log(r))');
  }
});

// Test 3: Toast
console.log('\nTest 3: Testing toast notification...');
window.Android.showToast('AI Testing in Progress\!');
console.log('✅ Check your phone for toast message');

console.log('\n🎉 Basic tests complete\! Follow the steps above to test AI generation.');
```

---

**Ready to test? Open `chrome://inspect` and start with the basic tests\!** 🚀
