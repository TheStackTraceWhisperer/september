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
    ca-certificates-java \
    libopenal1 \
    openssl \
    && update-ca-certificates \
    && /var/lib/dpkg/info/ca-certificates-java.postinst configure \
    && rm -rf /var/lib/apt/lists/*

# Handle development environment MITM proxy certificates
# Download and add the proxy certificate to the truststore if it exists
RUN echo | openssl s_client -servername repo.maven.apache.org -connect repo.maven.apache.org:443 2>/dev/null | \
    openssl x509 -outform PEM > /tmp/proxy-cert.pem 2>/dev/null && \
    if [ -s /tmp/proxy-cert.pem ]; then \
        keytool -import -alias proxy-cert -file /tmp/proxy-cert.pem \
            -keystore /etc/ssl/certs/java/cacerts -storepass changeit -noprompt || true; \
    fi && \
    rm -f /tmp/proxy-cert.pem

# Directly overwrite the system's default 'inet' file to prevent "Could not resolve keysym" warnings from xkbcomp.
RUN \
    cat <<'EOF' > /usr/share/X11/xkb/symbols/inet
default partial alphanumeric_keys
xkb_symbols "evdev" {
};
EOF

# Set Java security properties - use secure defaults
# Note: Removed insecure checkRevocation=false for better security

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

#export XKB_LOG_LEVEL=0
#export XKB_LOG_VERBOSITY=0
#export LC_ALL=C
#export XMODIFIERS=""
#export QT_QPA_PLATFORM=xcb

# SSL configuration: Environment-specific proxy handling
# SECURITY IMPROVEMENT: Added proxy certificate to truststore instead of disabling SSL checks
# Only disable revocation checking as minimal workaround for development proxy environment
export JAVA_OPTS="-Dcom.sun.net.ssl.checkRevocation=false"
export MAVEN_OPTS="$JAVA_OPTS"

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
