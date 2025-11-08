# Quick Fix - Start Backend Server

## Problem
Your React app needs the Python Flask backend to work. The "Next" button and other features make API calls to `localhost:5000`.

## Temporary Solution (For Testing)

### Step 1: Start the Backend

```bash
cd /tmp/Code-O-Clock
python app.py
```

This starts the server on `http://localhost:5000`

### Step 2: Update React App to Use Computer's IP

The app can't access `localhost` on your phone. You need to update the API endpoint to your computer's IP.

**Find your computer's IP:**
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

Example output: `inet 192.168.1.100`

### Step 3: Enable Port Forwarding

Since the React app is built and in assets, we can use ADB port forwarding:

```bash
# Forward phone's localhost:5000 to computer's localhost:5000
adb reverse tcp:5000 tcp:5000
```

Now your phone's `localhost:5000` will reach your computer's Flask server\!

### Step 4: Test

1. Make sure Flask is running: `python app.py`
2. Run port forwarding: `adb reverse tcp:5000 tcp:5000`
3. Open the app on your phone
4. Click "Next" - it should work now\!

## Better Solution: Use On-Device AI

Instead of relying on the backend, update your React app to use `window.Android` methods for AI processing. See `HYBRID_ARCHITECTURE_GUIDE.md` for details.

