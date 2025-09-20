# This Dockerfile creates a containerized CI/testing environment for the September game engine.
# It configures Ubuntu 24.04 with Java 21, Maven, and a virtual display (Xvfb) to enable
# headless execution of OpenGL and OpenAL dependent tests and applications.

FROM ubuntu:24.04
# Prevent interactive prompts during package installation
ARG DEBIAN_FRONTEND=noninteractive

# Install core dependencies for the September engine CI environment:
# - xvfb: Virtual X11 server for headless graphics testing
# - mesa-utils: Provides Mesa OpenGL drivers for software rendering
# - openjdk-21-jdk: Java 21 development kit (required by engine)
# - maven: Build tool for compiling and testing the Java project
# - ca-certificates: SSL certificate authorities for secure downloads
# - libopenal1: OpenAL audio library for 3D audio testing
RUN apt-get update && apt-get install -y --no-install-recommends \
    xvfb \
    mesa-utils \
    openjdk-21-jdk \
    maven \
    ca-certificates \
    ca-certificates-java \
    libopenal1 \
    && update-ca-certificates \
    && /var/lib/dpkg/info/ca-certificates-java.postinst configure \
    && rm -rf /var/lib/apt/lists/*


# Fix X11 keyboard configuration to eliminate xkbcomp warning messages.
# This overwrites the default X11 'inet' symbols file with a minimal configuration
# to prevent "Could not resolve keysym" warnings that occur when Xvfb starts.
RUN \
    cat <<'EOF' > /usr/share/X11/xkb/symbols/inet
default partial alphanumeric_keys
xkb_symbols "evdev" {
};
EOF

# Override Mesa OpenGL version reporting for the September engine requirements.
# The engine requires OpenGL 4.6 support, but Mesa in CI environments may report
# a lower version even when 4.6 features are available. These overrides force
# Mesa to report version 4.6 with GLSL 460 support, enabling proper context creation.
ENV MESA_GL_VERSION_OVERRIDE=4.6
ENV MESA_GLSL_VERSION_OVERRIDE=460

# Configure OpenAL to use a null audio backend for headless testing.
# This provides complete OpenAL API compatibility while silently discarding all
# audio output, similar to how Xvfb provides headless graphics functionality.
# Essential for testing audio systems without requiring actual audio hardware.
ENV ALSOFT_DRIVERS=null

# Copy the September engine source code into the container
COPY . /workspace
WORKDIR /workspace

# Generate a robust entrypoint script that initializes the virtual display environment.
# This script handles Xvfb startup, waits for proper initialization, and ensures
# the X11 display is ready before executing the main container command.
RUN cat <<'EOF' > /usr/local/bin/entrypoint.sh && \
    chmod +x /usr/local/bin/entrypoint.sh
#!/bin/bash
set -e

# Configure the virtual display identifier for Xvfb
export DISPLAY=:99


# Configure Java to use system certificate store explicitly
export JAVA_OPTS="-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts -Djavax.net.ssl.trustStorePassword=changeit"
export MAVEN_OPTS="$JAVA_OPTS"

# Start the virtual X11 server in the background with a 1280x1024 resolution.
# The server creates a virtual framebuffer that applications can render to
# without requiring a physical display or graphics hardware.
Xvfb $DISPLAY -screen 0 1280x1024x24 &

# Implement robust waiting logic for X11 server initialization.
# The script actively polls for the X11 socket file to ensure the server
# is fully operational before proceeding with the main command execution.
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

# Execute the main command passed to the container (typically Maven build/test commands)
exec "$@"
EOF

# Set the entrypoint script as the container's main executable.
# This ensures that every container run will initialize the virtual display
# environment before executing any commands.
ENTRYPOINT ["entrypoint.sh"]

# Define the default command for container execution.
# Runs Maven in batch mode (-B) with no transfer progress (-ntp) to verify
# the September engine build, including compilation and full test suite execution.
CMD ["mvn", "-B", "-ntp", "verify"]
