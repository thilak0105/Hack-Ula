/**
 * Home Screen
 * Main dashboard screen
 */

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
} from 'react-native';
import {Card, Button} from 'react-native-paper';
import {useNavigation} from '@react-navigation/native';

export default function HomeScreen() {
  const navigation = useNavigation();

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Course Creator</Text>
        <Text style={styles.subtitle}>AI-Powered Learning Platform</Text>
      </View>

      <View style={styles.content}>
        <Card style={styles.card}>
          <Card.Content>
            <Text style={styles.cardTitle}>Create New Course</Text>
            <Text style={styles.cardText}>
              Upload content and let AI generate a complete course structure
            </Text>
            <Button
              mode="contained"
              onPress={() => navigation.navigate('Upload' as never)}
              style={styles.button}>
              Get Started
            </Button>
          </Card.Content>
        </Card>

        <Card style={styles.card}>
          <Card.Content>
            <Text style={styles.cardTitle}>My Courses</Text>
            <Text style={styles.cardText}>
              View and manage your created courses
            </Text>
            <Button
              mode="outlined"
              onPress={() => navigation.navigate('Courses' as never)}
              style={styles.button}>
              View Courses
            </Button>
          </Card.Content>
        </Card>

        <Card style={styles.card}>
          <Card.Content>
            <Text style={styles.cardTitle}>Features</Text>
            <View style={styles.featureList}>
              <Text style={styles.featureItem}>✓ AI-Powered Course Generation</Text>
              <Text style={styles.featureItem}>✓ On-Device Processing (RunAnywhere)</Text>
              <Text style={styles.featureItem}>✓ Interactive Quizzes</Text>
              <Text style={styles.featureItem}>✓ Multi-language Support</Text>
              <Text style={styles.featureItem}>✓ Text-to-Speech</Text>
              <Text style={styles.featureItem}>✓ Offline Learning</Text>
            </View>
          </Card.Content>
        </Card>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    padding: 20,
    backgroundColor: '#6200ee',
    paddingTop: 40,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#fff',
    opacity: 0.9,
  },
  content: {
    padding: 16,
  },
  card: {
    marginBottom: 16,
    elevation: 4,
  },
  cardTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#333',
  },
  cardText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
  },
  button: {
    marginTop: 8,
  },
  featureList: {
    marginTop: 8,
  },
  featureItem: {
    fontSize: 14,
    color: '#333',
    marginBottom: 8,
  },
});


