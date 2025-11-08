/**
 * API Service
 * Handles communication with backend Flask server
 * Uses RunAnywhere SDK for on-device AI when available
 */

import axios, { AxiosInstance } from 'axios';
import { RunAnywhereService } from './runanywhere';
import Config from 'react-native-config';

const API_BASE_URL = Config.BACKEND_API_URL || 'http://localhost:5000';

class APIService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 60000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  /**
   * Upload content (file or URL)
   * Always uses backend for file processing
   */
  async uploadContent(
    file: any | null,
    url: string | null,
    prompt: string
  ): Promise<any> {
    const formData = new FormData();
    
    if (file) {
      formData.append('file', {
        uri: file.uri,
        type: file.type,
        name: file.name,
      } as any);
    }
    
    if (url) {
      formData.append('url', url);
    }
    
    formData.append('prompt', prompt);

    const response = await this.client.post('/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  }

  /**
   * Generate course structure
   * Tries RunAnywhere first, falls back to backend
   */
  async generateCourse(
    textChunks: string[],
    userPrompt: string
  ): Promise<any> {
    // Try RunAnywhere first (on-device, private, fast)
    if (RunAnywhereService.isAvailable()) {
      try {
        console.log('Using RunAnywhere SDK for course generation');
        const course = await RunAnywhereService.generateCourse(
          textChunks,
          userPrompt
        );
        return { course, source: 'runanywhere' };
      } catch (error) {
        console.warn('RunAnywhere failed, falling back to backend:', error);
        // Fall through to backend
      }
    }

    // Fallback to backend
    console.log('Using backend API for course generation');
    const response = await this.client.post('/generate-course', {
      chunks: textChunks,
      prompt: userPrompt,
    });

    return { course: response.data, source: 'backend' };
  }

  /**
   * Generate lesson content
   * Uses RunAnywhere for on-device processing
   */
  async generateLessonContent(
    lessonTitle: string,
    lessonSummary: string,
    contextChunks: string[],
    courseId?: string
  ): Promise<string> {
    // Try RunAnywhere first
    if (RunAnywhereService.isAvailable()) {
      try {
        return await RunAnywhereService.generateLessonContent(
          lessonTitle,
          lessonSummary,
          contextChunks
        );
      } catch (error) {
        console.warn('RunAnywhere failed, using backend:', error);
      }
    }

    // Fallback to backend
    const response = await this.client.post('/lesson-content', {
      lesson_title: lessonTitle,
      lesson_summary: lessonSummary,
      context_chunks: contextChunks,
      course_id: courseId,
    });

    return response.data.content;
  }

  /**
   * Generate quiz
   * Uses RunAnywhere for on-device generation
   */
  async generateQuiz(
    content: string,
    topic: string,
    numQuestions: number = 5
  ): Promise<any> {
    // Try RunAnywhere first
    if (RunAnywhereService.isAvailable()) {
      try {
        return await RunAnywhereService.generateQuiz(
          content,
          topic,
          numQuestions
        );
      } catch (error) {
        console.warn('RunAnywhere failed, using backend:', error);
      }
    }

    // Fallback to backend
    const response = await this.client.post('/generate-quiz', {
      content,
      topic,
      num_questions: numQuestions,
    });

    return response.data.quiz;
  }

  /**
   * Translate text
   * Uses RunAnywhere for on-device translation
   */
  async translateText(
    text: string,
    targetLang: string,
    sourceLang: string = 'auto'
  ): Promise<string> {
    // Try RunAnywhere first
    if (RunAnywhereService.isAvailable()) {
      try {
        return await RunAnywhereService.translateText(
          text,
          targetLang,
          sourceLang
        );
      } catch (error) {
        console.warn('RunAnywhere failed, using backend:', error);
      }
    }

    // Fallback to backend
    const response = await this.client.post('/translate', {
      text,
      target_lang: targetLang,
      source_lang: sourceLang,
    });

    return response.data.translated_text;
  }

  /**
   * Search content in ChromaDB
   * Always uses backend (requires vector database)
   */
  async searchContent(query: string, nResults: number = 5): Promise<any> {
    const response = await this.client.post('/search', {
      query,
      n_results: nResults,
    });

    return response.data;
  }

  /**
   * Get course by ID
   */
  async getCourse(courseId: string): Promise<any> {
    const response = await this.client.get(`/course/${courseId}`);
    return response.data;
  }

  /**
   * Generate PDF (always uses backend)
   */
  async generatePDF(courseData: any): Promise<string> {
    const response = await this.client.post('/generate-pdf', {
      courseData,
    });

    return response.data.download_url;
  }

  /**
   * Generate PowerPoint (always uses backend)
   */
  async generatePPTX(courseData: any): Promise<string> {
    const response = await this.client.post('/generate-pptx', {
      courseData,
    });

    return response.data.download_url;
  }

  /**
   * Text to speech (uses native TTS on mobile)
   */
  async textToSpeech(text: string, language: string = 'en'): Promise<string> {
    // On mobile, prefer native TTS
    // Fallback to backend if needed
    const response = await this.client.post('/text-to-speech', {
      text,
      language,
    });

    return response.data.audio_base64;
  }
}

export default new APIService();

