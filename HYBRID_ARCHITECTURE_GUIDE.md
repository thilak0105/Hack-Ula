# Hybrid Architecture Guide - On-Device AI + Cloud Backend

## 🎯 Current vs New Architecture

### ❌ OLD Architecture (Code-O-Clock)
```
React Frontend → Flask Backend (localhost:5000) → Groq/Gemini APIs → AI Response
```
**Problems:**
- Requires API keys (Groq, Gemini)
- Requires internet for AI
- Privacy concerns (data sent to cloud)
- API costs
- Network latency

### ✅ NEW Architecture (MentoraMobile)
```
React Frontend → Android WebView → RunAnywhere SDK → On-Device AI → Response
                       ↓
              Cloud Backend (Optional)
              - Data storage only
              - Course content sync
              - User progress tracking
```

**Benefits:**
- ✅ No API keys needed for AI
- ✅ Works fully offline
- ✅ 100% privacy (AI runs locally)
- ✅ No AI costs
- ✅ Faster responses (no network latency)
- ✅ Cloud only for data, not AI

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                  React Web App (Frontend)                    │
│  ┌────────────┐  ┌────────────┐  ┌─────────────────┐       │
│  │ Chat UI    │  │ Course UI  │  │  Quiz Generator │       │
│  └─────┬──────┘  └─────┬──────┘  └────────┬────────┘       │
└────────┼─────────────────┼──────────────────┼───────────────┘
         │                 │                  │
         │                 │                  │
         │ window.Android  │                  │
         ▼                 ▼                  ▼
┌────────────────────────────────────────────────────────────┐
│              Android WebAppInterface (Bridge)               │
│  ┌────────────┐  ┌────────────┐  ┌─────────────────┐      │
│  │ AI Methods │  │ Storage    │  │  Device Info    │      │
│  └─────┬──────┘  └─────┬──────┘  └────────┬────────┘      │
└────────┼─────────────────┼──────────────────┼───────────────┘
         │                 │                  │
         ▼                 ▼                  ▼
┌─────────────────┐  ┌────────────────┐  ┌──────────────┐
│   AIManager.kt  │  │ PreferencesMgr │  │ NetworkUtil  │
└────────┬────────┘  └────────────────┘  └──────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│            RunAnywhere SDK (On-Device AI)                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ Model Mgmt   │  │ Text Gen     │  │ LlamaCpp     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│         AI Models (Downloaded Locally, 119MB-815MB)      │
│  SmolLM2 360M  |  Qwen 2.5 0.5B  |  Llama 3.2 1B       │
└─────────────────────────────────────────────────────────┘

           (Optional - For data storage only)
                       ▼
┌─────────────────────────────────────────────────────────┐
│              Cloud Backend (Python Flask)                │
│  ┌────────────┐  ┌────────────┐  ┌─────────────────┐   │
│  │ Course DB  │  │ User Data  │  │  Progress Track │   │
│  └────────────┘  └────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 API Replacement Strategy

### Old Code-O-Clock Functions → New RunAnywhere Implementation

| Old Function (Groq/Gemini) | New Function (RunAnywhere) | Purpose |
|----------------------------|----------------------------|---------|
| `generate_course()` | `window.Android.generateText()` | Course generation |
| `generate_lesson_content()` | `window.Android.generateTextStream()` | Lesson creation |
| `modify_content()` | `window.Android.chat()` | Content modification |
| Backend AI API calls | On-device AI processing | All AI inference |

---

## 💻 Implementation Examples

### Example 1: Generate Course Content

**OLD (Code-O-Clock with APIs):**
```javascript
// React frontend calls Python backend
fetch('http://localhost:5000/generate_course', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    text_chunks: chunks,
    user_prompt: prompt
  })
})
.then(res => res.json())
.then(data => {
  // Data comes from Groq/Gemini API
  displayCourse(data);
});
```

**NEW (MentoraMobile with On-Device AI):**
```javascript
// Direct on-device AI processing
const prompt = `Create a structured course in JSON format:
User Request: ${userPrompt}
Content: ${chunks.join('\n')}

