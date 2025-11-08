# Course Creator Mobile App

React Native mobile application for Course Creator with RunAnywhere SDK integration.

## 🚀 Quick Start

### 1. Install Dependencies

```bash
npm install
```

### 2. iOS Setup

```bash
cd ios
pod install
cd ..
```

### 3. Configure Environment

Copy `.env.example` to `.env` and fill in your API keys:

```bash
cp .env.example .env
```

Edit `.env`:
```
RUNANYWHERE_API_KEY=your_key_here
BACKEND_API_URL=http://localhost:5000
```

### 4. Start Metro Bundler

```bash
npm start
```

### 5. Run on Device/Simulator

**iOS:**
```bash
npm run ios
```

**Android:**
```bash
npm run android
```

## 📱 Features

- ✅ Course creation from documents, URLs, or text
- ✅ On-device AI processing (RunAnywhere SDK)
- ✅ Learning path navigation
- ✅ Interactive quizzes
- ✅ Multi-language support
- ✅ Offline learning

## 🏗️ Project Structure

```
CourseCreatorApp/
├── src/
│   ├── screens/          # App screens
│   │   ├── HomeScreen.tsx
│   │   ├── UploadScreen.tsx
│   │   ├── CourseListScreen.tsx
│   │   ├── LearningPathScreen.tsx
│   │   └── SettingsScreen.tsx
│   └── services/         # API and SDK services
│       ├── api.ts
│       └── runanywhere.ts
├── App.tsx               # Main app component
└── package.json
```

## 🔧 Configuration

### Backend API

Make sure your Flask backend is running:
```bash
cd ../../code_o_clock_remote_retry
python app.py
```

### RunAnywhere SDK

1. Sign up at https://runanywhere.ai
2. Get your API key
3. Add to `.env` file
4. Follow `../IMPLEMENTATION_GUIDE.md` for native SDK integration

## 📚 Documentation

- Setup Guide: `../MOBILE_SETUP.md`
- Implementation: `../IMPLEMENTATION_GUIDE.md`
- Status: `../STATUS.md`

## 🐛 Troubleshooting

### Metro bundler issues
```bash
npm start -- --reset-cache
```

### iOS build issues
```bash
cd ios
pod deintegrate
pod install
cd ..
```

### Android build issues
```bash
cd android
./gradlew clean
cd ..
```

## 📝 Notes

- The app will automatically fallback to backend API if RunAnywhere SDK is not available
- Make sure backend CORS is enabled for mobile access
- Test on real devices for best experience


