#!/bin/bash
# Quick reference script for common dev container commands
# Usage: source .devcontainer/commands.sh OR copy commands directly

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored headers
print_section() {
  echo -e "${BLUE}>>> $1${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}⚠ $1${NC}"
}

# Development shortcuts
alias gb='./gradlew.bat'
alias gbt='./gradlew.bat test'
alias gbr='./gradlew.bat bootRun'
alias gbscan='./gradlew.bat checkFormat'
alias gbformat='./gradlew.bat format'
alias gbpmd='./gradlew.bat --no-problems-report pmdMain'
alias gbdocs='./gradlew.bat asciidoctor'
alias gbdocker='./gradlew.bat dockerBuild'
alias gbclean='./gradlew.bat clean'

# Database shortcuts
alias pg='psql -h postgres -U demo_user -d technical_interview_demo'
alias pgdrop='psql -h postgres -U demo_user -c "DROP DATABASE IF EXISTS technical_interview_demo; CREATE DATABASE technical_interview_demo;"'

# Service management
alias docker-ps='docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"'
alias docker-logs-postgres='docker logs -f technical-interview-demo-postgres'
alias docker-logs-prom='docker logs -f technical-interview-demo-prometheus'

# Common workflows
dev-setup() {
  print_section "Setting up development environment..."
  ./gradlew.bat clean build
  print_success "Setup complete!"
}

dev-start() {
  print_section "Starting Spring Boot application..."
  ./gradlew.bat bootRun
}

dev-test() {
  print_section "Running tests..."
  ./gradlew.bat test
}

dev-quality() {
  print_section "Running quality checks..."
  print_warning "Running checkFormat..."
  ./gradlew.bat checkFormat || print_error "Format check failed!"
  print_warning "Running PMD..."
  ./gradlew.bat --no-problems-report pmdMain || print_error "PMD check failed!"
  print_warning "Running tests..."
  ./gradlew.bat test || print_error "Tests failed!"
  print_success "Quality checks complete!"
}

dev-docs() {
  print_section "Generating API documentation..."
  ./gradlew.bat asciidoctor
  print_success "Docs generated at: build/docs/asciidoc/index.html"
}

dev-db-reset() {
  print_warning "Resetting PostgreSQL database..."
  psql -h postgres -U demo_user -d postgres -c "DROP DATABASE IF EXISTS technical_interview_demo;"
  psql -h postgres -U demo_user -d postgres -c "CREATE DATABASE technical_interview_demo;"
  print_success "Database reset complete!"
}

dev-ps() {
  print_section "Running containers:"
  docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

dev-logs() {
  print_section "Here are logs from recent containers:"
  echo "PostgreSQL logs:"
  docker logs --tail 10 technical-interview-demo-postgres
  echo ""
  echo "Prometheus logs:"
  docker logs --tail 10 technical-interview-demo-prometheus
}

dev-help() {
  cat <<EOF
${BLUE}Development Container Quick Reference${NC}

${YELLOW}Gradle Shortcuts:${NC}
  gb              ./gradlew.bat
  gbt             ./gradlew.bat test
  gbr             ./gradlew.bat bootRun
  gbscan          ./gradlew.bat checkFormat
  gbformat        ./gradlew.bat format
  gbpmd           ./gradlew.bat --no-problems-report pmdMain
  gbdocs          ./gradlew.bat asciidoctor
  gbdocker        ./gradlew.bat dockerBuild
  gbclean         ./gradlew.bat clean

${YELLOW}Database Shortcuts:${NC}
  pg              psql to PostgreSQL
  pgdrop          Drop and recreate database

${YELLOW}Common Workflows:${NC}
  dev-setup       Clean build and setup
  dev-start       Start Spring Boot app
  dev-test        Run all tests
  dev-quality     Run all quality checks
  dev-docs        Generate API documentation
  dev-db-reset    Reset PostgreSQL database
  dev-ps          Show running containers
  dev-logs        Show container logs
  dev-help        Show this help

${YELLOW}Application Access:${NC}
  • App:        http://localhost:8080
  • Docs:       http://localhost:8080/docs
  • Health:     http://localhost:8080/actuator/health
  • Readiness:  http://localhost:8080/actuator/health/readiness
  • Prometheus: http://localhost:9090
  • Books API:  http://localhost:8080/api/books

${BLUE}Tips:${NC}
  • Use 'dev-quality' before committing
  • Use 'dev-test' during development
  • PostgreSQL is at: postgres://demo_user:demo_password@postgres:5432/technical_interview_demo
  • Services auto-start via docker-compose.yml

EOF
}

# Print help on source
if [ "${BASH_SOURCE[0]}" == "${0}" ]; then
  dev-help
else
  print_success "Dev container shortcuts loaded! Type 'dev-help' for more info."
fi
