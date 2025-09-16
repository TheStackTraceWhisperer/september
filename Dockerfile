# Multi-stage Dockerfile for September Engine CI
# This Dockerfile is optimized for Docker layer caching to reduce build times.

# =============================================================================
# Stage 1: Base System Dependencies  
# This stage installs system packages and creates the runtime environment.
# This layer will be cached unless system dependencies change.
# =============================================================================
FROM ubuntu:24.04 AS base
ARG DEBIAN_FRONTEND=noninteractive

# Install system dependencies in a single layer for optimal caching
RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    mesa-utils \
    openjdk-21-jdk \
    maven \
    ca-certificates \
    libopenal1 \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Configure system settings that rarely change
# Directly overwrite the system's default 'inet' file to prevent "Could not resolve keysym" warnings from xkbcomp.
RUN cat <<'EOF' > /usr/share/X11/xkb/symbols/inet
default partial alphanumeric_keys
xkb_symbols "evdev" {
};
EOF

# Set environment variables for Java and graphics
ENV JAVA_OPTS="-Dcom.sun.net.ssl.checkRevocation=false"
ENV MESA_GL_VERSION_OVERRIDE=4.6
ENV MESA_GLSL_VERSION_OVERRIDE=460
ENV ALSOFT_DRIVERS=null

# =============================================================================
# Stage 2: Maven Dependencies Cache
# This stage downloads and caches Maven dependencies.
# This layer will be cached unless pom.xml changes.
# =============================================================================
FROM base AS dependencies

WORKDIR /workspace

# Copy pom.xml to leverage Docker layer caching for dependencies
COPY pom.xml ./

# Create a minimal Java file to enable compilation and dependency resolution
RUN mkdir -p src/main/java/temp && \
    echo 'package temp; public class Temp {}' > src/main/java/temp/Temp.java

# Download and cache all Maven dependencies by attempting to compile
# This forces Maven to download all plugin and dependency JARs
RUN export MAVEN_OPTS="-Dcom.sun.net.ssl.checkRevocation=false -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true" && \
    mvn compile -B -ntp || true

# Clean up the temporary file
RUN rm -rf src/

# =============================================================================  
# Stage 3: Application Build
# This stage copies source code and builds the application.
# This layer will be rebuilt whenever source code changes.
# =============================================================================
FROM dependencies AS builder

# Copy all source code (this layer will be rebuilt when source changes)
# Copy the source structure - either src/ or engine/+game/ directories
COPY . ./temp-sources/
RUN if [ -d "./temp-sources/src" ]; then \
        echo "Using traditional src/ structure" && \
        cp -r ./temp-sources/src ./src/; \
    elif [ -d "./temp-sources/engine" ] && [ -d "./temp-sources/game" ]; then \
        echo "Using modular engine/game structure" && \
        cp -r ./temp-sources/engine ./engine/ && \
        cp -r ./temp-sources/game ./game/; \
    else \
        echo "ERROR: Neither src/ nor engine/+game/ directories found" && \
        exit 1; \
    fi && \
    # Clean up temp directory but keep only essential files  
    find ./temp-sources -name "pom.xml" -exec cp {} ./ \; && \
    rm -rf ./temp-sources

# Build the application (dependencies are already cached from previous stage)
RUN export MAVEN_OPTS="-Dcom.sun.net.ssl.checkRevocation=false -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true" && \
    mvn compile -B -ntp

# =============================================================================
# Stage 4: Runtime Image  
# This stage creates the final runtime image with the entrypoint script.
# =============================================================================
FROM builder AS runtime

# Create the robust entrypoint script that sets up the virtual display
RUN cat <<'EOF' > /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh
#!/bin/bash
set -e

# Set the display variable for the virtual framebuffer
export DISPLAY=:99

# Set Maven options for SSL
export MAVEN_OPTS="-Dcom.sun.net.ssl.checkRevocation=false -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true"

# Start Xvfb in the background on the specified display
Xvfb $DISPLAY -screen 0 1280x1024x24 &

# ROBUST WAIT: Actively wait for the X server's socket to appear before proceeding.
XVFB_SOCKET="/tmp/.X11-unix/X99"
retries=0
while [ ! -S "$XVFB_SOCKET" ] && [ "$retries" -lt 30 ]; do
  retries=$((retries+1))
  sleep 0.1
done
if [ ! -S "$XVFB_SOCKET" ]; then
  echo "Xvfb failed to start after 3 seconds." >&2
  exit 1
fi

# Execute the main command passed to the container (e.g., mvn verify)
exec "$@"
EOF

# Set the entrypoint and default command
ENTRYPOINT ["entrypoint.sh"]
CMD ["mvn", "-B", "-ntp", "verify"]
