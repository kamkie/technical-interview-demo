# Optional Custom Dev Container Dockerfile
# This Dockerfile provides a more customized development environment
# To use this instead of the base image in devcontainer.json, change:
#   "image": "mcr.microsoft.com/devcontainers/java:1-25-bullseye"
# To:
#   "build": { "dockerfile": "Dockerfile", "context": "." }

FROM mcr.microsoft.com/devcontainers/java:1-25-bullseye

# Install additional development tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    # Build tools
    build-essential \
    cmake \
    # Version control
    git \
    git-flow \
    # Terminal utilities
    vim \
    nano \
    less \
    curl \
    wget \
    # Database clients
    postgresql-client \
    mongodb-mongosh \
    # Container tools
    docker.io \
    docker-compose \
    # Node.js for potential frontend work
    nodejs \
    npm \
    # Utilities
    jq \
    tldr \
    htop \
    # Timezone data
    tzdata \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Git completion scripts
RUN curl https://raw.githubusercontent.com/git/git/master/contrib/completion/git-completion.bash \
    -o /etc/bash_completion.d/git-completion.bash

# Configure default Java home
ENV JAVA_HOME="/usr/local/sdkman/candidates/java/current"
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Create non-root user (optional, mostly for security)
# Uncomment if you want to run as non-root user instead of root
# ARG USER_ID=1000
# ARG GROUP_ID=1000
# RUN groupadd -g ${GROUP_ID} devuser && \
#     useradd -m -u ${USER_ID} -g ${GROUP_ID} devuser
# USER devuser

# Set working directory
WORKDIR /workspace

# Label the image
LABEL maintainer="Technical Interview Demo Team"
LABEL description="Development container for Technical Interview Demo Spring Boot application"
LABEL version="1.0"

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD bash -c "java -version && gradle --version" || exit 1

# Default command
CMD ["/bin/bash"]

