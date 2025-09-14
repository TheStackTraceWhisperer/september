# September Engine - AI Development Guide

This document provides context and rules for AI assistants contributing to the September game engine.

### 1. Core Technology Stack

The project is built on a specific, modern Java ecosystem. All development must conform to these technologies:

- **Language:** Java 21
- **Core Library:** LWJGL 3.3.3 (for OpenGL, OpenAL, and GLFW bindings)
- **Math Library:** JOML 1.10.5
- **Architecture:** Entity-Component-System (ECS)
- **Logging:** SLF4J with a Logback backend
- **Build System:** Maven
- **Testing:** JUnit 5, Mockito, AssertJ, and ArchUnit

### 2. Fundamental Architectural Principles

The engine follows a strict architecture that must be preserved.

- **Engine vs. Game Separation:** The project is divided into two primary conceptual modules:
    - `september.engine`: The generic, reusable engine core. **This package must NEVER contain game-specific logic or depend on the `game` package.**
    - `september.game`: The specific implementation of the game, which depends on the engine.
    - This separation is programmatically enforced by `september.architecture.ArchitectureTest`.

- **Entity-Component-System (ECS):** All game objects and logic are built on the ECS pattern.
    - **Entity:** An ID.
    - **Component:** A plain data class/record (e.g., `TransformComponent`). Contains data, no logic.
    - **System:** A class implementing `ISystem`. Contains all logic for a specific domain (e.g., `MovementSystem`).

- **Hierarchical State Management:** The game's flow is managed by a two-tiered Finite State Machine (FSM):
    1.  **Main FSM:** Manages high-level states like `MainMenuState`, `LoadingState`, and `PlayingState`.
    2.  **Sub-FSM:** The `PlayingState` contains its own internal FSM to manage modes like `Exploring`, `In-Battle`, and `In-Menu`.

- **Resource Management:** All native resources (OpenGL/OpenAL, objects) are managed via `AutoCloseable` classes (e.g., `Mesh`, `Texture`, `AudioBuffer`) and are loaded/cached by the `ResourceManager`. This ensures deterministic cleanup and prevents memory leaks.

### 3. Build and Test Procedures (MANDATORY)

All code changes **must** be validated against the full test suite in an environment that correctly simulates our CI server.

#### Environment Setup
- **Dependencies (Ubuntu):**
  ```bash
  sudo apt-get update && sudo apt-get install -y --no-install-recommends openjdk-21-jdk maven xvfb mesa-utils
  ```
- **Critical Environment Variable:** The engine requires an OpenGL 4.6 context. For headless/CI environments, this variable is mandatory.
  ```bash
  export MESA_GL_VERSION_OVERRIDE=4.6
  ```

#### Key Maven Commands
- **Run Fast, Non-Harness Tests (Optional):** To run only the tests that do *not* extend the `EngineTestHarness` and therefore do not require a graphics context, you can use Maven's test exclusion patterns. This is useful for quick validation of pure logic changes.
  ```bash
  mvn test -Dtest="!*Harness"
  ```
- **Run All Tests (Unit and Integration), Complete Validation:** This command simulates the CI environment and runs the complete test suite. It **MUST** be used to verify any changes before committing.
  ```bash
  xvfb-run -a mvn verify
  ```

#### Running the Application
- To run the game from the command line, use the Maven `exec` plugin with the `xvfb-run` wrapper. Note the main class is now `september.game.Main`.
  ```bash
  xvfb-run -a mvn exec:java -Dexec.mainClass="september.game.Main"
  ```

### 4. Testing Philosophy

The project follows a strict, modern testing policy outlined in `TESTING.md`.

- **NO STATIC MOCKING:** Statically mocking LWJGL classes (`GLFW`, `GL11`, etc.) is strictly forbidden. It creates brittle tests that verify implementation, not behavior.
- **`EngineTestHarness` is MANDATORY for Integration Tests:** Any test that requires a live OpenGL or OpenAL context (e.g., for testing rendering, shaders, or audio) **must** extend `EngineTestHarness`, a hard requirement for consistency. The harness correctly sets up and tears down a live engine instance for each test.
- **Test Behavior, Not Implementation:** Assertions should check the observable outcome of an action (e.g., `assertThat(entity.position).isEqualTo(...)`) rather than verifying if a specific method was called.
- **Strategic Mocking:** Use Mockito for interfaces (`InputMappingService`, `TimeService`, etc.) to provide deterministic inputs to systems, but not to mock the engine's core functionality.

### 5. Guard Rails and Key Directives

- **Always Validate:** Before concluding, always run the full validation command: `xvfb-run -a mvn verify`.
- **Respect the Architecture:** Do not add game-specific code to the `engine` package. Create new services and systems as needed, following the established patterns.
- **Manage Native Resources:** Any new class that wraps a native OpenGL or OpenAL handle must implement `AutoCloseable` and be managed by the `ResourceManager`.
- **Follow the Code Style:** Adhere to the formatting and style rules defined in `CODESTYLE.md` and `.editorconfig`.
