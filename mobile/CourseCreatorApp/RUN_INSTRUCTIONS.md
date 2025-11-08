# How to Run the Mobile App

## ✅ Dependencies Installed

All npm packages have been installed successfully!

## 🚀 Running the App

### Option 1: Android (Recommended for Windows)

**Prerequisites:**
1. Install Android Studio
2. Set up Android SDK
3. Create an Android Virtual Device (AVD) or connect a physical device

**Steps:**
```bash
# 1. Make sure backend is running (in another terminal)
cd ../../code_o_clock_remote_retry
python app.py

# 2. In this directory, start Metro bundler (if not already running)
npm start

# 3. In another terminal, run Android
npm run android
```

### Option 2: iOS (macOS only)

**Prerequisites:**
1. macOS with Xcode installed
2. CocoaPods installed

**Steps:**
```bash
# 1. Install iOS dependencies
cd ios
pod install
cd ..

# 2. Start Metro bundler
npm start

# 3. Run iOS
npm run ios
```

## 🔧 Current Status

- ✅ Dependencies installed
- ✅ Backend server starting
- ✅ Metro bundler starting
- ⏳ Need Android/iOS setup to run on device/emulator

## 📱 Quick Test

If you have Android Studio set up:

```bash
# Start Metro (already running in background)
# Then in a new terminal:
npm run android
```

## ⚠️ Important Notes

1. **Backend Required**: The Flask backend must be running on `http://localhost:5000`
2. **Android Setup**: You need Android Studio and an emulator/device
3. **Environment**: The `.env` file is created with default backend URL
4. **RunAnywhere SDK**: Optional - app works with backend API fallback

## 🐛 Troubleshooting

### Metro bundler issues
```bash
npm start -- --reset-cache
```

### Android build issues
- Make sure Android Studio is installed
- Check that ANDROID_HOME is set
- Verify emulator is running or device is connected

### Backend connection issues
- Check backend is running: `python app.py`
- Verify CORS is enabled in backend
- Check `.env` has correct BACKEND_API_URL

## 📚 Next Steps

1. Set up Android Studio (if not done)
2. Create/start Android emulator
3. Run `npm run android`
4. Or connect physical Android device via USB

The app is ready to run once you have Android/iOS development environment set up!

