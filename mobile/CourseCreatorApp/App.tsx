/**
 * Course Creator Mobile App
 * Main App Component with Navigation
 */

import React, {useEffect, useState} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {Provider as PaperProvider} from 'react-native-paper';
import {StatusBar, StyleSheet} from 'react-native';
import Config from 'react-native-config';

// Screens
import HomeScreen from './src/screens/HomeScreen';
import UploadScreen from './src/screens/UploadScreen';
import CourseListScreen from './src/screens/CourseListScreen';
import LearningPathScreen from './src/screens/LearningPathScreen';
import SettingsScreen from './src/screens/SettingsScreen';

// Services
import {RunAnywhereService} from './src/services/runanywhere';

// Types
export type RootStackParamList = {
  MainTabs: undefined;
  LearningPath: {courseId: string; courseData: any};
  LessonDetail: {lesson: any; courseId: string};
  QuizScreen: {quiz: any};
};

export type TabParamList = {
  Home: undefined;
  Courses: undefined;
  Upload: undefined;
  Settings: undefined;
};

const Stack = createStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator<TabParamList>();

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: true,
        tabBarActiveTintColor: '#6200ee',
        tabBarInactiveTintColor: '#757575',
      }}>
      <Tab.Screen
        name="Home"
        component={HomeScreen}
        options={{title: 'Home'}}
      />
      <Tab.Screen
        name="Courses"
        component={CourseListScreen}
        options={{title: 'My Courses'}}
      />
      <Tab.Screen
        name="Upload"
        component={UploadScreen}
        options={{title: 'Create Course'}}
      />
      <Tab.Screen
        name="Settings"
        component={SettingsScreen}
        options={{title: 'Settings'}}
      />
    </Tab.Navigator>
  );
}

export default function App() {
  const [runanywhereReady, setRunanywhereReady] = useState(false);

  useEffect(() => {
    // Initialize RunAnywhere SDK
    const initRunAnywhere = async () => {
      try {
        const apiKey = Config.RUNANYWHERE_API_KEY || '';
        if (apiKey) {
          const initialized = await RunAnywhereService.initialize(apiKey);
          setRunanywhereReady(initialized);
          if (initialized) {
            console.log('✅ RunAnywhere SDK initialized');
          } else {
            console.log('⚠️ RunAnywhere SDK not available, using backend fallback');
          }
        } else {
          console.log('⚠️ RunAnywhere API key not set, using backend only');
        }
      } catch (error) {
        console.error('RunAnywhere initialization error:', error);
      }
    };

    initRunAnywhere();
  }, []);

  return (
    <PaperProvider>
      <NavigationContainer>
        <StatusBar barStyle="dark-content" />
        <Stack.Navigator
          screenOptions={{
            headerStyle: {
              backgroundColor: '#6200ee',
            },
            headerTintColor: '#fff',
            headerTitleStyle: {
              fontWeight: 'bold',
            },
          }}>
          <Stack.Screen
            name="MainTabs"
            component={MainTabs}
            options={{headerShown: false}}
          />
          <Stack.Screen
            name="LearningPath"
            component={LearningPathScreen}
            options={{title: 'Learning Path'}}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </PaperProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
});


