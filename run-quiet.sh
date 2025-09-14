#!/bin/bash

# Script to run September game engine without noisy XKB keyboard warnings
# These warnings are harmless but create a lot of console noise during GLFW initialization

# Suppress XKB warnings by setting log level to 0
export XKB_LOG_LEVEL=0
export XKB_LOG_VERBOSITY=0

# Ensure we have the necessary environment variables
export MESA_GL_VERSION_OVERRIDE=4.6
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}

# Run the application with Maven
echo "Running September with suppressed XKB warnings..."
xvfb-run -a mvn -B exec:java -Dexec.mainClass="september.game.Main"