# AI Course Generation Feature

## Overview

The app now uses the RunAnywhere SDK to generate course content using on-device AI. When you press
the "Generate Content" button, the app will:

1. Extract content from the provided website URL (if any)
2. Check if an AI model is loaded
3. Automatically load a downloaded model if none is loaded
4. Generate a comprehensive course structure using AI
5. Return the generated course with lessons and topics

## How It Works

### Backend (Kotlin)

The `WebAppInterface.kt` provides a new method `generateCourseContent()` that handles the complete
workflow:

```kotlin
@JavascriptInterface
fun generateCourseContent(
    url: String,
    extractedText: String,
    courseTitle: String,
    difficulty: String,
    audience: String,
    prerequisites: String,
    callback: String
)
```

**Workflow:**

1. **Content Extraction**: If a URL is provided, uses Jsoup to scrape the website
2. **Model Loading**: Checks if an AI model is loaded, if not, loads the first available downloaded
   model
3. **Prompt Creation**: Creates a structured prompt with course details and extracted content
4. **AI Generation**: Calls the RunAnywhere SDK to generate course content
5. **Response Parsing**: Attempts to parse JSON from AI response, falls back to structured
   formatting if needed
6. **Return Result**: Sends the generated course back to the frontend

### Frontend (JavaScript)

The `index.html` intercepts course generation requests and routes them to the native Android method:

```javascript
if (isCourseGeneration && window.Android && window.Android.generateCourseContent) {
    window.Android.generateCourseContent(
        url,
        extractedText,
        title,
        difficulty,
        audience,
        prerequisites,
        callbackName
    );
}
```

## Prerequisites

Before using the AI course generation feature, you must:

1. **Download an AI Model**:
    - Go to Settings in the app
    - Download at least one AI model
    - Wait for the download to complete

2. **Provide Course Information**:
    - Course Title (optional if URL provided)
    - Difficulty Level
    - Target Audience
    - Prerequisites

3. **Content Source** (at least one):
    - Website URL (will be scraped automatically)
    - OR pre-extracted text content

## Usage Example

When the user fills out the course creation form and clicks "Generate Content":

1. Form data is collected (title, difficulty, audience, prerequisites, URL)
2. Request is intercepted by the Android bridge
3. `generateCourseContent()` is called with the form data
4. AI processes the information and generates course structure
5. Generated course is displayed in the app

## Response Format

The method returns a JSON object:

```json
{
  "success": true,
  "course_id": "ai-1234567890",
  "extracted_text": "Full text from website...",
  "course": {
    "title": "Introduction to Machine Learning",
    "description": "A comprehensive course covering ML fundamentals...",
    "lessons": [
      {
        "title": "Lesson 1: What is Machine Learning?",
        "description": "Introduction to ML concepts...",
        "topics": ["Definition", "Types of ML", "Applications"]
      }
    ]
  },
  "message": "Course generated successfully using on-device AI"
}
```

## Error Handling

The feature handles various error scenarios:

- **No Model Downloaded**: Returns error asking user to download a model
- **Model Load Failure**: Returns error with details
- **Website Scraping Failed**: Uses provided title/text, proceeds with AI generation
- **AI Generation Error**: Returns error with message
- **Invalid JSON Response**: Fallback to structured formatting

## Performance Notes

- AI generation typically takes 10-60 seconds depending on the model size
- Website extraction adds 1-5 seconds
- Model loading (first time) adds 5-15 seconds
- Larger models produce better quality but are slower

## Technical Details

- **SDK Used**: RunAnywhere SDK (v0.1.3-alpha)
- **Supported Models**: LLaMA-based models via llama.cpp
- **Min SDK**: Android 24 (Android 7.0+)
- **Processing**: All AI inference happens on-device (no internet required for generation)
- **Web Scraping**: Uses Jsoup library for HTML parsing
