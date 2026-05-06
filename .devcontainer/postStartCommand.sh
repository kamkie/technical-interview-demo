#!/bin/bash
set -e

echo "🚀 Starting development environment..."

# Ensure Docker daemon is available
if ! docker ps >/dev/null 2>&1; then
  echo "⚠️ Docker is not available yet, waiting..."
  sleep 2
fi

echo "✅ Development environment is ready!"
echo ""
echo "📝 Tip: Use 'gradle' or './gradlew' to run Gradle commands"
echo "📊 Monitor app at http://localhost:8080/"
