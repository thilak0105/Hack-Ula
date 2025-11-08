# Course Creator Mobile App with RunAnywhere SDK

Convert your Flask-based Course Creator into a powerful mobile application with on-device AI processing.

## 🎯 Overview

This mobile implementation uses **RunAnywhere SDK** to bring AI-powered course creation to iOS and Android devices with:

- ✅ **On-Device AI Processing** - Privacy-first, low-latency AI
- ✅ **Hybrid Architecture** - Smart routing between device and cloud
- ✅ **Offline Support** - Core features work without internet
- ✅ **Cost Efficient** - Reduced cloud API usage
- ✅ **Fast Performance** - Sub-200ms responses for queries

## 📁 Project Structure

```
mobile/
├── MOBILE_SETUP.md              # Complete setup guide
├── IMPLEMENTATION_GUIDE.md      # Step-by-step implementation
├── package.json                 # React Native dependencies
└── src/
    └── services/
        ├── runanywhere.ts       # RunAnywhere SDK service
        └── api.ts               # Backend API service with fallback
```

## 🚀 Quick Start

### 1. Prerequisites

```bash
# Install React Native CLI
npm install -g react-native-cli

# Install dependencies
npm install
```

### 2. Get RunAnywhere SDK

1. Sign up at https://runanywhere.ai
2. Get your API key
3. Download iOS and Android SDKs
4. Follow `IMPLEMENTATION_GUIDE.md` for SDK integration

### 3. Configure

Create `.env` file:
```
RUNANYWHERE_API_KEY=your_key_here
BACKEND_API_URL=http://your-backend:5000
```

### 4. Run

```bash
# iOS
npm run ios

# Android
npm run android
```

## 🏗️ Architecture

### On-Device (RunAnywhere SDK)
- Course structure generation
- Lesson content generation
- Quiz generation
- Text translation
- Content summarization

### Backend (Flask API)
- File processing (PDF, DOCX, PPTX)
- ChromaDB vector storage
- PDF/PPTX generation
- Complex operations

## 📚 Documentation

- **MOBILE_SETUP.md** - Complete setup instructions
- **IMPLEMENTATION_GUIDE.md** - Detailed implementation steps
- **Backend Setup** - See `../SETUP_GUIDE.md`

## 🔧 Key Features

### 1. Intelligent Request Routing
RunAnywhere automatically decides:
- Simple queries → Process on-device
- Complex operations → Route to cloud
- Privacy-sensitive content → Always on-device

### 2. Automatic Fallback
If RunAnywhere is unavailable:
- Automatically falls back to backend API
- Seamless user experience
- No code changes needed

### 3. Privacy-First
- Sensitive course content processed locally
- No data sent to cloud unless necessary
- User privacy protected

## 📱 Mobile Features

- Document upload (PDF, DOCX, PPTX)
- URL content ingestion
- Course generation
- Learning path navigation
- Interactive quizzes
- Text-to-speech
- Multi-language support
- Offline lesson viewing

## 🔐 Security

- API keys stored securely
- On-device processing for sensitive data
- Encrypted communication with backend
- Secure file storage

## 📊 Performance

- **On-Device**: <200ms response time
- **Cloud Fallback**: ~1-3s response time
- **Offline**: Core features available
- **Battery**: Optimized for mobile devices

## 🛠️ Development

### Testing

```bash
# Test RunAnywhere SDK
npm test

# Test on device
npm run ios --device
npm run android --device
```

### Building

```bash
# iOS
cd ios && xcodebuild

# Android
cd android && ./gradlew assembleRelease
```

## 📝 Next Steps

1. Follow `MOBILE_SETUP.md` for initial setup
2. Implement native bridges (see `IMPLEMENTATION_GUIDE.md`)
3. Create mobile UI components
4. Test on real devices
5. Deploy to app stores

## 🤝 Support

- RunAnywhere SDK: https://docs.runanywhere.ai
- React Native: https://reactnative.dev
- Backend API: See `../SETUP_GUIDE.md`

## 📄 License

Same as main project (MIT License)

