# Dev Container Quick Reference Card

## Getting Started (3 Steps)

```
1. Install: code --install-extension ms-vscode-remote.remote-containers
2. Open: code /path/to/technical-interview-demo
3. Reopen: Ctrl+Shift+P → "Dev Containers: Reopen in Container"
4. Wait: 5-10 minutes for first build
✓ Done! You're ready to develop
```

## What's Included

- **Java 25 JDK** (official Microsoft OpenJDK)
- **Gradle** (via wrapper)
- **Docker & Docker Compose** (Docker-in-Docker)
- **PostgreSQL** (port 5432, auto-starts)
- **Prometheus** (port 9090, auto-starts)
- **VS Code extensions** (Java Pack, Spring Boot, Docker, etc.)
- **Helper commands** (20+ shortcuts)

## Essential Commands

| Task | Command |
|------|---------|
| **Start App** | `./gradlew.bat bootRun` |
| **Run Tests** | `./gradlew.bat test` |
| **Check Format** | `./gradlew.bat spotlessCheck` |
| **Fix Format** | `./gradlew.bat spotlessApply` |
| **Run PMD** | `./gradlew.bat --no-problems-report pmdMain` |
| **Generate Docs** | `./gradlew.bat asciidoctor` |
| **Build Docker** | `./gradlew.bat dockerBuild` |
| **Quality Check** | `dev-quality` *(after sourcing commands.sh)* |

## Access Points

| Service | URL |
|---------|-----|
| **App** | http://localhost:8080 |
| **API Docs** | http://localhost:8080/docs |
| **Health** | http://localhost:8080/actuator/health |
| **Prometheus** | http://localhost:9090 |
| **Readiness** | http://localhost:8080/actuator/health/readiness |

## Database Info

```
Host:     postgres (or localhost from host machine)
Port:     5432
Database: technical_interview_demo
User:     demo_user
Password: demo_password
```

## Useful Shortcuts (In Dev Container)

```bash
# After: source .devcontainer/commands.sh

gb help         # List all commands
gb              # ./gradlew.bat
gbt             # Run tests
gbr             # Run app
gbdocs          # Generate docs
gbformat        # Fix formatting
dev-help        # Show all helpers
pg              # Connect to PostgreSQL
docker-ps       # Show containers
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Container build times out | Increase Docker memory/CPU |
| Port 8080 in use | Stop other apps or use different port |
| Java not found | Rebuild container |
| Can't connect to DB | Wait 10 seconds, check Docker running |

## Files & Directories

```
.devcontainer/
  ├── devcontainer.json       ← Main config
  ├── README.md               ← Full documentation
  ├── Dockerfile              ← Optional custom image
  ├── docker-compose.yml      ← Services
  ├── commands.sh             ← Helper shortcuts
  └── ...
```

## Tips

✨ **Hot Reload:** Spring DevTools auto-reloads on code changes
✨ **Debug:** Set breakpoints in VS Code while running
✨ **Cache:** ~/.m2 and ~/.gradle automatically cached
✨ **Git:** Git commands work normally in container
✨ **Performance:** Second and subsequent builds are much faster

## Key Ports

```
8080 - Spring Boot Application
9090 - Prometheus Metrics
5432 - PostgreSQL Database
```

## When Stuck

```bash
# 1. Check container logs
docker logs -f <container_id>

# 2. Check services
docker ps

# 3. Rebuild container
# Ctrl+Shift+P → "Dev Containers: Rebuild Container"

# 4. Clean everything
docker system prune -a
```

---

**For more details:** See `.devcontainer/README.md`
