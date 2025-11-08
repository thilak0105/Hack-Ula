# EMERGENCY SIMPLE TEST - Check if SDK Works

## Test 1: Is Android object available?

In Chrome DevTools console:

```javascript
console.log(typeof window.Android);
console.log(Object.keys(window.Android || {}));
```

Expected: Should show "object" and list of functions

## Test 2: Can we get models?

```javascript
// First, create a global callback function
window.testCallback = function(models) {
    console.log('MODELS:', models);
};

// Then call with the function NAME as string
window.Android.getAvailableModels('testCallback');
```

Expected: Should show list of models

## Test 3: Download with named callbacks

```javascript
// Create named callbacks on window
window.progressCB = function(p) {
    console.log('Progress:', p + '%');
};

window.completeCB = function(success) {
    console.log('Complete:', success);
};

// Call with callback NAMES as strings
window.Android.downloadModel('tinyllama-1.1b', 'progressCB', 'completeCB');
```

This should work if the SDK is functional.
