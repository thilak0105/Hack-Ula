/**
 * RunAnywhere SDK Service
 * Handles on-device AI processing with intelligent routing
 */

import { NativeModules, Platform } from 'react-native';

const { RunAnywhereSDK } = NativeModules;

export interface RunAnywhereOptions {
  privacyLevel?: 'high' | 'medium' | 'low';
  maxTokens?: number;
  temperature?: number;
  useLocalFirst?: boolean;
}

export interface RunAnywhereResponse {
  content: string;
  processedLocally: boolean;
  latency: number;
  tokensUsed?: number;
}

export class RunAnywhereService {
  private static initialized = false;
  private static apiKey: string | null = null;

  /**
   * Initialize RunAnywhere SDK
   */
  static async initialize(apiKey: string): Promise<boolean> {
    try {
      if (!RunAnywhereSDK) {
        console.warn('RunAnywhere SDK not available. Using fallback mode.');
        return false;
      }

      const result = await RunAnywhereSDK.initialize(apiKey);
      this.initialized = result;
      this.apiKey = apiKey;
      
      if (result) {
        console.log('✅ RunAnywhere SDK initialized successfully');
      }
      
      return result;
    } catch (error) {
      console.error('RunAnywhere SDK initialization failed:', error);
      return false;
    }
  }

  /**
   * Check if SDK is available and initialized
   */
  static isAvailable(): boolean {
    return this.initialized && RunAnywhereSDK !== null;
  }

  /**
   * Process AI request with intelligent routing
   * RunAnywhere decides whether to process locally or in cloud
   */
  static async processRequest(
    prompt: string,
    context?: string,
    options: RunAnywhereOptions = {}
  ): Promise<RunAnywhereResponse> {
    if (!this.isAvailable()) {
      throw new Error('RunAnywhere SDK not initialized');
    }

    try {
      const requestOptions = {
        prompt,
        context: context || '',
        privacyLevel: options.privacyLevel || 'high',
        maxTokens: options.maxTokens || 2000,
        temperature: options.temperature || 0.7,
        useLocalFirst: options.useLocalFirst !== false,
      };

      const startTime = Date.now();
      const response = await RunAnywhereSDK.processRequest(requestOptions);
      const latency = Date.now() - startTime;

      return {
        content: response.content || response.text || response,
        processedLocally: response.processedLocally || false,
        latency,
        tokensUsed: response.tokensUsed,
      };
    } catch (error) {
      console.error('RunAnywhere request failed:', error);
      throw error;
    }
  }

  /**
   * Generate course structure from content
   * Uses RunAnywhere for on-device processing when possible
   */
  static async generateCourse(
    textChunks: string[],
    userPrompt: string
  ): Promise<any> {
    const fullText = textChunks.join('\n\n');
    
    const systemPrompt = `You are an expert Course Designer AI. Create a structured course in JSON format.

Rules:
1. Always output valid JSON only - no markdown, no explanations outside JSON.
2. Follow this exact schema:
{
  "course": "Course Title",
  "modules": [
    {
      "title": "Module Title",
      "lessons": [
        {
          "title": "Lesson Title",
          "summary": "Concise overview (2-3 sentences, max 50 words)",
          "detail": "Comprehensive explanation (400-600 words)"
        }
      ]
    }
  ]
}

User Request: ${userPrompt}

Content:
${fullText}

Return only valid JSON.`;

    const response = await this.processRequest(systemPrompt, '', {
      privacyLevel: 'high', // Process course content locally for privacy
      maxTokens: 4000,
    });

    try {
      // Parse JSON from response
      const jsonMatch = response.content.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        return JSON.parse(jsonMatch[0]);
      }
      return JSON.parse(response.content);
    } catch (error) {
      console.error('Failed to parse course JSON:', error);
      throw new Error('Invalid course structure received');
    }
  }

  /**
   * Generate lesson content
   */
  static async generateLessonContent(
    lessonTitle: string,
    lessonSummary: string,
    contextChunks: string[]
  ): Promise<string> {
    const context = contextChunks.join('\n\n');
    
    const prompt = `Create a comprehensive lesson about: ${lessonTitle}

Summary: ${lessonSummary}

Context:
${context}

Write a detailed lesson (500-800 words) that includes:
- Clear introduction
- Key concepts and definitions
- Real-world examples
- Practical applications
- Common misconceptions
- Key takeaways

Write in an engaging, educational tone.`;

    const response = await this.processRequest(prompt, '', {
      privacyLevel: 'high',
      maxTokens: 2000,
    });

    return response.content;
  }

  /**
   * Generate quiz from content
   */
  static async generateQuiz(
    content: string,
    topic: string,
    numQuestions: number = 5
  ): Promise<any> {
    const prompt = `Generate a quiz with ${numQuestions} questions about: ${topic}

Content:
${content}

Return JSON in this format:
{
  "title": "Quiz Title",
  "questions": [
    {
      "id": 1,
      "question": "Question text",
      "options": ["Option 1", "Option 2", "Option 3", "Option 4"],
      "correctAnswer": 0,
      "explanation": "Explanation text"
    }
  ]
}

Return only valid JSON.`;

    const response = await this.processRequest(prompt, '', {
      privacyLevel: 'medium',
      maxTokens: 1500,
    });

    try {
      const jsonMatch = response.content.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        return JSON.parse(jsonMatch[0]);
      }
      return JSON.parse(response.content);
    } catch (error) {
      console.error('Failed to parse quiz JSON:', error);
      throw new Error('Invalid quiz structure received');
    }
  }

  /**
   * Translate text
   */
  static async translateText(
    text: string,
    targetLang: string,
    sourceLang: string = 'auto'
  ): Promise<string> {
    const prompt = `Translate the following text to ${targetLang}:
${text}

Return only the translated text, no explanations.`;

    const response = await this.processRequest(prompt, '', {
      privacyLevel: 'high', // Process translations locally for privacy
      maxTokens: 1000,
    });

    return response.content;
  }

  /**
   * Summarize content
   */
  static async summarizeContent(content: string, maxLength: number = 200): Promise<string> {
    const prompt = `Summarize the following content in ${maxLength} words or less:
${content}

Return only the summary.`;

    const response = await this.processRequest(prompt, '', {
      privacyLevel: 'high',
      maxTokens: 500,
    });

    return response.content;
  }
}

