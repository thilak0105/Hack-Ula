#!/usr/bin/env python3
"""
Test script to verify the project setup
"""

import os
import sys
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

def test_imports():
    """Test if all required modules can be imported"""
    print("Testing imports...")
    try:
        from app import app
        from ai_providers import ai_manager
        from chroma_storage import ChromaDocumentStore
        from translation_service import translation_service
        from quiz_generator import quiz_generator
        from tts_service import tts_service
        from pptx_generator import pptx_generator
        from pdf_generator import pdf_generator
        print("✅ All imports successful")
        return True
    except Exception as e:
        print(f"❌ Import error: {e}")
        return False

def test_environment():
    """Test environment variables"""
    print("\nTesting environment variables...")
    groq_key = os.environ.get("GROQ_API_KEY")
    google_key = os.environ.get("GOOGLE_API_KEY")
    deepseek_key = os.environ.get("DEEPSEEK_API_KEY")
    
    print(f"GROQ_API_KEY: {'✅ SET' if groq_key else '❌ NOT SET'}")
    print(f"GOOGLE_API_KEY: {'✅ SET' if google_key else '❌ NOT SET'}")
    print(f"DEEPSEEK_API_KEY: {'✅ SET' if deepseek_key else '❌ NOT SET'}")
    
    if not groq_key and not google_key and not deepseek_key:
        print("\n⚠️  Warning: No API keys found!")
        print("   Please set at least one API key in your .env file")
        print("   Run: python setup_groq.py")
        return False
    
    return True

def test_ai_provider():
    """Test AI provider availability"""
    print("\nTesting AI provider...")
    try:
        from ai_providers import ai_manager
        if ai_manager.current_provider:
            print(f"✅ AI Provider: {ai_manager.current_provider.__class__.__name__}")
            return True
        else:
            print("❌ No AI provider available")
            return False
    except Exception as e:
        print(f"❌ AI Provider error: {e}")
        return False

def test_flask_app():
    """Test Flask app initialization"""
    print("\nTesting Flask app...")
    try:
        from app import app
        routes = [str(rule) for rule in app.url_map.iter_rules()]
        print(f"✅ Flask app initialized")
        print(f"✅ Routes registered: {len(routes)}")
        return True
    except Exception as e:
        print(f"❌ Flask app error: {e}")
        return False

def main():
    """Run all tests"""
    print("=" * 50)
    print("Course Creator Setup Test")
    print("=" * 50)
    
    results = []
    
    results.append(("Imports", test_imports()))
    results.append(("Environment", test_environment()))
    results.append(("AI Provider", test_ai_provider()))
    results.append(("Flask App", test_flask_app()))
    
    print("\n" + "=" * 50)
    print("Test Results:")
    print("=" * 50)
    
    for name, result in results:
        status = "✅ PASS" if result else "❌ FAIL"
        print(f"{name}: {status}")
    
    all_passed = all(result for _, result in results)
    
    if all_passed:
        print("\n🎉 All tests passed! The project is ready to use.")
        print("\nNext steps:")
        print("1. Start the server: python app.py")
        print("2. (Optional) Start the frontend: cd frontend && npm start")
    else:
        print("\n⚠️  Some tests failed. Please fix the issues above.")
        sys.exit(1)

if __name__ == "__main__":
    main()

