# September Project Technical Documentation

This document provides a detailed overview of the technical architecture and systems within the September game project.

## 1. Project Structure

The project is divided into two main modules: `engine` and `game`.

### 1.1. Game Package

The `game` package contains the specific logic and assets for this particular game.

![Game Package Structure](package_diagram.puml)

### 1.2. Engine Package

The `engine` package provides the core functionalities like rendering, entity-component-system (ECS), and state management.

![Engine Package Structure](engine_package_diagram.puml)

## 2. Application Flow

The following diagrams illustrate the sequence of events from application startup to the main game loop.

### 2.1. Application Startup

This diagram shows the initial sequence of object creation and method calls when the application is launched.

![Application Startup](application_startup.puml)

### 2.2. High-Level Game Loop

This diagram shows the main game loop as managed by the `Engine`.

![Game Loop](game_loop.puml)

## 3. Game Loop and Order of Operations

### 3.1. Canonical Order of Operations

The core of the game's execution is the `update` cycle, which is initiated by the `Engine`'s `mainLoop`. The canonical order of operations is as follows:

1.  **Engine**: The `mainLoop` calls `update()` on the `GameStateManager`.
2.  **GameStateManager**: It calls `onUpdate()` on the currently active `GameState` (in this case, `PlayingState`).
3.  **PlayingState**: It calls `updateAll()` on the `SystemManager`.
4.  **SystemManager**: It iterates through all registered systems and calls `update()` on each one.

### 3.2. System Order of Operations

The order in which systems are updated is determined by the order in which they are registered in the `PlayingState`'s `onEnter` method. Based on the corrected code, the order is:

1.  `PlayerInputSystem`
2.  `MovementSystem`
3.  `EnemyAISystem`
4.  `RenderSystem`

The following sequence diagram provides a detailed look at the entire update cycle:

![Game Update Sequence](update_sequence.puml)

This concludes the technical documentation based on the current state of the codebase.
