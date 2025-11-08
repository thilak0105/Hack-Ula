/**
 * Upload Screen
 * Upload content and generate courses
 */

import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Alert,
  ActivityIndicator,
} from 'react-native';
import {Button, TextInput, Card} from 'react-native-paper';
import DocumentPicker from 'react-native-document-picker';
import APIService from '../services/api';
import {useNavigation} from '@react-navigation/native';

export default function UploadScreen() {
  const navigation = useNavigation();
  const [loading, setLoading] = useState(false);
  const [prompt, setPrompt] = useState('');
  const [url, setUrl] = useState('');
  const [selectedFile, setSelectedFile] = useState<any>(null);

  const pickDocument = async () => {
    try {
      const result = await DocumentPicker.pick({
        type: [
          DocumentPicker.types.pdf,
          DocumentPicker.types.docx,
          DocumentPicker.types.pptx,
        ],
      });
      setSelectedFile(result[0]);
    } catch (err) {
      if (DocumentPicker.isCancel(err)) {
        // User cancelled
      } else {
        Alert.alert('Error', 'Failed to pick document');
      }
    }
  };

  const handleUpload = async () => {
    if (!selectedFile && !url && !prompt.trim()) {
      Alert.alert('Error', 'Please provide a file, URL, or text prompt');
      return;
    }

    setLoading(true);
    try {
      const result = await APIService.uploadContent(
        selectedFile,
        url || null,
        prompt
      );

      if (result.course) {
        Alert.alert('Success', 'Course generated successfully!', [
          {
            text: 'View Course',
            onPress: () => {
              navigation.navigate('LearningPath' as never, {
                courseId: result.course_id,
                courseData: result.course,
              } as never);
            },
          },
          {text: 'OK'},
        ]);
      } else {
        Alert.alert('Error', 'Failed to generate course');
      }
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to upload content');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.content}>
        <Card style={styles.card}>
          <Card.Content>
            <Text style={styles.sectionTitle}>Upload Content</Text>
            <Text style={styles.sectionText}>
              Upload a document, provide a URL, or enter text to generate a
              course
            </Text>

            <Button
              mode="outlined"
              onPress={pickDocument}
              style={styles.button}
              icon="file-upload">
              {selectedFile ? selectedFile.name : 'Select Document'}
            </Button>

            <TextInput
              label="Or enter URL"
              value={url}
              onChangeText={setUrl}
              mode="outlined"
              style={styles.input}
              placeholder="https://example.com/article"
            />

            <TextInput
              label="Or describe your course"
              value={prompt}
              onChangeText={setPrompt}
              mode="outlined"
              multiline
              numberOfLines={4}
              style={styles.input}
              placeholder="Create a course about machine learning basics..."
            />

            <Button
              mode="contained"
              onPress={handleUpload}
              style={styles.submitButton}
              loading={loading}
              disabled={loading}>
              {loading ? 'Generating Course...' : 'Generate Course'}
            </Button>

            {loading && (
              <View style={styles.loadingContainer}>
                <ActivityIndicator size="large" color="#6200ee" />
                <Text style={styles.loadingText}>
                  AI is creating your course...
                </Text>
              </View>
            )}
          </Card.Content>
        </Card>

        <Card style={styles.card}>
          <Card.Content>
            <Text style={styles.infoTitle}>Supported Formats</Text>
            <Text style={styles.infoText}>• PDF Documents</Text>
            <Text style={styles.infoText}>• Word Documents (.docx)</Text>
            <Text style={styles.infoText}>• PowerPoint (.pptx)</Text>
            <Text style={styles.infoText}>• Web URLs</Text>
            <Text style={styles.infoText}>• Text descriptions</Text>
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
  content: {
    padding: 16,
  },
  card: {
    marginBottom: 16,
    elevation: 4,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#333',
  },
  sectionText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
  },
  button: {
    marginBottom: 16,
  },
  input: {
    marginBottom: 16,
  },
  submitButton: {
    marginTop: 8,
    paddingVertical: 4,
  },
  loadingContainer: {
    marginTop: 20,
    alignItems: 'center',
  },
  loadingText: {
    marginTop: 12,
    color: '#666',
  },
  infoTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#333',
  },
  infoText: {
    fontSize: 14,
    color: '#666',
    marginBottom: 4,
  },
});


