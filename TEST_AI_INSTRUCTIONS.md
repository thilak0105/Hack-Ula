# 🧪 Testing AI Features

## Quick Test Access

Your app now has a dedicated test page for all AI features\!

### Option 1: Direct URL (Easy\!)
1. Open the app on your device
2. In Chrome DevTools or browser address bar, navigate to:
   ```
   file:///android_asset/mentora/test-ai-features.html
   ```

### Option 2: Modify MainActivity to load test page temporarily
Change line in MainActivity.kt:
```kotlin
// From:
webView.loadUrl("file:///android_asset/mentora/index.html")

// To:
webView.loadUrl("file:///android_asset/mentora/test-ai-features.html")
```

### Option 3: Use Chrome Remote Debugging (Recommended\!)

1. **Enable USB Debugging** on your Android device
2. **Connect device** to computer via USB
3. **Open Chrome** on your computer
4. **Navigate to:** `chrome://inspect#devices`
5. **Find your device** in the list
6. **Click "inspect"** on the Mentora app
7. **In the Console, type:**
   ```javascript
   window.location.href = 'file:///android_asset/mentora/test-ai-features.html'
   ```

## What You Can Test

The test page has buttons for:

✅ **Translation** - Translate "Hello" to Spanish
✅ **Summarization** - Summarize sample text  
✅ **Quiz Generation** - Generate 3 quiz questions
✅ **Chat Assistant** - Ask "What is AI?"
✅ **Flashcards** - Generate 5 flashcards
✅ **Simplification** - Simplify "Quantum Mechanics"
✅ **Study Notes** - Generate bullet notes
✅ **Test All** - Run all features at once\!

## Expected Results

### If Model is Downloaded:
- All features will show "✅ Success\!"
- Responses will be AI-generated
- May take 10-30 seconds per feature

### If Model NOT Downloaded:
- Features will use simulated responses
- Still shows structure and format
- Instant responses (no AI delay)

## Chrome DevTools Console Testing

You can also test directly in the console:

```javascript
// Test Translation
Android.translateText('Hello World', 'Spanish', function(r) {
    console.log(r.translatedText);
});

// Test Quiz
Android.generateQuiz('Python is a programming language', 3, 'easy', function(r) {
    console.log(r.quiz);
});

// Test Chat
Android.chatWithAI('What is AI?', '', '', function(r) {
    console.log(r.answer);
});

// Check AI Status
console.log(JSON.parse(Android.getRunAnywhereAIStatus()));
```

## Troubleshooting

### "Android.XXX is not a function"
- Make sure you're testing on the actual Android app, not a browser
- Check that the app is properly installed

### "SDK not initialized"
- The AI model needs to be downloaded first
- Go to Settings → Download AI Model
- Or features will use simulated responses

### Features taking too long
- First-time model download is ~1GB
- Subsequent uses are much faster
- Check internet connection for download

## Testing on Emulator vs Real Device

**Emulator:**
- ⚠️ May be slower for AI processing
- ✅ Good for UI testing
- ⚠️ Model download may be slow

**Real Device (Recommended):**
- ✅ Much faster AI processing
- ✅ Better performance
- ✅ More realistic testing

## Quick Command Reference

```bash
# Install app
./gradlew installDebug

# Check connected devices
adb devices

# View logs (helps debug)
adb logcat | grep -i "WebAppInterface\|AIManager"

# Launch app
adb shell am start -n com.mentora.mobile/.MainActivity
```

## What to Look For

✅ **All 8 features** should be accessible
✅ **Error handling** should show friendly messages
✅ **Loading states** should appear during processing
✅ **Results** should be formatted nicely
✅ **AI Status** should show feature list

---

**Ready to test? Open the app and use Chrome Remote Debugging\!** 🚀
