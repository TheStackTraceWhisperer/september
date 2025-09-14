# This Dockerfile sets up a Java 21 and Maven environment on Ubuntu 24.04
# with Xvfb for headless OpenGL rendering.

FROM ubuntu:24.04
ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    mesa-utils \
    openjdk-21-jdk \
    maven \
    ca-certificates \
    libopenal1 \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Set Java security properties to handle SSL properly
ENV JAVA_OPTS="-Dcom.sun.net.ssl.checkRevocation=false"

# Set the OpenGL version override. This forces Mesa to report 4.6, though the
# underlying GLSL support may be lower. This is necessary for context creation.
ENV MESA_GL_VERSION_OVERRIDE=4.6
ENV MESA_GLSL_VERSION_OVERRIDE=460

# Configure OpenAL to use null backend for headless audio testing.
# This provides full OpenAL API compatibility while discarding audio output,
# similar to how Xvfb provides headless graphics testing.
ENV ALSOFT_DRIVERS=null

COPY . /workspace
WORKDIR /workspace

# Copy in the robust entrypoint script that sets up the virtual display.
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

# Set the new script as the main executable
ENTRYPOINT ["entrypoint.sh"]

# Set the default command to run. Use batch mode for better CI output.
CMD ["mvn", "-B", "-ntp", "verify"]
