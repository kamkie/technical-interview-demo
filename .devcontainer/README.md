# Development Container Setup

This project includes a preconfigured development container (dev container) that provides a complete, isolated development environment using VS Code's Remote - Containers extension.

## Prerequisites

1. **VS Code** (version 1.52 or later)
2. **Docker Desktop** (installed and running)
3. **VS Code Extensions:**
    - Remote - Containers
    - Dev Containers

Install the extensions:

```bash
code --install-extension ms-vscode-remote.remote-containers
code --install-extension ms-vscode.remote-explorer
```

## Quick Start

### 1. Open the Project in Dev Container

1. Open the project folder in VS Code
2. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on macOS) to open the command palette
3. Type "Dev Containers: Reopen in Container"
4. VS Code will build the container (this takes 5-10 minutes on first run)
5. Once ready, you're inside the containerized development environment

### 2. Verify Setup

After the container is created and running:

```bash
# Verify Java is available
java -version

# Verify Gradle is available
./gradlew --version

# Verify Docker is available
docker ps
```

## What's Included

### Development Container Image

- **Base Image:** Microsoft's official Java development container (Java 25)
- **OS:** Debian Bookworm
- **Pre-installed Tools:**
    - Java 25 JDK (official Microsoft OpenJDK)
    - Gradle (via wrapper)
    - Docker & Docker Compose (Docker-in-Docker)
    - PostgreSQL client tools
    - Node.js/npm (for potential frontend work)

### Supporting Services (Docker Compose)

The `.devcontainer/docker-compose.yml` defines optional services:

- **PostgreSQL** (port 5432)
    - Database: `technical_interview_demo`
    - User: `demo_user`
    - Password: `demo_password`

- **Prometheus** (port 9090)
    - Metrics collection from Spring Boot app
    - Accessible at `http://localhost:9090`

### VS Code Extensions

Automatically installed in the container:

- Extension Pack for Java
- Spring Boot Dashboard
- Gradle for Java
- Docker
- GitLens
- Copilot (if available)
- SonarLint (code quality)

### VS Code Settings

Custom Java and editor settings are automatically applied:

- Java formatter and auto-format on save
- Proper Java home configuration
- Code rulers at 100 and 120 characters
- Automatic trailing whitespace trimming

## Usage

### Start the Application

```bash
# From the terminal inside the dev container
./gradlew.bat bootRun
```

The app will start on `http://localhost:8080`

### Run Tests

```bash
./gradlew.bat test
```

### Run Quality Checks

```bash
./gradlew.bat checkFormat
./gradlew.bat --no-problems-report pmdMain
./gradlew.bat asciidoctor
```

### Build Docker Image

```bash
./gradlew.bat dockerBuild
```

### Access Services from Host

The dev container forwards these ports to your host machine:

| Service | Port | URL |
|---------|------|-----|
| Spring Boot App | 8080 | http://localhost:8080 |
| Prometheus | 9090 | http://localhost:9090 |
| PostgreSQL | 5432 | postgresql://demo_user:demo_password@localhost:5432/technical_interview_demo |
| API Docs | 8080 | http://localhost:8080/docs |
| Readiness | 8080 | http://localhost:8080/actuator/health/readiness |

### PostgreSQL Connection

To connect to PostgreSQL from outside the container:

```bash
psql -h localhost -p 5432 -U demo_user -d technical_interview_demo
```

Or from your IDE:

- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `technical_interview_demo`
- **User:** `demo_user`
- **Password:** `demo_password`

## Lifecycle Scripts

The container runs these scripts in order:

1. **onCreateCommand.sh** - Runs once when container is first created
    - Makes scripts executable
    - Sets up basic permissions

2. **postCreateCommand.sh** - Runs once after container creation
    - Installs system dependencies
    - Downloads Gradle dependencies
    - Performs initial build (without tests)
    - Displays helpful information

3. **postStartCommand.sh** - Runs every time container starts
    - Verifies Docker availability
    - Shows ready status

## Tips & Tricks

### 1. Faster Builds

On the first run, Gradle dependencies are pre-downloaded. Subsequent builds will be much faster.

### 2. Share Maven/Gradle Cache

The container automatically mounts your host machine's `~/.m2` and `~/.gradle` directories. This speeds up builds and allows you to reuse dependencies from the host.

### 3. Working with Git

Git is available in the container. Your SSH keys and git config are available:

```bash
git clone <repo>
git commit -m "message"
```

### 4. Using Spring Boot Dashboard

