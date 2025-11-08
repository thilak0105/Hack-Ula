/**
 * Learning Path Screen
 * Display course modules and lessons
 */

import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
} from 'react-native';
import {Card, ActivityIndicator} from 'react-native-paper';
import {useRoute, useNavigation} from '@react-navigation/native';

export default function LearningPathScreen() {
  const route = useRoute();
  const navigation = useNavigation();
  const {courseId, courseData} = route.params as any;
  const [expandedModules, setExpandedModules] = useState<Set<number>>(
    new Set([0]),
  );

  const toggleModule = (index: number) => {
    const newExpanded = new Set(expandedModules);
    if (newExpanded.has(index)) {
      newExpanded.delete(index);
    } else {
      newExpanded.add(index);
    }
    setExpandedModules(newExpanded);
  };

  if (!courseData) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#6200ee" />
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.courseTitle}>{courseData.course || 'Course'}</Text>
        <Text style={styles.moduleCount}>
          {courseData.modules?.length || 0} Modules
        </Text>
      </View>

      <View style={styles.content}>
        {courseData.modules?.map((module: any, moduleIndex: number) => (
          <Card key={moduleIndex} style={styles.moduleCard}>
            <TouchableOpacity
              onPress={() => toggleModule(moduleIndex)}
              style={styles.moduleHeader}>
              <View style={styles.moduleHeaderContent}>
                <Text style={styles.moduleTitle}>{module.title}</Text>
                <Text style={styles.lessonCount}>
                  {module.lessons?.length || 0} lessons
                </Text>
              </View>
              <Text style={styles.expandIcon}>
                {expandedModules.has(moduleIndex) ? '▼' : '▶'}
              </Text>
            </TouchableOpacity>

            {expandedModules.has(moduleIndex) && (
              <Card.Content>
                {module.lessons?.map((lesson: any, lessonIndex: number) => (
                  <TouchableOpacity
                    key={lessonIndex}
                    style={styles.lessonItem}
                    onPress={() => {
                      // Navigate to lesson detail
                      navigation.navigate('LessonDetail' as never, {
                        lesson,
                        courseId,
                      } as never);
                    }}>
                    <Text style={styles.lessonTitle}>{lesson.title}</Text>
                    <Text style={styles.lessonSummary} numberOfLines={2}>
                      {lesson.summary}
                    </Text>
                  </TouchableOpacity>
                ))}
              </Card.Content>
            )}
          </Card>
        ))}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    padding: 20,
    backgroundColor: '#6200ee',
  },
  courseTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 4,
  },
  moduleCount: {
    fontSize: 14,
    color: '#fff',
    opacity: 0.9,
  },
  content: {
    padding: 16,
  },
  moduleCard: {
    marginBottom: 16,
    elevation: 4,
  },
  moduleHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
  },
  moduleHeaderContent: {
    flex: 1,
  },
  moduleTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  lessonCount: {
    fontSize: 14,
    color: '#666',
  },
  expandIcon: {
    fontSize: 16,
    color: '#6200ee',
    marginLeft: 16,
  },
  lessonItem: {
    padding: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  lessonTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  lessonSummary: {
    fontSize: 14,
    color: '#666',
  },
});


