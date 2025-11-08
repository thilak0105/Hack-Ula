# RunAnywhere SDK Implementation Guide

## Overview

This guide provides step-by-step instructions for integrating RunAnywhere SDK into the Course Creator mobile app.

## Architecture Decision

### What Runs On-Device (RunAnywhere SDK)
- ✅ Course structure generation
- ✅ Lesson content generation
- ✅ Quiz generation
- ✅ Text translation
- ✅ Content summarization
- ✅ Simple queries and chat

### What Runs on Backend
- ✅ File processing (PDF, DOCX, PPTX extraction)
- ✅ ChromaDB vector storage and search
- ✅ PDF/PPTX document generation
- ✅ Complex file operations
- ✅ YouTube/website content extraction

## Step-by-Step Implementation

### 1. Get RunAnywhere SDK

1. Sign up at https://runanywhere.ai
2. Get your API key
3. Download iOS and Android SDKs
4. Follow RunAnywhere documentation for SDK installation

### 2. iOS Integration

#### Add SDK to iOS Project

```bash
cd mobile/CourseCreatorApp/ios
# Follow RunAnywhere iOS SDK installation guide
```

#### Create Native Bridge

**RunAnywhereBridge.h**
```objc
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RunAnywhereSDK, NSObject)

RCT_EXTERN_METHOD(initialize:(NSString *)apiKey
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(processRequest:(NSDictionary *)options
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

@end
```

**RunAnywhereBridge.m**
```objc
#import "RunAnywhereBridge.h"
#import <RunAnywhereSDK/RunAnywhereSDK.h>

@implementation RunAnywhereSDK

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(initialize:(NSString *)apiKey
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    // Initialize RunAnywhere SDK
    // Follow RunAnywhere iOS SDK documentation
    BOOL success = [RunAnywhere initializeWithAPIKey:apiKey];
    resolve(@(success));
}

RCT_EXPORT_METHOD(processRequest:(NSDictionary *)options
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    // Process request using RunAnywhere SDK
    // Follow RunAnywhere iOS SDK documentation
    NSString *prompt = options[@"prompt"];
    NSString *context = options[@"context"];
    
    [RunAnywhere processRequest:prompt
                        context:context
                     completion:^(NSDictionary *response, NSError *error) {
        if (error) {
            reject(@"error", error.localizedDescription, error);
        } else {
            resolve(response);
        }
    }];
}

@end
```

### 3. Android Integration

#### Add SDK to Android Project

```bash
cd mobile/CourseCreatorApp/android
# Add RunAnywhere SDK to build.gradle
```

**build.gradle**
```gradle
dependencies {
    implementation 'ai.runanywhere:sdk:1.0.0'
    // ... other dependencies
}
```

#### Create Native Bridge

**RunAnywhereModule.java**
```java
package com.coursecreator;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import ai.runanywhere.RunAnywhere;
import ai.runanywhere.RunAnywhereCallback;

public class RunAnywhereModule extends ReactContextBaseJavaModule {
    
    public RunAnywhereModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RunAnywhereSDK";
    }

    @ReactMethod
    public void initialize(String apiKey, Promise promise) {
        try {
            RunAnywhere.initialize(apiKey, getReactApplicationContext());
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("INIT_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void processRequest(ReadableMap options, Promise promise) {
        try {
            String prompt = options.getString("prompt");
            String context = options.getString("context");
            
            RunAnywhere.processRequest(prompt, context, new RunAnywhereCallback() {
                @Override
                public void onSuccess(String response, boolean processedLocally) {
                    WritableMap result = Arguments.createMap();
                    result.putString("content", response);
                    result.putBoolean("processedLocally", processedLocally);
                    promise.resolve(result);
                }

                @Override
                public void onError(String error) {
                    promise.reject("PROCESS_ERROR", error);
                }
            });
        } catch (Exception e) {
            promise.reject("PROCESS_ERROR", e.getMessage());
        }
    }
}
```

**RunAnywherePackage.java**
```java
package com.coursecreator;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunAnywherePackage implements ReactPackage {
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RunAnywhereModule(reactContext));
        return modules;
    }
}
```

### 4. Initialize RunAnywhere in React Native

**App.tsx**
```typescript
import React, { useEffect } from 'react';
import { RunAnywhereService } from './src/services/runanywhere';
import Config from 'react-native-config';

export default function App() {
  useEffect(() => {
    // Initialize RunAnywhere SDK on app start
    const initRunAnywhere = async () => {
      const apiKey = Config.RUNANYWHERE_API_KEY;
      if (apiKey) {
        await RunAnywhereService.initialize(apiKey);
      }
    };
    
    initRunAnywhere();
  }, []);

  // ... rest of app
}
```

### 5. Use RunAnywhere in Components

**CourseGenerationScreen.tsx**
```typescript
import React, { useState } from 'react';
import { View, Text, Button } from 'react-native';
import { RunAnywhereService } from '../services/runanywhere';
import APIService from '../services/api';

export default function CourseGenerationScreen() {
  const [loading, setLoading] = useState(false);
  const [course, setCourse] = useState(null);

  const generateCourse = async (textChunks: string[], prompt: string) => {
    setLoading(true);
    try {
      // APIService will automatically use RunAnywhere if available
      const result = await APIService.generateCourse(textChunks, prompt);
      setCourse(result.course);
      
      if (result.source === 'runanywhere') {
        console.log('✅ Course generated on-device via RunAnywhere');
      } else {
        console.log('✅ Course generated via backend API');
      }
    } catch (error) {
      console.error('Course generation failed:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View>
      {/* UI components */}
    </View>
  );
}
```

## Configuration

### Environment Variables

Create `.env` file:
```
RUNANYWHERE_API_KEY=your_runanywhere_api_key
BACKEND_API_URL=http://your-backend:5000
```

### Privacy Settings

RunAnywhere SDK automatically routes requests based on:
- **Privacy level**: High privacy = local processing
- **Content complexity**: Simple queries = local, complex = cloud
- **Device capabilities**: Available resources

## Testing

### Test On-Device Processing

```typescript
// Test RunAnywhere SDK
const testRunAnywhere = async () => {
  await RunAnywhereService.initialize('your-api-key');
  
  const response = await RunAnywhereService.processRequest(
    'Generate a simple course about Python',
    '',
    { privacyLevel: 'high' }
  );
  
  console.log('Processed locally:', response.processedLocally);
  console.log('Latency:', response.latency, 'ms');
};
```

### Test Fallback

```typescript
// Test backend fallback
const testFallback = async () => {
  // Disable RunAnywhere or use invalid API key
  const result = await APIService.generateCourse(chunks, prompt);
  // Should fallback to backend
};
```

## Performance Optimization

1. **Cache Results**: Store generated content locally
2. **Batch Requests**: Combine multiple requests when possible
3. **Preload Models**: Initialize RunAnywhere early
4. **Monitor Latency**: Track on-device vs cloud performance

## Troubleshooting

### SDK Not Initializing
- Check API key is correct
- Verify SDK is properly installed
- Check native module bridge is correct

### Requests Always Going to Cloud
- Check device has sufficient resources
- Verify privacy level settings
- Check RunAnywhere SDK logs

### Performance Issues
- Monitor device memory usage
- Optimize prompt sizes
- Use appropriate maxTokens settings

## Next Steps

1. Complete native bridge implementation
2. Test on real devices (iOS and Android)
3. Optimize prompts for mobile
4. Add error handling and retry logic
5. Implement caching strategy
6. Monitor and analyze performance

