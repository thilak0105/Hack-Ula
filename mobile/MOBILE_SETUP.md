# Mobile App Setup with RunAnywhere SDK

This guide will help you convert the Course Creator project into a mobile application using RunAnywhere SDK.

## Architecture Overview

### Hybrid Approach
- **Mobile App**: React Native app with RunAnywhere SDK for on-device AI processing
- **Backend API**: Flask backend for heavy operations (file processing, ChromaDB, file generation)
- **RunAnywhere SDK**: Handles AI requests on-device for privacy and low latency

### Benefits
- ✅ Privacy: Sensitive content processed on-device
- ✅ Low Latency: Sub-200ms responses for chat/queries
- ✅ Cost Savings: Reduced cloud API usage
- ✅ Offline Support: Works without internet for basic features
- ✅ Better UX: Faster, more responsive mobile experience

## Prerequisites

1. **React Native Development Environment**
   - Node.js 16+
   - React Native CLI
   - Android Studio (for Android)
   - Xcode (for iOS - macOS only)

2. **RunAnywhere SDK**
   - iOS SDK: Available from RunAnywhere
   - Android SDK: Available from RunAnywhere
   - API Key from RunAnywhere platform

3. **Backend Server**
   - Keep the Flask backend running for:
     - File processing (PDF, DOCX, PPTX)
     - ChromaDB vector storage
     - PDF/PPTX generation
     - Complex operations

## Project Structure

```
mobile/
├── CourseCreatorApp/          # React Native app
│   ├── src/
│   │   ├── components/         # Mobile UI components
│   │   ├── services/           # API and RunAnywhere SDK services
│   │   ├── screens/            # App screens
│   │   ├── navigation/        # Navigation setup
│   │   └── utils/               # Utilities
│   ├── android/                # Android native code
│   ├── ios/                    # iOS native code
│   └── package.json
├── runanywhere-service/        # RunAnywhere SDK integration
│   ├── ios/                    # iOS SDK wrapper
│   └── android/                # Android SDK wrapper
└── MOBILE_SETUP.md             # This file
```

## Step 1: Initialize React Native Project

```bash
# Install React Native CLI
npm install -g react-native-cli

# Create new React Native project
cd mobile
npx react-native init CourseCreatorApp --template react-native-template-typescript

cd CourseCreatorApp
```

## Step 2: Install Dependencies

```bash
# Core dependencies
npm install @react-navigation/native @react-navigation/stack @react-navigation/bottom-tabs
npm install react-native-screens react-native-safe-area-context
npm install react-native-gesture-handler
npm install axios react-query
npm install react-native-document-picker
npm install react-native-fs
npm install @react-native-async-storage/async-storage

# UI components
npm install react-native-paper react-native-vector-icons
npm install react-native-markdown-display

# For iOS
cd ios && pod install && cd ..
```

## Step 3: RunAnywhere SDK Integration

### iOS Setup

1. **Add RunAnywhere SDK to iOS project**
   ```bash
   cd ios
   # Add RunAnywhere SDK via CocoaPods or manual integration
   # Follow RunAnywhere iOS SDK documentation
   ```

2. **Create iOS Bridge Module**
   - Create `RunAnywhereBridge.swift` and `RunAnywhereBridge.m`
   - Bridge RunAnywhere SDK to React Native

### Android Setup

1. **Add RunAnywhere SDK to Android project**
   ```bash
   cd android
   # Add RunAnywhere SDK to build.gradle
   # Follow RunAnywhere Android SDK documentation
   ```

2. **Create Android Bridge Module**
   - Create `RunAnywhereModule.java`
   - Bridge RunAnywhere SDK to React Native

## Step 4: Configure Backend API

Update your Flask backend to support mobile:

1. **Enable CORS for mobile**
   ```python
   # Already configured in app.py
   CORS(app, resources={r"/*": {"origins": "*"}})
   ```

2. **Add mobile-specific endpoints**
   - Optimize responses for mobile
   - Add mobile authentication if needed

## Step 5: Mobile App Features

### Core Features to Implement

1. **Content Upload**
   - Document picker for PDF, DOCX, PPTX
   - URL input for web content
   - Camera integration for document scanning

