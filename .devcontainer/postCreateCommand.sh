#!/bin/bash
set -e

echo "🔧 Setting up development environment..."

# Update and install system dependencies
echo "📦 Installing system dependencies..."
apt-get update
apt-get install -y --no-install-recommends \
    git \
    curl \
    wget \
    build-essential \
    postgresql-client \
    npm

# Java 25 is already included in the base image
echo "☕ Java version:"
java -version

# Verify Gradle wrapper works
echo "📦 Verifying Gradle..."
./gradlew --version

# Pre-download dependencies to speed up first builds
echo "⬇️ Downloading Gradle dependencies (this may take a few minutes)..."
./gradlew dependencies > /dev/null 2>&1 || true

# Build the project once to ensure everything is set up
echo "🏗️ Building project (first build may take a few minutes)..."
./gradlew build -x test > /dev/null 2>&1 || true

echo "✅ Development environment setup complete!"
echo ""
echo "Next steps:"
echo "  1. Run the application: ./gradlew.bat bootRun"
echo "  2. Run tests: ./gradlew.bat test"
echo "  3. Generate docs: ./gradlew.bat asciidoctor"
echo "  4. View API docs at: http://localhost:8080/docs"
echo ""
echo "Available commands:"
echo "  • ./gradlew.bat bootRun          - Run the application"
echo "  • ./gradlew.bat test             - Run all tests"
echo "  • ./gradlew.bat spotlessCheck    - Check code formatting"
echo "  • ./gradlew.bat pmdMain          - Run PMD analysis"
echo "  • ./gradlew.bat asciidoctor      - Generate API documentation"
echo "  • ./gradlew.bat dockerBuild      - Build Docker image"
echo ""
