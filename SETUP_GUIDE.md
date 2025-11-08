# MentoraMobile - Setup Guide for Team

This is the Android mobile application for Code-O-Clock, built with Kotlin and integrating the
Code-O-Clock web frontend.

## 📋 Prerequisites

- **Android Studio** (latest version - Arctic Fox or newer)
- **JDK 17** or higher
- **Android device** or emulator (API 24+, Android 7.0+)
- **Git**

## 🚀 Quick Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd MentoraMobile
```

### 2. Open in Android Studio

1. Open Android Studio
2. Select **"Open"**
3. Navigate to the `MentoraMobile` folder
4. Click **"Open"**
5. Wait for Gradle sync (2-3 minutes first time)

### 3. Connect Your Device

**Physical Device:**

- Enable Developer Options (Settings → About → Tap Build Number 7 times)
- Enable USB Debugging
- Connect via USB
- Accept USB debugging prompt

**Or use an Emulator:**

- Device Manager → Create Device → Pixel 6 → API 33+

### 4. Run the App

Click the green **Run** button ▶️ in Android Studio

**Or via terminal:**

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📁 Project Structure

```
MentoraMobile/
├── app/
│   ├── src/main/
│   │   ├── assets/mentora/           # Code-O-Clock web app (React build)
│   │   ├── java/com/mentora/mobile/
│   │   │   ├── MainActivity.kt       # Main WebView activity
│   │   │   ├── MentoraApplication.kt # App initialization
│   │   │   ├── WebAppInterface.kt    # JavaScript bridge
│   │   │   ├── SplashActivity.kt     # Splash screen
│   │   │   └── utils/                # Utility classes
│   │   ├── res/                      # Android resources
│   │   └── AndroidManifest.xml       # App configuration
│   └── build.gradle                  # App dependencies
├── build.gradle                      # Project configuration
├── settings.gradle                   # Gradle settings
└── README.md                         # Project documentation
```

---

## 🔄 Updating the Web App

To update the Code-O-Clock web frontend:

### Step 1: Build the React App

```bash
cd <your-code-o-clock-frontend>
npm install
CI=false npm run build
```

### Step 2: Copy Build Files

```bash
# Remove old files
rm -rf MentoraMobile/app/src/main/assets/mentora/*

# Copy new build
cp -r build/* MentoraMobile/app/src/main/assets/mentora/
```

### Step 3: Fix Paths in index.html

Open `app/src/main/assets/mentora/index.html` and ensure all paths are relative:

- Change `/static/` to `./static/`
- Change `/favicon.ico` to `./favicon.ico`
- etc.

### Step 4: Rebuild Android App

```bash
cd MentoraMobile
./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚠️ Important Notes

### Backend Requirements

The Code-O-Clock app requires a backend server running. The React app makes API calls to:

- `http://localhost:5000/*`

**For mobile to work, you need to:**

1. **Update API endpoints** in the React code to use your computer's IP or cloud server
2. **Start the backend server** before testing features

Example: Change `localhost:5000` to your computer's IP like `192.168.1.100:5000`

### JavaScript Bridge

The app exposes native Android methods to JavaScript via `window.Android`:

```javascript
// Check if running on Android
if (window.Android) {
  // Show toast
  window.Android.showToast("Hello!");
  
  // Get device info
  const device = window.Android.getDeviceInfo();
}
```

---

## 🐛 Troubleshooting

### Gradle Sync Fails

- Check internet connection
- File → Invalidate Caches and Restart

### App Shows Blank White Screen

- Check `index.html` has relative paths (`./` not `/`)
- Check Logcat for JavaScript errors
- Ensure backend server is running and accessible

### Build Fails

```bash
./gradlew clean
./gradlew assembleDebug
```

### Device Not Detected

```bash
adb kill-server
adb start-server
adb devices
```

---

## 📦 Building Release APK

```bash
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/release/app-release.apk`

---

## 🎯 Next Steps

1. Update API endpoints to use server IP instead of localhost
2. Add app icons (launcher icons)
3. Configure app signing for release
4. Test all features with backend
5. Optimize performance

---

## 👥 Team Collaboration

### Making Changes

```bash
# Create a branch
git checkout -b feature/your-feature

# Make changes, commit
git add .
git commit -m "Description of changes"

# Push to remote
git push origin feature/your-feature
```

### Pulling Updates

```bash
git pull origin main
./gradlew clean build
```

---

## 📞 Support

For issues:

- Check this guide
- Review Android Studio Logcat
- Ask team members

---

**Happy Coding!** 🚀
