# Use Maven with JDK 21
FROM maven:3.9.9-eclipse-temurin-21

# Install Xvfb and required X11 libs for GLFW window creation
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       xvfb xauth \
       libx11-6 libxrandr2 libxinerama1 libxcursor1 libxi6 libgl1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

# Copy project files
COPY pom.xml ./
COPY src ./src

# Build and run tests (unit + integration) under Xvfb to simulate headless CI
# Use Maven's log file flag to capture output in build.log
RUN xvfb-run -a mvn -B -e -l build.log clean verify

# Default command: show the captured build log
CMD ["bash", "-lc", "cat build.log"]