Return JSON with this structure:
{
  "course": "Course Title",
  "modules": [
    {
      "title": "Module Title",
      "lessons": [
        {"title": "...", "summary": "...", "detail": "..."}
      ]
    }
  ]
}`;

window.Android.generateText(prompt, (response) => {
  try {
    const courseData = JSON.parse(response);
    displayCourse(courseData);
  } catch (e) {
    console.error('Failed to parse course:', e);
  }
});
```

### Example 2: Generate Lesson Content (Streaming)

**OLD:**
```javascript
fetch('http://localhost:5000/generate_lesson', {
  method: 'POST',
  body: JSON.stringify({
    lesson_title: title,
    lesson_summary: summary,
    context_chunks: context
  })
})
.then(res => res.text())
.then(content => displayLesson(content));
```

**NEW (With Real-time Streaming):**
```javascript
const prompt = `Create a comprehensive lesson about: ${title}
Summary: ${summary}
Context: ${context.join('\n')}

Write a detailed lesson (500-800 words) with:
- Clear introduction
- Key concepts
- Real-world examples
- Practical applications
- Key takeaways`;

let lessonContent = '';

window.Android.generateTextStream(
  prompt,
  (token) => {
    // Real-time token streaming\!
    lessonContent += token;
    updateLessonDisplay(lessonContent);
  },
  (success) => {
    if (success) {
      finalizeLessonDisplay(lessonContent);
    }
  }
);
```

### Example 3: Modify Content Based on Feedback

**OLD:**
```javascript
fetch('http://localhost:5000/modify_content', {
  method: 'POST',
  body: JSON.stringify({
    content_type: 'lesson',
    original_content: originalLesson,
    modification_prompt: feedback
  })
})
.then(res => res.json())
.then(modified => updateContent(modified));
```

**NEW:**
```javascript
const prompt = `Modify the following lesson based on feedback:

Original Lesson:
${JSON.stringify(originalLesson, null, 2)}

Trainer's Feedback:
${feedback}

Return the modified lesson in JSON format with the same structure.`;

window.Android.generateText(prompt, (response) => {
  try {
    const modifiedLesson = JSON.parse(response);
    updateContent(modifiedLesson);
  } catch (e) {
    console.error('Failed to parse modified content:', e);
  }
});
```

---

## 🔐 Cloud Backend Role (Optional)

The cloud backend is NOW ONLY for data storage, NOT for AI:

```python
# NEW backend.py (No AI processing\!)

from flask import Flask, jsonify, request
import json

app = Flask(__name__)

# Storage only - NO AI processing\!
courses_db = {}
user_progress = {}

@app.route('/courses', methods=['GET'])
def get_courses():
    """Just return stored courses"""
    return jsonify(list(courses_db.values()))

@app.route('/courses', methods=['POST'])
def save_course():
    """Just save course data"""
    course_data = request.json
    course_id = course_data.get('id')
    courses_db[course_id] = course_data
    return jsonify({'success': True, 'id': course_id})

@app.route('/progress/<user_id>', methods=['GET', 'POST'])
def handle_progress(user_id):
    """Track user progress"""
    if request.method == 'GET':
        return jsonify(user_progress.get(user_id, {}))
    else:
        user_progress[user_id] = request.json
        return jsonify({'success': True})

# NO AI GENERATION ENDPOINTS\!
# All AI processing happens on-device\!

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

---

## 📱 React Component Example

Complete example with on-device AI:

```jsx
import React, { useState, useEffect } from 'react';

