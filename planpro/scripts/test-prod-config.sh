#!/bin/bash

# Test Production Configuration Script
# This script helps test the production configuration locally

echo "🚀 Testing Production Configuration for Railway Deployment"

# Check if required environment variables are set
echo "📋 Checking environment variables..."

if [ -z "$PROD_DB_HOST" ]; then
    echo "❌ PROD_DB_HOST is not set"
    echo "   Please set: export PROD_DB_HOST=postgres.railway.internal"
    exit 1
fi

if [ -z "$PROD_DB_NAME" ]; then
    echo "❌ PROD_DB_NAME is not set"
    echo "   Please set: export PROD_DB_NAME=railway"
    exit 1
fi

if [ -z "$PROD_DB_PASSWORD" ]; then
    echo "❌ PROD_DB_PASSWORD is not set"
    echo "   Please set: export PROD_DB_PASSWORD=your_password"
    exit 1
fi

if [ -z "$PROD_DB_USERNAME" ]; then
    echo "❌ PROD_DB_USERNAME is not set"
    echo "   Please set: export PROD_DB_USERNAME=postgres"
    exit 1
fi

echo "✅ All required environment variables are set"

# Set additional required variables for testing
export SPRING_PROFILES_ACTIVE=prod
export PORT=8080
export JWT_SECRET=test-jwt-secret-for-local-testing
export PASSWORD_ENCRYPTION_KEY=test-encryption-key-for-local-testing
export WEB_URL=http://localhost:8080

echo "🔧 Starting application with production profile..."

# Build the application
echo "📦 Building application..."
./gradlew build -x test

if [ $? -eq 0 ]; then
    echo "✅ Build successful"
    
    # Start the application
    echo "🚀 Starting application..."
    java -jar build/libs/*.jar
    
else
    echo "❌ Build failed"
    exit 1
fi
