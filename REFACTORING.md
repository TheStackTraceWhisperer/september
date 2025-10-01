# Dependency Injection and Event Listening Refactoring

## Overview
This document describes the architectural improvements made to the September game engine to better utilize dependency injection and event listening patterns, resulting in a more decoupled, maintainable, and testable codebase.

## Changes Made

### 1. GameState Dependency Injection
**Before:** States were manually instantiated using `new MainMenuState()` and `new PlayingState()`
**After:** States are now managed as singletons by the DI container and injected where needed

**Benefits:**
- States can now have their dependencies injected via constructor
- Better testability through dependency injection
- Follows the Dependency Inversion Principle

**Files Changed:**
- `engine/src/main/java/september/engine/core/Game.java` - Added ApplicationContext parameter to `getInitialState()`
- `engine/src/main/java/september/engine/core/Engine.java` - Passes ApplicationContext to game
- `game/src/main/java/september/game/Main.java` - Uses DI container to get initial state
- `game/src/main/java/september/game/state/MainMenuState.java` - Constructor injection for dependencies
- `game/src/main/java/september/game/state/PlayingState.java` - Constructor injection for dependencies

### 2. InputMappingService as Singleton
**Before:** `MultiDeviceMappingService` was manually instantiated in `PlayingState.onEnter()`
**After:** Marked as `@Singleton` and injected into states that need it

**Benefits:**
- Service lifecycle managed by DI container
- Can be shared across multiple states if needed
- Easier to test and mock

**Files Changed:**
- `game/src/main/java/september/game/input/MultiDeviceMappingService.java` - Added `@Singleton` annotation

### 3. System Creation Factories
**Before:** Systems were manually instantiated with `new RenderSystem(...)`, `new UISystem(...)`, etc.
**After:** Introduced factory classes that encapsulate system creation with proper dependency management

**New Classes:**
- `engine/src/main/java/september/engine/systems/SystemFactory.java` - Creates engine systems (RenderSystem, UIRenderSystem, MovementSystem)
- `engine/src/main/java/september/engine/systems/UISystemFactory.java` - Creates UI systems (UISystem)
- `game/src/main/java/september/game/systems/GameSystemFactory.java` - Creates game systems (PlayerInputSystem, EnemyAISystem, CollisionSystem)

**Benefits:**
- Centralized system creation logic
- Dependencies are injected into factories, not hard-coded
- Easier to add new systems or change dependencies
- Better separation of concerns
- Factories themselves are testable

### 4. Event Handling Improvements
**Before:** MainMenuState stored EngineServices as instance field to use in event handlers
**After:** Injected GameStateManager and PlayingState directly, reducing coupling

**Benefits:**
- Clearer dependencies
- Less coupling to the entire EngineServices aggregate
- Better follows Single Responsibility Principle

### 5. Dead Code Removal
**Before:** `World` class created an unused `SystemManager` instance
**After:** Removed dead code

**Benefits:**
- Cleaner code
- Reduced memory footprint
- Less confusion about which SystemManager is being used

## Testing

### New Tests Added
- `SystemFactoryTest.java` - Tests for engine system factory (4 tests)
- `UISystemFactoryTest.java` - Tests for UI system factory (2 tests)
- `GameSystemFactoryTest.java` - Tests for game system factory (4 tests)

### Test Results
- **Engine Unit Tests:** 140 tests passing
- **Game Unit Tests:** 44 tests passing
- **Total:** 184 unit tests passing

### Updated Tests
- `EngineTestHarness.java` - Updated to new Game interface signature
- `EngineTest.java` - Updated to new Game interface signature
- `MainMenuStateTest.java` - Updated to use constructor injection with mocks

## Architecture Improvements

### Decoupling
1. **States are decoupled from each other** - MainMenuState no longer directly instantiates PlayingState
2. **Systems are decoupled from infrastructure** - Factories hide the complexity of dependency management
3. **Services are decoupled from creation** - DI container manages lifecycles

### Testability
1. **Constructor injection** makes it easy to pass mocks in tests
2. **Factory pattern** allows testing system creation independently
3. **Singleton services** can be easily mocked or replaced for testing

### Maintainability
1. **Single Responsibility** - Each factory has one job: create systems
2. **Open/Closed Principle** - Easy to add new systems without modifying existing code
3. **Dependency Inversion** - High-level modules depend on abstractions (factories) not concrete classes

## Design Patterns Used

1. **Dependency Injection** - Constructor injection throughout
2. **Factory Pattern** - System factories encapsulate object creation
3. **Singleton Pattern** - Services managed as singletons by DI container
4. **Event Listener Pattern** - Micronaut's @EventListener for decoupled event handling

## Code Smells Eliminated

1. ✅ **Manual object creation** - Replaced with DI and factories
2. ✅ **Direct dependencies** - Replaced with injected dependencies
3. ✅ **God object** - EngineServices no longer needed everywhere
4. ✅ **Dead code** - Unused SystemManager removed from World
5. ✅ **Tight coupling** - States and systems now loosely coupled

## Future Improvements

While this refactoring significantly improves the codebase, some additional improvements could be considered:

1. **System lifecycle management** - Could create a SystemRegistry to manage system registration/unregistration
2. **State transitions** - Could create a StateTransition object to encapsulate state changes
3. **Event system enhancement** - Could add more event types for different game actions
4. **Configuration externalization** - Some factory configurations could be moved to config files

## Conclusion

This refactoring successfully addressed the requirements to:
- ✅ Make better use of dependency injection throughout the application
- ✅ Improve event listening patterns with Micronaut's event system
- ✅ Decouple the entire application
- ✅ Eliminate code smells
- ✅ Keep test cases updated
- ✅ Fortify test cases with new comprehensive tests

All unit tests pass, demonstrating that the refactoring maintains existing functionality while improving code quality and architecture.
