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

- **Mocking**: The project uses Mockito for mocking. The strategy depends on the type of class being tested.

  - **Philosophy**: The goal of mocking is to isolate the unit under test from its external dependencies. This makes tests faster, more reliable, and more focused by replacing real dependencies with predictable, lightweight fakes.

  - **Static API Mocking (for Native Calls)**:
    - **When**: Use this for classes that make direct, static calls to LWJGL APIs.
    - **How**: Use Mockito's `mockStatic` feature.
    - **Why**: To remain platform-agnostic and avoid needing a real display or native libraries.
    - **Examples**: `org.lwjgl.glfw.GLFW`, `org.lwjgl.opengl.GL`, `org.lwjgl.opengl.GL11`.

  - **Dependency Injection Mocking (for All Other Classes)**:
    - **When**: Use this for classes that do *not* make direct native calls, but instead rely on other objects.
    - **How**: Pass mocked instances of its dependencies into the class's constructor or methods.
    - **Example**: The `Engine` class does not call `GLFW` directly. It relies on other services (like a `WindowContext`). To test `Engine`, you would pass a *mocked* `WindowContext` to it.

## Advanced Mocking & Common Pitfalls

While the principles above cover the basics, testing code that interacts with native libraries like LWJGL can lead to several common, subtle issues. This section documents these pitfalls and their solutions to ensure tests remain clean and robust.

<br>

> **Warning: `mockStatic` is Mandatory for Native Calls**
> 
> To be perfectly clear: any class that directly or indirectly makes a call to an LWJGL (`org.lwjgl.*`) method **must** be tested using `mockStatic`. Features like `mockConstruction` are not a substitute and will lead to unpredictable failures. When in doubt, mock the static native class.

<br>

### Pitfall 1: `TooManyActualInvocations` Error

-   **Symptom**: A test fails with `org.mockito.exceptions.verification.TooManyActualInvocations`. The message shows a method was called more times than the test `verify()` expected (e.g., "Wanted 1 time... but was 2 times").
-   **Cause**: This often happens when a class's **constructor** calls a native method (e.g., `glBindTexture`) and the **test method** also calls a method that invokes the *same* native function. The call from the constructor "leaks" into the test's verification phase.
-   **Solution**: Isolate the test's verification by clearing the mock's invocation history immediately after the object is constructed.

-   **Example**:
    ```java
    @Test
    void bind_activatesAndBindsTexture() {
        // --- Arrange ---
        // The constructor for Texture() calls glBindTexture(..., 1) and glBindTexture(..., 0)
        Texture texture = new Texture(ByteBuffer.allocate(1));

        // CRITICAL: Clear the mock's history to ignore calls made by the constructor.
        gl11.clearInvocations();

        // --- Act ---
        texture.bind(5);

        // --- Assert ---
        // Now, this verification will only count the single call made inside the bind() method.
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 1));
    }
    ```

### Pitfall 2: `UnnecessaryStubbingException` Error

-   **Symptom**: A test fails with `org.mockito.exceptions.misusing.UnnecessaryStubbingException`.
-   **Cause**: Mockito's strict stubbing rules detect that a stubbing configured in a `@BeforeEach` method was not used by a specific test case. This indicates a poorly scoped mock setup.
-   **Solution**: Move stubs from the general `@BeforeEach` method into the specific `@Test` methods that actually require them. This makes each test more self-contained and easier to understand.

-   **Example**:
    ```java
    // BAD: Stubs are too broad. The endScene_unbindsProgram test doesn't use the camera.
    @BeforeEach
    void setUp() {
        renderer = new OpenGLRenderer();
        // This will cause an error in tests that don't involve the camera.
        when(camera.getProjectionMatrix()).thenReturn(new Matrix4f());
        when(camera.getViewMatrix()).thenReturn(new Matrix4f());
    }

    // GOOD: Stubs are scoped to the test that needs them.
    @Test
    void beginScene_clearsAndUsesProgram() {
        // Arrange: Stub camera matrices specifically for this test.
        when(camera.getProjectionMatrix()).thenReturn(new Matrix4f());
        when(camera.getViewMatrix()).thenReturn(new Matrix4f());

        // Act & Assert...
    }

    @Test
    void endScene_unbindsProgram() {
        // This test no longer inherits unnecessary stubs.
        // Act & Assert...
    }
    ```

### Pitfall 3: Incorrect `InOrder` Verification Syntax

-   **Symptom**: A test fails to compile with an "incompatible types" error when using `inOrder.verify()`.
-   **Cause**: The syntax for verifying static mocks with `InOrder` is different from verifying regular mocks. It requires passing the `MockedStatic` object as the first argument.
-   **Solution**: Use the two-argument `inOrder.verify(mock, lambda)` method.

-   **Example**:
    ```java
    // BAD: Compilation Error
    InOrder inOrder = inOrder(gl);
    inOrder.verify(() -> glUseProgram(0));

    // GOOD: Correct Syntax
    InOrder inOrder = inOrder(gl);
    inOrder.verify(gl, () -> glUseProgram(0));
    inOrder.verify(gl, () -> glDeleteProgram(PROGRAM_ID));
    ```

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

## Test Data Management

When tests require external data (e.g., configuration files, shaders, textures), follow these guidelines:

- **Location**: Place all test-specific resources in `/src/test/resources`.
- **Access**: Load resources using standard classpath lookups (e.g., `getClass().getResourceAsStream(...)`). This ensures tests run correctly both in the IDE and in Maven builds.
- **Separation**: Keep test resources separate from production resources in `/src/main/resources` to prevent them from being bundled with the final application.
- **Cleanup**: Because tests read from the classpath, file-based cleanup is not typically required. Each test should be independent and not rely on state left by previous tests.

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