2. **Course Generation**
   - Use RunAnywhere SDK for on-device AI processing
   - Fallback to backend API if needed
   - Progress indicators

3. **Learning Path**
   - Mobile-optimized course navigation
   - Swipe gestures
   - Offline lesson viewing

4. **Quiz**
   - Interactive quiz interface
   - On-device quiz generation via RunAnywhere
   - Score tracking

5. **Translation**
   - On-device translation using RunAnywhere
   - Language selection

6. **Text-to-Speech**
   - Native TTS integration
   - Audio playback controls

## Step 6: RunAnywhere SDK Configuration

### Initialize RunAnywhere SDK

```typescript
// src/services/runanywhere.ts
import { NativeModules } from 'react-native';

const { RunAnywhereSDK } = NativeModules;

export class RunAnywhereService {
  static async initialize(apiKey: string) {
    return await RunAnywhereSDK.initialize(apiKey);
  }

  static async processRequest(prompt: string, context?: string) {
    // RunAnywhere intelligently routes to local or cloud
    return await RunAnywhereSDK.processRequest({
      prompt,
      context,
      options: {
        privacyLevel: 'high', // Process sensitive data locally
        maxTokens: 2000,
      }
    });
  }

  static async generateCourse(textChunks: string[], userPrompt: string) {
    const fullText = textChunks.join('\n');
    const prompt = `Generate a course structure from: ${userPrompt}\n\nContent: ${fullText}`;
    
    return await RunAnywhereService.processRequest(prompt);
  }
}
```

## Step 7: API Service Layer

```typescript
// src/services/api.ts
import axios from 'axios';
import { RunAnywhereService } from './runanywhere';

const API_BASE_URL = 'http://your-backend-url:5000';

export class APIService {
  // Use RunAnywhere for AI, backend for file operations
  static async uploadContent(file: any, prompt: string) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('prompt', prompt);
    
    return await axios.post(`${API_BASE_URL}/upload`, formData);
  }

  static async generateCourse(textChunks: string[], prompt: string) {
    // Try RunAnywhere first (on-device)
    try {
      return await RunAnywhereService.generateCourse(textChunks, prompt);
    } catch (error) {
      // Fallback to backend
      return await axios.post(`${API_BASE_URL}/generate-course`, {
        chunks: textChunks,
        prompt
      });
    }
  }

  static async generateQuiz(content: string, topic: string) {
    // Use RunAnywhere for on-device quiz generation
    return await RunAnywhereService.processRequest(
      `Generate a quiz about ${topic} from: ${content}`
    );
  }
}
```

## Step 8: Mobile UI Components

Create mobile-optimized components:

- `CourseUploadScreen.tsx` - File upload interface
- `CourseListScreen.tsx` - Course browsing
- `LearningPathScreen.tsx` - Course navigation
- `LessonScreen.tsx` - Lesson reading
- `QuizScreen.tsx` - Quiz interface
- `SettingsScreen.tsx` - App settings

## Step 9: Testing

```bash
# Run on Android
npm run android

# Run on iOS
npm run ios
```

## Step 10: Build for Production

### Android
```bash
cd android
./gradlew assembleRelease
```

### iOS
```bash
cd ios
# Build via Xcode
```

## Configuration

### Environment Variables

Create `.env` file in mobile app:
```
RUNANYWHERE_API_KEY=your_key_here
BACKEND_API_URL=http://your-backend:5000
```

## Benefits of This Architecture

1. **Privacy**: Course content processed on-device
2. **Performance**: Sub-200ms responses for queries
3. **Cost**: Reduced cloud API usage
4. **Offline**: Basic features work without internet
5. **Scalability**: Backend handles heavy operations
6. **User Experience**: Fast, responsive mobile app

## Next Steps

1. Follow the implementation files created in this directory
2. Set up RunAnywhere SDK account and get API key
3. Configure backend for mobile access
4. Test on both iOS and Android devices
5. Deploy to app stores

## Support

- RunAnywhere SDK Docs: https://docs.runanywhere.ai
- React Native Docs: https://reactnative.dev
- Backend API Docs: See SETUP_GUIDE.md

