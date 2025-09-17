# Engine Systems Testing - Critical Priority

## Summary
The `september.engine.systems` package has **0% test coverage** across all three core systems. This represents a critical gap in testing core engine functionality.

## Affected Classes
- `AudioSystem` - 0% coverage (641 missed instructions, 74 missed branches)
- `MovementSystem` - 0% coverage (133 missed instructions, 12 missed branches)  
- `RenderSystem` - 0% coverage (82 missed instructions, 2 missed branches)

## Current State
- ❌ No unit tests exist for any system class
- ❌ Integration tests fail due to engine initialization issues
- ❌ System behavior completely unverified

## Required Testing Strategy

### Integration Tests (Primary)
All systems require `EngineTestHarness`-based integration tests as they interact with:
- OpenGL context (RenderSystem)
- OpenAL context (AudioSystem)  
- ECS World (all systems)

### Test Scenarios Needed

#### AudioSystem
- [ ] Audio component lifecycle management
- [ ] Music vs sound effect handling
- [ ] Audio source creation and cleanup
- [ ] Background music state management
- [ ] Multiple sound effect playback

#### MovementSystem  
- [ ] Entity movement with diagonal input
- [ ] Entity movement with straight-line input
- [ ] No movement when no input provided
- [ ] Previous position tracking
- [ ] Movement speed calculations

#### RenderSystem
- [ ] Sprite rendering with transforms
- [ ] Mesh rendering with materials
- [ ] Camera projection and view matrices
- [ ] Multiple entity rendering
- [ ] Resource cleanup on system shutdown

## Implementation Notes
- Follow existing patterns in `MovementSystemIT.java` template
- Use real `IWorld` from harness, mock external services (TimeService, etc.)
- Verify observable outcomes, not method calls
- Test both positive flows and edge cases

## Acceptance Criteria
- [ ] Each system has comprehensive integration test suite
- [ ] Test coverage >80% for all three system classes
- [ ] Tests pass in CI environment with `xvfb-run`
- [ ] Tests follow project's testing philosophy (behavior over implementation)

## Related Files
- `engine/src/main/java/september/engine/systems/`
- `engine/src/test/java/september/engine/systems/MovementSystemIT.java` (existing template)
- `TESTING.md` (project testing guidelines)