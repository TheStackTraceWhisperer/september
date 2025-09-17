# Input System Testing - High Priority

## Summary
The `september.engine.core.input` package has **6% test coverage**, with critical input handling completely untested. This affects user interaction and game control systems.

## Affected Classes  
- `GlfwInputService` - 13% coverage (70 missed instructions, 8 missed branches)
- `GlfwGamepadService` - 3% coverage (82 missed instructions, 18 missed branches)
- Input callback classes - 0% coverage (anonymous inner classes)
- Interface definitions (`InputService`, `GamepadService`) - No tests

## Current State
- ❌ Keyboard input handling untested
- ❌ Mouse input handling untested  
- ❌ Gamepad support completely untested
- ❌ Input callback registration and execution untested
- ❌ Input state management unverified

## Required Testing Strategy

### Test Types Needed

#### Interface/Contract Tests
Test interface contracts with mock implementations:
- [ ] `InputService` interface behavior
- [ ] `GamepadService` interface behavior
- [ ] Service lifecycle management

#### Integration Tests with Strategic Mocking
Use `EngineTestHarness` with mocked GLFW interactions:
- [ ] Input service initialization
- [ ] Key press/release handling
- [ ] Mouse button and position tracking
- [ ] Gamepad connection and input
- [ ] Input callback registration

### Specific Test Scenarios

#### GlfwInputService Tests
```java
@ExtendWith(MockitoExtension.class)  
class GlfwInputServiceIT extends EngineTestHarness {
    @Test void keyPress_updatesInputState();
    @Test void mouseButton_registersCorrectly();
    @Test void cursorPosition_tracksMovement();
    @Test void multipleInputs_handleConcurrently();
}
```

#### GlfwGamepadService Tests
```java
class GlfwGamepadServiceIT extends EngineTestHarness {
    @Test void gamepadConnection_detectsAutomatically();
    @Test void analogStick_reportsAccurateValues();
    @Test void buttonPress_registersImmediately();
    @Test void gamepadDisconnection_handlesGracefully();
}
```

#### Input Callback Tests
```java
class InputCallbackTest {
    @Test void keyCallback_forwardsToService();
    @Test void mouseCallback_updatesState();
    @Test void errorHandling_doesNotCrash();
}
```

## Implementation Challenges

### Strategic Mocking Approach
Since we don't mock LWJGL statically, we need to:
- Mock at the service interface level
- Test observable state changes rather than GLFW calls
- Use dependency injection where possible
- Create testable adapters for GLFW interactions

### Test Environment Considerations
- Input tests may require special handling in headless environment
- Mock gamepad devices for consistent testing
- Simulate input events programmatically

## Acceptance Criteria
- [ ] All input service interfaces have contract tests
- [ ] GlfwInputService has >70% test coverage
- [ ] GlfwGamepadService has >70% test coverage  
- [ ] Input callbacks have basic functionality tests
- [ ] Tests verify input state management
- [ ] Tests pass reliably in CI environment
- [ ] No static mocking of LWJGL used

## Implementation Notes
- Follow project's strategic mocking guidelines
- Test behavior/outcomes, not implementation details
- Consider creating input simulation utilities
- Ensure tests are deterministic and repeatable

## Dependencies
- Working `EngineTestHarness` for GLFW context
- May need input testing utilities
- Mock framework for service interfaces

## Related Files
- `engine/src/main/java/september/engine/core/input/`
- `game/src/test/java/september/game/input/` (reference patterns)
- `TESTING.md` (strategic mocking guidelines)