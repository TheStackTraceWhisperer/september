# This Dockerfile sets up a Java 21 and Maven environment on Ubuntu 24.04
# with Xvfb for headless OpenGL rendering, explicitly setting OpenGL version to 4.6.

FROM ubuntu:24.04
ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    mesa-utils \
    openjdk-21-jdk \
    maven \
    x11proto-core-dev \
    && rm -rf /var/lib/apt/lists/*

# Explicitly set the OpenGL version to 4.6
ENV MESA_GL_VERSION_OVERRIDE=4.6

COPY . /workspace
WORKDIR /workspace

# Copy in the robust entrypoint script
RUN cat <<'EOF' > /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh
#!/bin/bash
set -e

# Set the display variable first
export DISPLAY=:99

# Start Xvfb in the background on the display specified by the variable
Xvfb $DISPLAY -screen 0 1280x1024x24 &

# Execute the main command passed to the container (e.g., mvn clean verify)
exec "$@"
EOF

# Set the new script as the main executable
ENTRYPOINT ["entrypoint.sh"]

# Set the default command to run
CMD ["mvn", "-ntp", "clean", "verify"]