# 🚀 AI Features Quick Reference

## All Available AI Methods

### 📚 Course & Lesson Generation

```javascript
// Generate complete course from URL or text
Android.generateCourseContent(url, extractedText, courseTitle, difficulty, audience, prerequisites, callback);

// Generate detailed lesson content
Android.generateLessonContent(lessonTitle, lessonDescription, courseContext, callback);
```

### 🌍 Translation

```javascript
// Translate text to any language
Android.translateText(text, targetLanguage, callback);
// Languages: Spanish, French, German, Chinese, Japanese, etc.
```

### 📝 Summarization

```javascript
// Summarize long content
Android.summarizeText(text, maxLength, callback);
// maxLength: word count (e.g., 100, 200, 500)
```

### 🎯 Quiz Generation

```javascript
// Generate multiple choice quiz
Android.generateQuiz(lessonContent, numQuestions, difficulty, callback);
// difficulty: 'easy', 'medium', 'hard'
// numQuestions: 1-20
```

### 💬 AI Chat Assistant

```javascript
// Chat with AI assistant
Android.chatWithAI(question, context, conversationHistory, callback);
// context: current lesson/course info
// conversationHistory: previous chat messages
```

### 🎴 Flashcard Generation

```javascript
// Generate study flashcards
Android.generateFlashcards(lessonContent, numCards, callback);
// numCards: 5-50
```

### 🧠 Concept Simplification

```javascript
// Simplify complex concepts
Android.simplifyExplanation(concept, targetAge, callback);
// targetAge: '10 year old', 'beginner', 'high school student', etc.
```

### 📚 Study Notes

```javascript
// Generate study notes
Android.generateStudyNotes(lessonContent, format, callback);
// format: 'bullet', 'outline', 'summary'
```

### 🔧 Model Management

```javascript
// Get available models
Android.getAvailableModels(callback);

// Download a model
Android.downloadModel(modelId, progressCallback, completeCallback);

// Load model for use
Android.loadModel(modelId, callback);

// Unload current model
Android.unloadModel(callback);

// Check if model loaded
Android.isModelLoaded(callback);

// Get current model info
Android.getCurrentModel(callback);
```

### ℹ️ AI Status

```javascript
// Get AI SDK status
const status = JSON.parse(Android.getRunAnywhereAIStatus());
```

---

## Quick Usage Examples

### Translation Example

```javascript
Android.translateText('Hello World', 'Spanish', function(response) {
    if (response.success) {
        console.log(response.translatedText); // "Hola Mundo"
    }
});
```

### Quiz Example

```javascript
Android.generateQuiz(lessonContent, 5, 'medium', function(response) {
    if (response.success) {
        response.quiz.forEach(q => {
            console.log(q.question);
            console.log(q.options);
        });
    }
});
```

### Chat Example

```javascript
Android.chatWithAI('What is AI?', '', '', function(response) {
    if (response.success) {
        console.log(response.answer);
    }
});
```

### Flashcards Example

```javascript
Android.generateFlashcards(lessonContent, 10, function(response) {
    if (response.success) {
        response.flashcards.forEach(card => {
            console.log('Front:', card.front);
            console.log('Back:', card.back);
        });
    }
});
```

---

## Response Formats

### Success Response

```json
{
    "success": true,
    "data": "...",
    "message": "Operation completed"
}
```

### Error Response

```json
{
    "success": false,
    "error": "Error message here"
}
```

---

## Feature Availability

✅ All features work **on-device** (offline after model download)  
✅ **Privacy-first** (no data leaves device)  
✅ **Free** (no API costs)  
✅ **Fast** (local inference)

---

## Common Patterns

### Check Android Bridge

```javascript
if (window.Android && window.Android.translateText) {
    // Android bridge available
} else {
    // Fallback for web/testing
}
```

### Show Loading State

```javascript
showLoadingSpinner();
Android.generateQuiz(content, 5, 'medium', function(response) {
    hideLoadingSpinner();
    displayQuiz(response.quiz);
});
```

### Error Handling

```javascript
Android.chatWithAI(question, '', '', function(response) {
    if (response.success) {
        displayAnswer(response.answer);
    } else {
        showError(response.error || 'AI temporarily unavailable');
    }
});
```

---

## Performance Tips

1. **Cache results** when possible
2. **Debounce** frequent calls (chat)
3. **Truncate** very long content (>5000 chars)
4. **Show progress** for long operations
5. **Use mock mode** for quick testing

---

## Testing Checklist

- [ ] Translation works
- [ ] Summarization works
- [ ] Quiz generation works
- [ ] Chat assistant works
- [ ] Flashcards work
- [ ] Simplification works
- [ ] Study notes work
- [ ] Model download works

---

**Need help? Check `NEW_AI_FEATURES.md` for detailed documentation!**
