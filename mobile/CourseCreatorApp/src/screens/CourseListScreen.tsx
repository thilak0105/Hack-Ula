/**
 * Course List Screen
 * Display all created courses
 */

import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  RefreshControl,
} from 'react-native';
import {Card, ActivityIndicator} from 'react-native-paper';
import {useNavigation} from '@react-navigation/native';
import APIService from '../services/api';

interface Course {
  id: string;
  title: string;
  modules: any[];
  createdAt?: string;
}

export default function CourseListScreen() {
  const navigation = useNavigation();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadCourses();
  }, []);

  const loadCourses = async () => {
    try {
      // In a real app, you'd fetch from local storage or backend
      // For now, we'll use a placeholder
      setCourses([]);
    } catch (error) {
      console.error('Failed to load courses:', error);
    } finally {
      setLoading(false);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await loadCourses();
    setRefreshing(false);
  };

  const renderCourse = ({item}: {item: Course}) => (
    <TouchableOpacity
      onPress={() => {
        navigation.navigate('LearningPath' as never, {
          courseId: item.id,
          courseData: item,
        } as never);
      }}>
      <Card style={styles.courseCard}>
        <Card.Content>
          <Text style={styles.courseTitle}>{item.title}</Text>
          <Text style={styles.courseInfo}>
            {item.modules?.length || 0} modules
          </Text>
          {item.createdAt && (
            <Text style={styles.courseDate}>
              Created: {new Date(item.createdAt).toLocaleDateString()}
            </Text>
          )}
        </Card.Content>
      </Card>
    </TouchableOpacity>
  );

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#6200ee" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {courses.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>No courses yet</Text>
          <Text style={styles.emptySubtext}>
            Create your first course from the Upload tab
          </Text>
        </View>
      ) : (
        <FlatList
          data={courses}
          renderItem={renderCourse}
          keyExtractor={item => item.id}
          contentContainerStyle={styles.list}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
        />
      )}
    </View>
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
  list: {
    padding: 16,
  },
  courseCard: {
    marginBottom: 12,
    elevation: 2,
  },
  courseTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  courseInfo: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
  courseDate: {
    fontSize: 12,
    color: '#999',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  emptyText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#666',
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
  },
});


