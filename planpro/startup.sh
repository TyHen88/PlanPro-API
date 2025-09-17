#!/bin/bash

# Railway startup script
echo "Starting PlanPro API on Railway..."

# Set default values for Railway environment
export PORT=${PORT:-8080}
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-railway}

# Ensure PORT is properly set for Spring Boot
export SERVER_PORT=$PORT
export MANAGEMENT_SERVER_PORT=$PORT

# Ensure required directories exist
mkdir -p /tmp/upload-files/temp_qr

# Set JVM options for Railway's memory constraints (more conservative)
export JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=50 -XX:MinRAMPercentage=25 -XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -Dfile.encoding=UTF-8 -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"

echo "JVM Options: $JAVA_TOOL_OPTIONS"
echo "Server Port: $PORT"
echo "Spring Profile: $SPRING_PROFILES_ACTIVE"

# Check if app.jar exists
if [ ! -f "app.jar" ]; then
    echo "ERROR: app.jar not found!"
    ls -la
    exit 1
fi

# Check available memory
echo "Available memory:"
free -h || true

# Start the application with error handling
echo "Starting Java application..."
echo "Environment variables:"
echo "DATABASE_URL: ${DATABASE_URL:-NOT_SET}"
echo "PROD_DB_HOST: ${PROD_DB_HOST:-NOT_SET}"
echo "PROD_DB_USERNAME: ${PROD_DB_USERNAME:-NOT_SET}"
echo "PROD_DB_PASSWORD: ${PROD_DB_PASSWORD:-NOT_SET}"

# Start the application without timeout (Railway handles this)
echo "Starting Java application..."
exec java -jar app.jar
