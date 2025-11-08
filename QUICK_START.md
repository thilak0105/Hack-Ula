# Quick Start Guide

## ✅ Project Status: READY TO USE

The project has been set up and tested. All components are working correctly.

## 🚀 Starting the Application

### Backend (Required)

```bash
# Navigate to the project directory
cd code_o_clock_remote_retry

# Start the Flask server
python app.py
```

The backend will start on `http://127.0.0.1:5000`

### Frontend (Optional)

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start the React app
npm start
```

The frontend will start on `http://localhost:3000`

## 📋 What's Been Fixed

1. ✅ Fixed syntax error in `app.py` (removed duplicate code)
2. ✅ Updated `requirements.txt` with missing dependencies:
   - Added `deep-translator` for translation service
   - Added `python-dotenv` for environment variable management
   - Removed `libretranslate` (not needed, causes Windows build issues)
3. ✅ Verified all imports work correctly
4. ✅ Tested Flask app initialization (23 routes registered)
5. ✅ Verified AI provider setup (Groq is configured and working)

## 🔧 Configuration

### API Keys

The project uses Groq as the primary AI provider. Your API key is already configured.

To add additional providers:

1. **Google Gemini** (Fallback):
   ```bash
   # Add to .env file
   GOOGLE_API_KEY=your_key_here
   ```

2. **DeepSeek** (Alternative):
   ```bash
   # Add to .env file
   DEEPSEEK_API_KEY=your_key_here
   ```

Or use the setup scripts:
```bash
python setup_groq.py      # Setup Groq
python setup_deepseek.py  # Setup DeepSeek
python setup_all_providers.py  # Setup all providers
```

## 🧪 Testing

Run the test script to verify everything is working:

```bash
python test_setup.py
```

## 📚 API Endpoints

The backend provides the following main endpoints:

- `POST /upload` - Upload content (PDF, DOCX, PPTX, or URL)
- `POST /search` - Search stored content
- `POST /lesson-content` - Get detailed lesson content
- `POST /translate` - Translate text content
- `POST /generate-quiz` - Generate quiz from content
- `POST /text-to-speech` - Convert text to speech
- `POST /generate-pptx` - Generate PowerPoint presentation
- `POST /generate-pdf` - Generate PDF document

## 📁 Project Structure

```
code_o_clock_remote_retry/
├── app.py                 # Main Flask application
├── ai_providers.py         # AI provider management (Groq, Gemini, etc.)
├── chroma_storage.py       # ChromaDB storage for course content
├── content_ingestion.py    # Content extraction (PDF, DOCX, PPTX, URLs)
├── quiz_generator.py      # Quiz generation
├── translation_service.py # Translation service
├── tts_service.py         # Text-to-speech service
├── pptx_generator.py       # PowerPoint generation
├── pdf_generator.py        # PDF generation
├── frontend/              # React frontend application
├── chroma_db/             # ChromaDB database (auto-created)
└── downloads/             # Generated files (auto-created)
```

## 🎯 Next Steps

1. Start the backend: `python app.py`
2. (Optional) Start the frontend: `cd frontend && npm start`
3. Test the application by uploading content or using the API endpoints
4. Check the `SETUP_GUIDE.md` for detailed information

## ⚠️ Troubleshooting

If you encounter any issues:

1. **No AI providers available**: Make sure at least one API key is set in `.env`
2. **Import errors**: Run `pip install -r requirements.txt`
3. **Port already in use**: Change the port in `app.py` or stop the process using port 5000
4. **Frontend not connecting**: Make sure the backend is running on port 5000

## 📝 Notes

- The application automatically uses Groq as the primary provider with fallback to Gemini
- ChromaDB is used for vector storage and will be created automatically
- All generated files are saved in the `downloads` directory
- The project is ready to use - all dependencies are installed and configured