Open the VS Code Spring Boot Dashboard to:

- Start/stop applications
- View running apps
- Access app endpoints
- View logs

### 5. Debug Mode

Set breakpoints in VS Code and use the debugger:

1. Click the Debug icon in the sidebar
2. Select "Java Debug"
3. Click "Run and Debug"
4. Breakpoints will trigger in the running application

### 6. Watch Mode

For continuous testing or compilation:

```bash
# Watch and run tests on file changes (requires a plugin)
./gradlew.bat test --watch
```

### 7. Hot Reload

Spring Boot DevTools is included. Changes to Java classes or resources may trigger auto-reload:

```bash
./gradlew.bat bootRun
```

Make code changes, and the app will restart automatically (if DevTools is enabled).

## Troubleshooting

### Container fails to build

**Problem:** Build times out or runs out of resources

**Solution:**

1. Increase Docker Desktop resources (CPU/Memory)
2. Clear Docker cache: `docker system prune`
3. Rebuild: `Ctrl+Shift+P` → "Dev Containers: Rebuild Container"

### Port already in use

**Problem:** Port 8080 or 5432 is already in use

**Solution:**

1. Find the process: `lsof -i :8080`
2. Kill it: `kill -9 <PID>`
3. OR use different port in `application.properties`

### Docker-in-Docker not available

**Problem:** `docker ps` returns error

**Solution:**

1. Ensure Docker Desktop is running
2. Restart VS Code
3. Rebuild the container

### Java not found

**Problem:** `java: command not found`

**Solution:**

1. The Java home may not be set correctly
2. Rebuild the container: `Ctrl+Shift+P` → "Dev Containers: Rebuild Container"

### PostgreSQL connection refused

**Problem:** Can't connect to database

**Solution:**

1. Ensure services are running: `docker ps`
2. Wait a few seconds for PostgreSQL to fully start
3. Check connection string: `postgresql://demo_user:demo_password@localhost:5432/technical_interview_demo`
4. Check network: Services must be on `dev-network` bridge

## Environment Variables

Key environment variables set in the container:

```
JAVA_HOME=/usr/lib/jvm/msopenjdk-current
GRADLE_USER_HOME=/root/.gradle
M2_HOME=/root/.m2
TZ=UTC
```

## File Structure

```
.devcontainer/
├── devcontainer.json       # Main configuration file
├── onCreateCommand.sh      # Initial setup script
├── postCreateCommand.sh    # Setup after container creation
├── postStartCommand.sh     # Runs on every container start
├── docker-compose.yml      # Services (PostgreSQL, Prometheus)
├── prometheus.yml          # Prometheus configuration
├── postgres-init.sql       # PostgreSQL initialization
└── README.md               # This file
```

## Advanced Configuration

### Modifying the Dev Container

Edit `.devcontainer/devcontainer.json` to:

1. **Add more VS Code extensions:**
   ```json
   "extensions": [
     "ms-vscode.vscode-todo-highlight",
     "...existing extensions..."
   ]
   ```

2. **Add more forwarded ports:**
   ```json
   "forwardPorts": [8080, 9090, 5432, 6379]
   ```

3. **Mount additional volumes:**
   ```json
   "mounts": [
     "...existing mounts...",
     "source=/var/run/docker.sock,target=/var/run/docker.sock,type=bind"
   ]
   ```

4. **Change the base image:**
   ```json
   "image": "eclipse-temurin:25-jdk-jammy"
   ```

### Adding Services

Edit `.devcontainer/docker-compose.yml` to add more services:

```yaml
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - dev-network
```

Then reference the container name in your app configuration:

```properties
spring.redis.host=redis
spring.redis.port=6379
```

## Exiting Dev Container

1. Click the green indicator in the bottom-left corner of VS Code
2. Select "Reopen Folder Locally"
3. VS Code will switch back to local development

**Note:** The container remains running. To stop it:

```bash
docker-compose -f .devcontainer/docker-compose.yml down
```

## Resources

- [VS Code Remote Development](https://code.visualstudio.com/docs/remote/remote-overview)
- [Dev Containers Documentation](https://containers.dev/)
- [Java Development Container Features](https://github.com/devcontainers/features)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)

## Support

For issues or questions:

1. Check VS Code's Remote - Containers documentation
2. Review the `postCreateCommand.sh` output for error details
3. Rebuild the container to ensure clean state
4. Check Docker Desktop logs for infrastructure issues

---

Happy coding! 🚀
