# September LWJGL OpenGL 4.6 Application

September is a Java 21 Maven application that demonstrates OpenGL 4.6 Core Profile functionality using LWJGL (Lightweight Java Game Library) and GLFW for window creation. The application creates a window with OpenGL 4.6 context, displays system information, and cleanly terminates.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

Bootstrap, build, and test the repository:
- Install Java 21 and dependencies:
  - `sudo apt-get update && sudo apt-get install -y --no-install-recommends openjdk-21-jdk maven xvfb mesa-utils x11proto-core-dev`
  - `sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-21-openjdk-amd64/bin/java 1100 && sudo update-alternatives --set java /usr/lib/jvm/java-21-openjdk-amd64/bin/java`
  - `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`
- Build and test:
  - Unit tests only: `mvn test` -- takes ~6 seconds. NEVER CANCEL. Set timeout to 2+ minutes.
  - Full build with integration tests: `xvfb-run -a mvn -B verify` -- takes ~10 seconds. NEVER CANCEL. Set timeout to 2+ minutes.
  - Clean build: `mvn clean` -- takes <1 second
  - Compile only: `mvn compile` -- takes ~3 seconds
- Environment setup for headless operation:
  - `export MESA_GL_VERSION_OVERRIDE=4.6` -- REQUIRED for OpenGL 4.6 support
  - Use `xvfb-run -a` prefix for all commands that run integration tests or the main application

## Running the Application

- Via Maven (recommended): `xvfb-run -a mvn -B exec:java -Dexec.mainClass="io.thestacktracewhisperer.september.Main"` -- takes ~5 seconds including Maven overhead
- Via JAR with dependencies: `xvfb-run -a java -cp "target/september-1.0-SNAPSHOT.jar:$(find ~/.m2/repository -name "*.jar" | grep -E "(lwjgl|slf4j|logback)" | tr '\n' ':')" io.thestacktracewhisperer.september.Main` -- takes <1 second
- Application execution time: runs immediately, displays OpenGL info, sleeps 100ms, then terminates

## Validation

- ALWAYS manually validate any new code by running the full test suite with integration tests: `xvfb-run -a mvn -B verify`
- ALWAYS run through the complete application execution scenario after making changes: `xvfb-run -a mvn -B exec:java -Dexec.mainClass="io.thestacktracewhisperer.september.Main"`
- Expected application output includes:
  - OpenGL version: "4.6 (Core Profile) Mesa X.X.X"
  - OpenGL renderer: "llvmpipe (LLVM X.X.X, 256 bits)"
  - OpenGL vendor: "Mesa"
  - GLFW version: "3.4.0"
  - Window handle: numeric value (e.g., 140252161423936)
- Docker CI simulation (has certificate issues): `docker build -t september-ci .` -- takes ~1m25s. NEVER CANCEL. Set timeout to 3+ minutes.

## Test Structure

- Unit tests (Surefire): `**/*Test.java` (excluding IT variants)
  - Use Mockito static mocking for GLFW APIs
  - Run with: `mvn test`
- Integration tests (Failsafe): `**/*IT.java`, `**/*ITCase.java`, `**/*IntegrationTest.java`, `**/it/**/*Test.java`
  - Require working display (provided by Xvfb in headless environments)
  - Must use real APIs without mocking
  - Run with: `xvfb-run -a mvn verify` or `xvfb-run -a mvn integration-test`

## Critical Requirements

- **Java 21 REQUIRED**: Application requires Java 21. Set `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`
- **Xvfb REQUIRED for headless**: All integration tests and application runs in headless environments must use `xvfb-run -a` prefix
- **OpenGL 4.6 REQUIRED**: Set `export MESA_GL_VERSION_OVERRIDE=4.6` for proper OpenGL version
- **NEVER CANCEL builds or tests**: All Maven commands complete quickly (<10 seconds for full build), but set appropriate timeouts
- **Display dependency**: Integration tests and main application require a display (real or virtual via Xvfb)

## Build Artifacts and Dependencies

- Main artifact: `target/september-1.0-SNAPSHOT.jar`
- Additional artifacts: `target/september-1.0-SNAPSHOT-sources.jar`, `target/september-1.0-SNAPSHOT-javadoc.jar`
- Key dependencies: LWJGL 3.3.3 (core, GLFW, OpenGL), SLF4J 2.0.13, Logback, JUnit Jupiter 5.10.2, Mockito 5.19.0
- Platform-specific profiles: Automatically activate based on OS (linux/windows/mac) to include native LWJGL binaries

## Project Structure

- Main classes:
  - `io.thestacktracewhisperer.september.Main` -- Application entry point
  - `io.thestacktracewhisperer.september.GlfwContext` -- GLFW lifecycle management
  - `io.thestacktracewhisperer.september.WindowContext` -- OpenGL 4.6 window creation
- Test classes mirror main package structure with unit tests using mocks and integration tests using real APIs
- Documentation: `TESTING.md` contains comprehensive testing policy and headless CI guidance

## Common Commands Reference

### Repository exploration
```bash
ls -la
# .github/  .git/  .gitignore  Dockerfile  TESTING.md  denied.md  pom.xml  src/  target/
```

### Package structure
```bash
find src -name "*.java"
# src/main/java/io/thestacktracewhisperer/september/GlfwContext.java
# src/main/java/io/thestacktracewhisperer/september/Main.java
# src/main/java/io/thestacktracewhisperer/september/WindowContext.java
# src/test/java/io/thestacktracewhisperer/september/GlfwContextTest.java
# src/test/java/io/thestacktracewhisperer/september/MainIntegrationTest.java
# src/test/java/io/thestacktracewhisperer/september/MainTest.java
# src/test/java/io/thestacktracewhisperer/september/WindowContextTest.java
```

### Key Maven properties
```bash
grep -A5 -B5 "maven.compiler.release\|lwjgl.version\|junit" pom.xml
# maven.compiler.release: 21
# lwjgl.version: 3.3.3  
# junit.jupiter.version: 5.10.2
```

## Troubleshooting

- **Build fails with Java version error**: Ensure Java 21 is installed and `JAVA_HOME` is set correctly
- **Tests fail with display errors**: Use `xvfb-run -a` prefix for commands requiring display
- **OpenGL context creation fails**: Ensure `MESA_GL_VERSION_OVERRIDE=4.6` is set
- **Docker build fails with certificate errors**: This is a known limitation in some CI environments - use local Maven builds instead
- **Integration tests hang**: Ensure Xvfb is properly installed and running: `sudo apt-get install xvfb mesa-utils`

## Development Workflow

1. Always start with environment setup (Java 21, Xvfb, Mesa)
2. Run clean build with tests: `mvn clean && xvfb-run -a mvn -B verify`
3. Make code changes
4. Run unit tests for quick feedback: `mvn test`
5. Run full validation: `xvfb-run -a mvn -B verify`
6. Test application execution: `xvfb-run -a mvn -B exec:java -Dexec.mainClass="io.thestacktracewhisperer.september.Main"`
7. Verify expected output includes OpenGL 4.6 context creation and proper cleanup