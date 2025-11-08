# Setup Guide for Course Creator

This guide will help you set up and run the Course Creator application.

## Prerequisites

- Python 3.8 or higher
- Node.js 14 or higher (for frontend)
- npm or yarn (for frontend)

## Step 1: Install Python Dependencies

```bash
cd code_o_clock_remote_retry
pip install -r requirements.txt
```

## Step 2: Set Up Environment Variables

Create a `.env` file in the `code_o_clock_remote_retry` directory with your API keys:

```env
# Groq API Key (Primary provider - recommended)
# Get your API key from: https://console.groq.com/
GROQ_API_KEY=your_groq_api_key_here

# Google Gemini API Key (Fallback provider)
# Get your API key from: https://makersuite.google.com/app/apikey
GOOGLE_API_KEY=your_google_api_key_here

# DeepSeek API Key (Optional - alternative provider)
# Get your API key from: https://platform.deepseek.com/
DEEPSEEK_API_KEY=your_deepseek_api_key_here
```

### Quick Setup Scripts

You can use the provided setup scripts to configure API keys:

```bash
# Setup Groq (Recommended)
python setup_groq.py

# Setup DeepSeek (Alternative)
python setup_deepseek.py

# Setup all providers
python setup_all_providers.py
```

## Step 3: Start the Backend Server

```bash
# Option 1: Using the start script
python start_server.py

# Option 2: Directly
python app.py
```

The server will start on `http://127.0.0.1:5000`

## Step 4: Set Up Frontend (Optional)

If you want to run the frontend:

```bash
cd frontend
npm install
npm start
```

The frontend will start on `http://localhost:3000`

## Step 5: Test the Application

1. Backend should be running on `http://127.0.0.1:5000`
2. Frontend should be running on `http://localhost:3000` (if set up)
3. Test the API endpoints using the frontend or tools like Postman

## Troubleshooting

### Issue: "No AI providers available"
- Make sure you have at least one API key set in your `.env` file
- Run `python setup_groq.py` to set up Groq (recommended)

### Issue: Import errors
- Make sure all dependencies are installed: `pip install -r requirements.txt`
- Check that you're using Python 3.8 or higher

### Issue: ChromaDB errors
- The database will be created automatically in the `chroma_db` directory
- Make sure you have write permissions in the project directory

### Issue: Port already in use
- Change the port in `app.py` or `start_server.py`
- Or stop the process using port 5000

## API Endpoints

- `POST /upload` - Upload content (PDF, DOCX, PPTX, or URL)
- `POST /search` - Search stored content
- `POST /lesson-content` - Get detailed lesson content
- `POST /translate` - Translate text content
- `POST /generate-quiz` - Generate quiz from content
- `POST /text-to-speech` - Convert text to speech
- `POST /generate-pptx` - Generate PowerPoint presentation
- `POST /generate-pdf` - Generate PDF document
- And more...

## Notes

- The application uses Groq as the primary AI provider with automatic fallback to Gemini
- ChromaDB is used for vector storage and retrieval
- All generated files are saved in the `downloads` directory

