# September - Java LWJGL Graphics Application

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Bootstrap, Build, and Test the Repository
- Install Java 21: `sudo apt-get update && sudo apt-get install -y openjdk-21-jdk`
- Set Java 21 as default: `sudo update-alternatives --set java /usr/lib/jvm/temurin-21-jdk-amd64/bin/java`
- Verify setup: `export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64 && java --version && mvn --version`
- Install display dependencies: `sudo apt-get install -y xvfb mesa-utils x11proto-core-dev`
- Clean compile: `mvn clean compile` -- takes ~20 seconds. NEVER CANCEL. Set timeout to 120+ seconds.
- Unit tests only: `mvn test` -- takes ~15 seconds. NEVER CANCEL. Set timeout to 120+ seconds.
- Full build with integration tests: `export MESA_GL_VERSION_OVERRIDE=4.6 && xvfb-run -a mvn -B verify` -- takes ~25 seconds. NEVER CANCEL. Set timeout to 180+ seconds.

### Run the Application
- ALWAYS run the bootstrapping steps first.
- Main application: `export MESA_GL_VERSION_OVERRIDE=4.6 && xvfb-run -a mvn exec:java -Dexec.mainClass="io.thestacktracewhisperer.september.Main" -Dexec.classpathScope=compile`
- The application creates a GLFW window, logs OpenGL info, waits 100ms, then exits cleanly.

### Docker CI Simulation
- Build Docker image: `docker build -t september-ci .` -- takes ~70 seconds. NEVER CANCEL. Set timeout to 300+ seconds.
- NOTE: Docker container execution may fail due to SSL/network issues in headless environments. Use local builds for validation.

## Validation

### Manual Testing Requirements
- ALWAYS run through complete build and test cycle after making changes.
- ALWAYS verify the main application runs without errors using Xvfb.
- Test both unit tests (mocked GLFW) and integration tests (real GLFW with Xvfb).
- Unit tests: `mvn test` - should see mocked GLFW interactions
- Integration tests: `export MESA_GL_VERSION_OVERRIDE=4.6 && xvfb-run -a mvn integration-test` - should see real OpenGL initialization
- Main application test scenario: Run `export MESA_GL_VERSION_OVERRIDE=4.6 && xvfb-run -a mvn exec:java -Dexec.mainClass="io.thestacktracewhisperer.september.Main"` and verify logs show:
  - Window handle creation
  - GLFW initialization 
  - GLFW version information
  - Clean window destruction

### CI Build Compatibility
- The GitHub Actions CI uses same Xvfb approach: `xvfb-run -s "-screen 0 1920x1080x24" mvn -B clean verify`
- Always set `MESA_GL_VERSION_OVERRIDE=4.6` for consistent OpenGL version

## Common Tasks

### Repository Structure
```
/home/runner/work/september/september/
├── .github/workflows/ci.yml    # GitHub Actions CI pipeline
├── Dockerfile                  # CI simulation container
├── TESTING.md                  # Comprehensive testing documentation
├── pom.xml                     # Maven project configuration
├── src/main/java/io/thestacktracewhisperer/september/
│   ├── Main.java              # Application entry point
│   ├── GlfwContext.java       # GLFW initialization wrapper
│   └── WindowContext.java     # Window creation and management
└── src/test/java/io/thestacktracewhisperer/september/
    ├── MainIntegrationTest.java  # Integration test for main path
    ├── GlfwContextTest.java      # Unit tests with mocked GLFW
    └── WindowContextTest.java    # Unit tests with mocked GLFW
```

### Key Project Components
- **Main.java**: Entry point that creates GLFW context, window, logs OpenGL info, then exits
- **GlfwContext.java**: RAII wrapper for GLFW initialization/termination
- **WindowContext.java**: RAII wrapper for GLFW window creation/destruction with OpenGL 4.6 core profile
- **Testing Strategy**: Unit tests mock GLFW static methods; integration tests use real GLFW under Xvfb

### Build Configuration Summary
- **Java Version**: 21 (enforced by maven-enforcer-plugin)
- **LWJGL Version**: 3.3.3 with platform-specific natives (Linux/Windows/Mac profiles)
- **Test Separation**: Surefire for unit tests, Failsafe for integration tests
- **Coverage**: JaCoCo with separate unit/integration/merged reports
- **OpenGL**: Uses Mesa software rendering with version override for consistent CI behavior

### Timing Expectations
- **Compilation**: ~20 seconds
- **Unit Tests**: ~15 seconds  
- **Full Verify**: ~25 seconds
- **Docker Build**: ~70 seconds
- These are FAST builds, not the multi-hour builds warned about in some documentation templates.

### Critical Requirements
- **Java 21**: Required by enforcer plugin
- **Xvfb**: Required for integration tests in headless environments
- **MESA_GL_VERSION_OVERRIDE=4.6**: Ensures consistent OpenGL version
- **NEVER use GLFW_PLATFORM=null**: Always use Xvfb for virtual display instead

### Common Commands Reference
```bash
# Bootstrap environment
sudo apt-get update && sudo apt-get install -y openjdk-21-jdk xvfb mesa-utils x11proto-core-dev
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export MESA_GL_VERSION_OVERRIDE=4.6

# Development cycle
mvn clean compile          # Compile only
mvn test                   # Unit tests only
xvfb-run -a mvn verify     # Full build with integration tests
xvfb-run -a mvn exec:java -Dexec.mainClass="io.thestacktracewhisperer.september.Main"  # Run app

# CI simulation
docker build -t september-ci .
# Note: Container run may fail due to network issues, use local builds for validation
```