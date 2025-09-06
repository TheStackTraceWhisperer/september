# Testing Policy

This project separates unit tests and integration tests cleanly and runs both locally and in headless CI environments that provide a virtual display via Xvfb.

## Test Types

- Unit tests (Surefire)
  - Location/patterns: `**/*Test.java` (excluding IT variants by name or path as configured in surefire excludes)
  - Use Mockito (including static mocking) to isolate code under test.
  - It’s acceptable to statically mock GLFW in unit tests only.
  - No native or display dependencies should be required for unit tests beyond what’s already brought in by LWJGL.

- Integration tests (Failsafe)
  - Location/patterns: `**/*IT.java`, `**/*ITCase.java`, `**/*IntegrationTest.java`, and `**/it/**/*Test.java`
  - Must execute the true application path (no test-specific branches/flags). `MainIntegrationTest` invokes the same code paths as production.
  - Do not mock platform APIs in integration tests.
  - Require a working display. In CI this is provided via Xvfb.

## Headless CI Strategy (Xvfb Required)

- We do not change application behavior based on headless vs non-headless. Integration tests must run as if a real display is present.
- In headless CI, wrap Maven in Xvfb:

```bash
xvfb-run -a mvn -B clean verify
```

- Never set `GLFW_PLATFORM=null`. Tests must reflect real-platform operation. If a virtual display is needed, use Xvfb.

## Dockerfile: Headless CI Simulation

The provided `Dockerfile`:
- Uses a Maven + JDK 21 base image.
- Installs Xvfb and required X11 libraries for GLFW.
- Copies the project and runs `xvfb-run -a mvn -B -e -l build.log clean verify` during build.
- Default command prints the captured `build.log` when the container runs.

Common Docker workflow:

```bash
docker build -t september-ci .
docker run --rm september-ci
```

## LWJGL and Natives

- LWJGL Java bindings included: core and GLFW.
- OS-specific profiles (windows/linux/mac) bring in the corresponding native classifiers for LWJGL core and GLFW so unit tests and integration tests can initialize GLFW when needed.
- The window wrapper (`WindowContext`) uses `GLFW_NO_API` (no OpenGL context creation) to maximize compatibility in headless CI.

## Mocking Guidance

- Unit tests may statically mock:
  - `org.lwjgl.glfw.GLFW`
- Keep static mocking in unit tests only. Integration tests must use the real APIs under Xvfb.

## How to Run Locally

- Unit tests only:

```bash
mvn test
```

- Unit + integration tests (needs a display; use Xvfb if headless):

```bash
mvn verify
# or
xvfb-run -a mvn -B verify
```

## Expectations Summary

- No conditional logic to skip or alter behavior for tests in production code.
- Integration tests must exercise the mainline application path without mocks or test-only code paths.
- Headless builds must use Xvfb, not environment overrides like `GLFW_PLATFORM`.
- The Dockerfile exists to simulate a CI run with a virtual display and to persist the build log for review.
