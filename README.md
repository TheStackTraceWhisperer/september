# September

[![CI](https://github.com/TheStackTraceWhisperer/september/actions/workflows/ci.yml/badge.svg)](https://github.com/TheStackTraceWhisperer/september/actions/workflows/ci.yml)
# September Engine

A lightweight, modern, and ECS-based game engine written in Java 21 with LWJGL 3 for creating 2D games.

---

## Overview

September is a game engine designed from the ground up with a focus on clean architecture and modern development practices. It leverages a strict **Entity-Component-System (ECS)** pattern to provide a highly modular and data-driven foundation for game development.

The project is architecturally separated into a generic `engine` module and a specific `game` module, ensuring that core engine logic remains completely independent and reusable. This design, combined with a comprehensive testing suite and a fully automated CI/CD pipeline, makes it an excellent foundation for building complex 2D games, particularly those with mechanics similar to classic JRPGs.

## Core Features

- **Modern Tech Stack**: Built on **Java 21** and **LWJGL 3.3.3**, using modern language features and high-performance libraries for graphics, audio, and input.
- **ECS Architecture**: A clean and efficient Entity-Component-System design that favors composition over inheritance, enforced by the `IWorld` interface and `SystemManager`.
- **State Management Framework**: A built-in hierarchical Finite State Machine (FSM) to manage high-level game states (`MainMenu`, `Playing`, `Paused`) and complex internal states (e.g., `Exploring` vs. `In-Battle`).
- **OpenGL 4.6 Rendering**: A rendering pipeline built on a modern OpenGL 4.6 Core Profile, featuring both a simple sprite renderer and a high-performance `InstancedOpenGLRenderer` for batching thousands of sprites.
- **OpenAL 3D Audio**: A complete audio system that supports background music with fades (`MusicComponent`), one-shot sound effects (`SoundEffectComponent`), and 3D positional audio (`AudioSourceComponent`).
- **Multi-Device Input**: A flexible input system that supports both keyboard and gamepads, with an abstraction layer (`InputMappingService`) to map raw inputs to game actions.
- **Multi-Platform Support**: A Maven build configured with profiles to automatically handle native dependencies for Windows, macOS, and Linux.
- **Comprehensive Testing Suite**: A robust testing philosophy that prioritizes high-confidence integration tests using a real graphics context, enforced by the `EngineTestHarness`. Static mocking of LWJGL is forbidden.
- **Automated CI/CD Pipeline**: A GitHub Actions workflow that automatically builds, tests, and creates a GitHub Release every time a tag is pushed to the `main` branch.

## Getting Started

### Prerequisites
- Java 21
- Maven
- For headless environments (like CI/CD): `xvfb` and `mesa-utils`

### Build and Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/TheStackTraceWhisperer/september.git](https://github.com/TheStackTraceWhisperer/september.git)
    cd september
    ```

2.  **Set Environment for Headless (if applicable):**
    This is required for running graphics-dependent tests and applications in a CI environment.
    ```bash
    export MESA_GL_VERSION_OVERRIDE=4.6
    export MESA_GLSL_VERSION_OVERRIDE=460
    ```

3.  **Run the Full Build:**
    This command compiles the code and runs the entire test suite, including graphics-dependent integration tests.
    ```bash
    # On a headless system (like Linux servers or CI)
    xvfb-run -a mvn verify

    # On a desktop with a graphical display
    mvn verify
    ```

4.  **Run the Game:**
    ```bash
    # On a headless system
    xvfb-run -a mvn exec:java -Dexec.mainClass="september.game.Main"

    # On a desktop
    mvn exec:java -Dexec.mainClass="september.game.Main"
    ```

## Documentation

For more detailed information, please refer to the project's documentation:

- **[ARCHITECTURE.md](thestacktracewhisperer/september/september-5fbc7137eff6cd1f33f21cfd0323984a62b6bd9d/ARCHITECTURE.md)**: A deep dive into the ECS pattern and engine/game separation.
- **[TESTING.md](thestacktracewhisperer/september/september-5fbc7137eff6cd1f33f21cfd0323984a62b6bd9d/TESTING.md)**: The mandatory testing philosophy and a guide to writing effective tests.
- **[CODESTYLE.md](thestacktracewhisperer/september/september-5fbc7137eff6cd1f33f21cfd0323984a62b6bd9d/CODESTYLE.md)**: The official code style and formatting guide.
- **[AUDIO.md](thestacktracewhisperer/september/september-5fbc7137eff6cd1f33f21cfd0323984a62b6bd9d/AUDIO.md)**: A detailed guide to the audio system and its components.
- **[docs/controls.md](thestacktracewhisperer/september/september-5fbc7137eff6cd1f33f21cfd0323984a62b6bd9d/docs/controls.md)**: The default keyboard and gamepad control mappings.
- **[docs/state-management-design.md](thestacktracewhisperer/september/september-5fbc7137eff6cd1f33f21cfd0323984a62b6bd9d/docs/state-management-design.md)**: The architectural blueprint for the state and scene management systems.
