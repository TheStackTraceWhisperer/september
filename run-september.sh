#!/bin/bash

# Script to run September game engine with optimized X11/XKB configuration
# This demonstrates the proper way to minimize XKB warnings at the environment level

echo "Configuring environment for clean September execution..."

# Set X11 keyboard extension log levels to minimize verbosity
export XKB_LOG_LEVEL=0
export XKB_LOG_VERBOSITY=0

# Configure locale to prevent keyboard mapping inconsistencies
export LC_ALL=C

# For headless environments, minimize input method warnings  
if [[ "$DISPLAY" == :99* ]] || [[ "$DISPLAY" == *xvfb* ]]; then
    echo "Detected headless X11 environment, applying additional configuration..."
    export XMODIFIERS=""
    export QT_QPA_PLATFORM=xcb
fi

# Ensure OpenGL version for consistent behavior
export MESA_GL_VERSION_OVERRIDE=4.6

echo "Environment configured. Starting September..."

# Execute September with the configured environment
exec "$@"