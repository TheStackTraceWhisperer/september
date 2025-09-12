# ✓ Testing Policy

This document outlines the testing standards for the project. Our philosophy prioritizes high-confidence integration tests that verify the actual behavior of the engine and its components in a realistic environment.

## Core Testing Philosophy

1.  **Integration over Isolation**: We prefer integration tests over traditional unit tests. The goal is to test how components work together, not just their logic in isolation.
2.  **No Static Mocking**: We **do not** use static mocking for native libraries like LWJGL (`GLFW`, `OpenGL`). This practice is considered obsolete for this project as it leads to brittle tests that only verify implementation details.
3.  **The `EngineTestHarness` is Mandatory**: Any test for a class that directly or indirectly depends on a live OpenGL context **must** extend `september.engine.EngineTestHarness`. This is the cornerstone of our testing strategy.
4.  **Test Real Behavior**: Assertions should verify the observable outcome of an action (e.g., "did the entity's position change?"), not whether a specific method was called (e.g., `verify(transform).revertPosition()`).

## Testing Standards

There are three primary types of tests in this project, each with a specific purpose.

### 1. Harness-Based Integration Tests

-   **When to Use**: For any class that requires a live OpenGL context. This includes rendering components (`Mesh`, `Texture`, `Shader`, `OpenGLRenderer`) and any system that interacts with them (`RenderSystem`).
-   **How to Use**: Extend the `EngineTestHarness` class.
-   **What it Provides**: Before each test, the harness creates a complete, live engine instance with a valid OpenGL context, window, and all core services (`IWorld`, `ResourceManager`, `Camera`). It automatically handles cleanup after each test.

-   **Example (`MeshTest.java`)**:
    ```java
    class MeshTest extends EngineTestHarness {
        @Test
        void constructor_createsValidMesh_onGpu() {
            // The harness provides a live GL context.
            Mesh mesh = new Mesh(vertices, indices);
            // Assert that a real GPU resource was created.
            assertThat(mesh.getVaoId()).isPositive();
        }
    }
    ```

### 2. Pure Logic Tests (Harness-Free)

-   **When to Use**: For any class with no native dependencies. This includes most data components (e.g., `TransformComponent`, `HealthComponent`) and core data managers (`World`).
-   **How to Use**: Write a standard JUnit 5 test. **Do not** extend the harness.
-   **Benefit**: These tests are extremely fast as they do not incur the overhead of initializing a graphics context.

-   **Example (`WorldTest.java`)**:
    ```java
    class WorldTest {
        @Test
        void componentLifecycle() {
            IWorld world = new World();
            int entity = world.createEntity();
            // ... test component management logic ...
        }
    }
    ```

### 3. Hybrid Integration Tests (Strategic Mocking)

-   **When to Use**: For systems that have both native dependencies (requiring the harness) and external dependencies that are difficult to control in a test (e.g., user input, network, time).
-   **How to Use**: Extend `EngineTestHarness` and use Mockito to mock the specific interface that provides the uncontrollable input.
-   **Benefit**: This provides the best of both worlds: the system runs in a real `IWorld`, but its inputs are precisely and deterministically controlled by the test.

-   **Example (`PlayerInputSystemTest.java`)**:
    ```java
    @ExtendWith(MockitoExtension.class)
    class PlayerInputSystemTest extends EngineTestHarness {
        @Mock
        private InputMappingService mappingService; // Mock the input

        // ...

        @Test
        void setsMovementFlags_whenMoveActionsAreActive() {
            // The harness provides a real `world`
            // Create a real entity in the world
            // Use the mock `mappingService` to simulate input
            when(mappingService.isActionActive(...)).thenReturn(true);

            // Run the system and assert the state of the real component
        }
    }
    ```

---

## Common Standards

- **Frameworks**: JUnit 5 for test structure, AssertJ for fluent assertions, and Mockito for strategic interface mocking.
- **Test Data**: All test resources (shaders, textures) are located in `/src/test/resources` and loaded via the classpath.
- **CI Environment**: Integration tests run in a headless environment using a virtual framebuffer (Xvfb). The provided `Dockerfile` simulates this environment.
- **Local Execution**:
  - To run fast, harness-free tests: `mvn test`
  - To run all tests, including harness-based integration tests: `mvn verify`
