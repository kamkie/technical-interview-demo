#!/bin/bash
# Wrapper for ./gradlew that auto-loads .env before execution
#
# Usage:
#   Make this executable: chmod +x build.sh
#   ./build.sh build
#   ./build.sh bootRun
#
# Or add to PATH and use directly:
#   build.sh test
#
# Benefits:
#   - Automatically loads .env if it exists
#   - Works identically to ./gradlew
#   - No manual environment setup needed per session
#   - Faster AI instruction execution (no env discovery steps)

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$REPO_ROOT/.env"

# Step 1: Auto-load .env if it exists
if [ -f "$ENV_FILE" ]; then
    # For bash, we source .env directly if it follows bash syntax
    # Otherwise, we parse it and export variables
    set -a
    source "$ENV_FILE" 2>/dev/null || true
    set +a
fi

# Step 2: Call ./gradlew with all arguments
cd "$REPO_ROOT"
./gradlew "$@"
