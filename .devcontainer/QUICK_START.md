# Dev Container Quick Reference Card

## Getting Started

```
1. Install: code --install-extension ms-vscode-remote.remote-containers
2. Open: code /path/to/technical-interview-demo
3. Reopen: Ctrl+Shift+P -> "Dev Containers: Reopen in Container"
4. Wait: 5-10 minutes for first build
5. Run: ./gradlew bootRun
```

## What's Included

- **Java 25 JDK** (official Microsoft OpenJDK)
- **Gradle** (via wrapper)
- **Docker & Docker Compose** (Docker-in-Docker)
- **Optional PostgreSQL** (port 5432)
- **Optional Prometheus** (port 9090)
- **VS Code extensions** (Java Pack, Spring Boot, Docker, etc.)
- **Helper commands** (after sourcing `commands.sh`)

## Essential Commands

| Task | Command |
|------|---------|
| **Start App** | `./gradlew bootRun` |
| **Run Tests** | `./gradlew test` |
| **Check Format** | `./gradlew checkFormat` |
| **Fix Format** | `./gradlew format` |
| **Run PMD** | `./gradlew --no-problems-report pmdMain` |
| **Generate Docs** | `./gradlew asciidoctor` |
| **Build Docker** | `./gradlew dockerBuild` |
| **Quality Check** | `dev-quality` *(after sourcing commands.sh)* |
| **Start Optional Services** | `docker compose -f .devcontainer/docker-compose.yml up -d` |

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
Host:     localhost
Port:     5432
Database: technical_interview_demo
User:     demo_user
Password: demo_password
```

## Useful Shortcuts (In Dev Container)

```bash
# After: source .devcontainer/commands.sh

dev-help        # List all commands
gb              # ./gradlew
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
  ├── devcontainer.json       # Main config
  ├── README.md               # Full documentation
  ├── Dockerfile              # Optional custom image
  ├── docker-compose.yml      # Optional services
  ├── commands.sh             # Helper shortcuts
  └── ...
```

## Tips

- **Hot Reload:** Spring DevTools auto-reloads on code changes
- **Debug:** Set breakpoints in VS Code while running
- **Cache:** `~/.m2` and `~/.gradle` are mounted from the host
- **Git:** Git commands work normally in the container
- **Performance:** Second and subsequent builds are much faster

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
# Ctrl+Shift+P -> "Dev Containers: Rebuild Container"

# 4. Clean everything
docker system prune -a
```

---

**For more details:** See `.devcontainer/README.md`