function CourseGenerator() {
  const [models, setModels] = useState([]);
  const [selectedModel, setSelectedModel] = useState(null);
  const [isModelLoaded, setIsModelLoaded] = useState(false);
  const [courseContent, setCourseContent] = useState('');
  const [generatedCourse, setGeneratedCourse] = useState(null);
  const [isGenerating, setIsGenerating] = useState(false);

  // Check if running on Android
  const isAndroid = typeof window.Android \!== 'undefined';

  useEffect(() => {
    if (isAndroid) {
      // Load available AI models
      window.Android.getAvailableModels((modelsJson) => {
        const modelsList = JSON.parse(modelsJson);
        setModels(modelsList);
      });
    }
  }, [isAndroid]);

  const downloadModel = (modelId) => {
    window.Android.downloadModel(
      modelId,
      (progress) => {
        console.log(`Download progress: ${progress}%`);
        // Update UI with progress
      },
      (success) => {
        if (success) {
          alert('Model downloaded\! Now loading...');
          loadModel(modelId);
        }
      }
    );
  };

  const loadModel = (modelId) => {
    window.Android.loadModel(modelId, (success) => {
      if (success) {
        setIsModelLoaded(true);
        setSelectedModel(modelId);
        alert('Model loaded and ready\!');
      }
    });
  };

  const generateCourse = () => {
    if (\!isModelLoaded) {
      alert('Please download and load a model first');
      return;
    }

    if (\!courseContent.trim()) {
      alert('Please enter some content');
      return;
    }

    setIsGenerating(true);

    const prompt = `Create a structured course in JSON format from this content:

Content:
${courseContent}

Return a JSON object with this structure:
{
  "course": "Course Title",
  "modules": [
    {
      "title": "Module Title",
      "lessons": [
        {
          "title": "Lesson Title",
          "summary": "Brief summary (max 50 words)",
          "detail": "Detailed explanation (400-600 words)"
        }
      ]
    }
  ]
}

Respond ONLY with valid JSON, no other text.`;

    window.Android.generateText(prompt, (response) => {
      setIsGenerating(false);
      try {
        // Try to extract JSON from response
        const jsonMatch = response.match(/\{[\s\S]*\}/);
        if (jsonMatch) {
          const courseData = JSON.parse(jsonMatch[0]);
          setGeneratedCourse(courseData);
          
          // Optionally save to cloud backend
          saveToCloud(courseData);
        } else {
          alert('Failed to parse course. Response:\n' + response);
        }
      } catch (e) {
        alert('Error parsing course: ' + e.message);
        console.error('Response:', response);
      }
    });
  };

  const saveToCloud = async (courseData) => {
    try {
      // Optional: Save to cloud for backup/sync
      const response = await fetch('https://your-server.com/courses', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(courseData)
      });
      
      if (response.ok) {
        console.log('Course saved to cloud');
      }
    } catch (e) {
      console.error('Failed to save to cloud:', e);
      // Still works offline\!
    }
  };

  if (\!isAndroid) {
    return <div>Please run on Android device to use AI features</div>;
  }

  return (
    <div className="course-generator">
      <h2>AI Course Generator</h2>

      {/* Model Selection */}
      <div className="model-section">
        <h3>Select AI Model</h3>
        {models.map(model => (
          <div key={model.id} className="model-card">
            <h4>{model.name}</h4>
            <p>Size: {model.sizeInMB} MB</p>
            
            {\!model.isDownloaded ? (
              <button onClick={() => downloadModel(model.id)}>
                Download
              </button>
            ) : model.id === selectedModel && isModelLoaded ? (
              <span>✓ Loaded</span>
            ) : (
              <button onClick={() => loadModel(model.id)}>
                Load
              </button>
            )}
          </div>
        ))}
      </div>

      {/* Course Generation */}
      {isModelLoaded && (
        <div className="generation-section">
          <h3>Generate Course</h3>
          <textarea
            value={courseContent}
            onChange={(e) => setCourseContent(e.target.value)}
            placeholder="Enter content to generate course from..."
            rows={10}
            style={{ width: '100%' }}
          />
          <button 
            onClick={generateCourse}
            disabled={isGenerating}
          >
            {isGenerating ? 'Generating...' : 'Generate Course'}
          </button>
        </div>
      )}

      {/* Display Generated Course */}
      {generatedCourse && (
        <div className="course-display">
          <h3>Generated Course: {generatedCourse.course}</h3>
          {generatedCourse.modules.map((module, idx) => (
            <div key={idx} className="module">
              <h4>Module {idx + 1}: {module.title}</h4>
              {module.lessons.map((lesson, lessonIdx) => (
                <div key={lessonIdx} className="lesson">
                  <h5>{lesson.title}</h5>
                  <p><strong>Summary:</strong> {lesson.summary}</p>
                  <p>{lesson.detail}</p>
                </div>
              ))}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default CourseGenerator;
```

---

## 🔄 Migration Checklist

### Phase 1: On-Device AI (Current Status ✅)
- [x] RunAnywhere SDK integrated
- [x] AI models registered
- [x] JavaScript bridge created
- [x] Basic AI methods working

### Phase 2: Replace API Calls (Next Steps)
- [ ] Update React components to use `window.Android` instead of `fetch()`
- [ ] Replace course generation logic
- [ ] Replace lesson generation logic
- [ ] Replace content modification logic
- [ ] Add proper JSON parsing and error handling

### Phase 3: Cloud Backend (Optional)
- [ ] Create simplified backend (data storage only)
- [ ] Remove AI processing from backend
- [ ] Add course storage endpoints
- [ ] Add user progress tracking
- [ ] Deploy to cloud (Heroku, Railway, or similar)

### Phase 4: Testing
- [ ] Test course generation on-device
- [ ] Test lesson generation with streaming
- [ ] Test content modification
- [ ] Test offline functionality
- [ ] Test cloud sync (if implemented)

---

## 💡 Best Practices

### 1. Prompt Engineering for On-Device AI

On-device models need clear, structured prompts:

```javascript
// ✅ GOOD: Clear, structured prompt
const prompt = `Create a course outline in JSON format.

Topic: ${topic}

Required JSON structure:
{
  "course": "Title",
  "modules": [{"title": "...", "lessons": [...]}]
}

Return ONLY JSON, no other text.`;

// ❌ BAD: Vague prompt
const prompt = `Make a course about ${topic}`;
```

### 2. Handle JSON Responses

Always handle parsing errors:

```javascript
window.Android.generateText(prompt, (response) => {
  try {
    // Try to extract JSON if model adds extra text
    const jsonMatch = response.match(/\{[\s\S]*\}/);
    const json = jsonMatch ? JSON.parse(jsonMatch[0]) : JSON.parse(response);
    handleSuccess(json);
  } catch (e) {
    console.error('Parse error:', e);
    console.log('Raw response:', response);
    handleError(e);
  }
});
```

### 3. Use Streaming for Better UX

For long-form content, use streaming:

```javascript
let fullResponse = '';

window.Android.generateTextStream(
  prompt,
  (token) => {
    fullResponse += token;
    // Update UI in real-time
    updateDisplay(fullResponse);
  },
  (success) => {
    if (success) {
      processComplete(fullResponse);
    }
  }
);
```

### 4. Progressive Enhancement

Support both on-device and cloud:

```javascript
function generateCourse(content) {
  if (window.Android && isModelLoaded) {
    // Use on-device AI
    return generateCourseOnDevice(content);
  } else {
    // Fallback to cloud API (if available)
    return generateCourseViaAPI(content);
  }
}
```

---

## 📊 Performance Comparison

| Aspect | Old (Cloud APIs) | New (On-Device) |
|--------|------------------|-----------------|
| **Privacy** | ❌ Data sent to cloud | ✅ 100% local |
| **Cost** | ❌ API costs | ✅ Free |
| **Internet** | ❌ Required | ✅ Works offline |
| **Latency** | ❌ 2-5 seconds | ✅ < 1 second |
| **Speed (tokens/s)** | ~30-50 | ~5-15 |
| **Quality** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Setup** | ❌ API keys | ✅ One-time download |

---

## 🎯 Summary

**What Changed:**
- ❌ **Removed:** Groq API, Gemini API, external API calls
- ✅ **Added:** RunAnywhere SDK, on-device AI processing
- ✅ **Result:** Privacy-first, offline-capable, zero AI costs

**Architecture:**
- **Frontend:** React (unchanged)
- **Bridge:** `window.Android` JavaScript interface
- **AI:** On-device with RunAnywhere SDK
- **Backend (Optional):** Data storage only, NO AI processing

**Next Steps:**
1. Update React components to use `window.Android` methods
2. Test on-device course generation
3. Optionally deploy simplified cloud backend for data storage
4. Share with team\!

---

**Your app is now ready for privacy-first, on-device AI\!** 🎉
