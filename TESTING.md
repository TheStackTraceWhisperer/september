# ✓ Testing Policy

This document outlines the testing standards for the project, covering common principles, unit testing, and integration testing.

## Common Standards

- **No Test-Specific Production Code**: Production code must not contain conditional logic to skip or alter behavior for tests.
- **LWJGL & Natives**:
  - The project includes LWJGL bindings for `core` and `GLFW`.
  - OS-specific Maven profiles (`windows`/`linux`/`mac`) automatically include the correct native classifiers.
- **Local Execution**:
  - To run unit tests only: `mvn test`
  - To run all tests (unit + integration): `mvn verify`

## Unit Test Standards

- **Framework**: Maven Surefire Plugin
- **Location/Patterns**: `**/*Test.java` (excluding integration test patterns).
- **Goal**: Isolate and test a single unit of code.
- **Dependencies**: No native or display dependencies are required.
- **Mocking**:
  - Use Mockito to isolate the code under test.
  - Unit tests must mock all LWJGL API classes to remain platform-agnostic. This includes, but is not limited to, static mocking for the following classes:
    - `org.lwjgl.glfw.GLFW`
    - `org.lwjgl.opengl.GL`
    - `org.lwjgl.opengl.GL11`
    - This only applies to classes that actually make glfw/gl calls, Engine for example would just use mocked dependencies

## Integration Test Standards

- **Framework**: Maven Failsafe Plugin
- **Location/Patterns**: `**/*IT.java`, `**/*ITCase.java`, `**/*IntegrationTest.java`, and `**/it/**/*Test.java`.
- **Goal**: Test the application's mainline path with real dependencies.
- **Execution**:
  - Must execute the true application path (e.g., `MainIntegrationTest` invokes production code paths).
  - No test-specific branches or feature flags in production code.
- **Mocking**: Do not mock platform APIs (like GLFW).
- **Display Requirement**: A working display is required.

### Headless CI for Integration Tests

Integration tests must run as if a real display is present, even in headless environments.

- **Strategy**: Use a virtual framebuffer like **Xvfb**. Never use environment overrides like `GLFW_PLATFORM=null`.
- **Local Headless Execution**:
  ```bash
  xvfb-run -a mvn -B verify
  ```
- **OpenGL Context**: The window wrapper (`WindowContext`) creates an OpenGL 4.6 core profile context. The CI environment must provide this.

### Docker CI Simulation

The provided `Dockerfile` simulates a CI run with a virtual display.

- **Environment**: Ubuntu 24.04, JDK 21, Maven, Xvfb, and Mesa.
- **Mechanism**: A custom `entrypoint.sh` starts `Xvfb` and then executes the Maven build. This is more robust than `xvfb-run` inside a container.
- **OpenGL Version**: The environment variable `MESA_GL_VERSION_OVERRIDE=4.6` is set to ensure the headless environment provides the exact OpenGL version required by the application.
- **Workflow**:
  ```bash
  # Build the CI image
  docker build -t september-ci .

  # Run the tests inside the container
  docker run --rm september-ci

  # View logs if needed
  docker logs <container_id>
  ```

---

## Loop Policy Usage (MainLoopPolicy)

The application loop behavior is controlled via the functional `MainLoopPolicy` API. This is intentionally lightweight, side-effect free, and safe to use directly in tests without mocking.

### Available Policies
- `MainLoopPolicy.standard()` — Runs until the native window is closed (unbounded for automated tests).
- `MainLoopPolicy.frames(n)` — Runs a fixed number of frames (0 skips the loop entirely). Ideal for deterministic unit tests.
- `MainLoopPolicy.initializeOnly()` — Alias for `frames(0)`; performs initialization only.
- `MainLoopPolicy.timed(duration)` — Runs until the duration elapses AND (if using the standard policy in composition) the window remains open.
- `MainLoopPolicy.all(p1, p2, ...)` — Logical AND composition; stops when any member stops.
- `MainLoopPolicy.any(p1, p2, ...)` — Logical OR composition; continues while any member continues.

### Testing Guidance
- **Unit Tests**: Prefer `frames(0)` or `frames(1)` to avoid long-running loops. Example: `new Main(MainLoopPolicy.frames(0)).run();`
- **Integration Tests**: Use a small bounded policy like `frames(1)` or a short `timed(Duration.ofMillis(50))` to keep builds fast while exercising real paths.
- **Do Not Mock the Policy**: It's pure and already deterministic; mocking adds no value.
- **No Branches in Production**: Policy selection happens in test construction only—never gate production logic on test conditions.

### Example
```java
// Unit test style: initialize only (no frames)
new Main(MainLoopPolicy.initializeOnly()).run();

// Short integration run: one frame
new Main(MainLoopPolicy.frames(1)).run();

// Composite: run up to 100 frames OR 150 ms, whichever stops first
new Main(MainLoopPolicy.any(
    MainLoopPolicy.frames(100),
    MainLoopPolicy.timed(Duration.ofMillis(150))
)).run();
```

### Rationale
Using policies instead of ad-hoc sleeps or flags ensures:
- Deterministic test duration
- No reliance on `Thread.sleep` hacks
- Clear separation between control logic (tests) and application behavior (production)

---

