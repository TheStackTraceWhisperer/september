# Engine Systems Testing - Critical Priority

## Summary
The `september.engine.systems` package has **0% test coverage** across all three core systems. This represents a critical gap in testing core engine functionality.

## Affected Classes
- `AudioSystem` - 0% coverage (641 missed instructions, 74 missed branches)
- `MovementSystem` - 0% coverage (133 missed instructions, 12 missed branches)  
- `RenderSystem` - 0% coverage (82 missed instructions, 2 missed branches)

## Current State
- ✅ Integration tests exist for all three systems (MovementSystemIT, RenderSystemIT, AudioSystemIT)
- ❌ Integration tests fail due to OpenGL/OpenAL context initialization issues in CI
- ❌ Tests show 0% coverage because they cannot execute, not because they don't exist
- ✅ Test code is comprehensive and well-written

## Required Testing Strategy

### Fix Test Execution Environment (Primary)
The main issue is that comprehensive integration tests exist but fail to execute:
- Fix OpenGL context initialization in CI environment
- Resolve OpenAL context setup issues
- Ensure `EngineTestHarness` works properly in headless mode

### Existing Test Coverage
**Tests that exist but fail to run:**
- `MovementSystemIT.java` - Tests entity movement with diagonal and straight-line input
- `RenderSystemIT.java` - Tests sprite rendering without errors
- `AudioSystemIT.java` - Comprehensive audio component testing

### Test Scenarios Needed

#### Fix Existing Tests (Priority 1)
- [ ] Resolve EngineTestHarness OpenGL context initialization
- [ ] Fix CI environment compatibility for graphics tests
- [ ] Ensure AudioSystemIT can run in headless mode
- [ ] Debug and fix MovementSystemIT execution
- [ ] Debug and fix RenderSystemIT execution

#### Additional Test Coverage (Priority 2)  
- [ ] Unit tests for system logic that doesn't require graphics context
- [ ] Edge case testing for each system
- [ ] Performance testing for system update loops

## Implementation Notes
- Follow existing patterns in `MovementSystemIT.java` template
- Use real `IWorld` from harness, mock external services (TimeService, etc.)
- Verify observable outcomes, not method calls
- Test both positive flows and edge cases

## Acceptance Criteria
- [ ] All existing integration tests execute successfully in CI environment
- [ ] EngineTestHarness properly initializes OpenGL/OpenAL contexts
- [ ] Test coverage >80% for all three system classes (currently 0% due to execution failures)
- [ ] Tests pass reliably with `xvfb-run -a mvn verify`
- [ ] Any additional unit tests follow project's testing philosophy
- [ ] Fix root cause of "Engine initialization failed" errors

## Related Files
- `engine/src/main/java/september/engine/systems/`
- `engine/src/test/java/september/engine/systems/MovementSystemIT.java` (existing template)
- `TESTING.md` (project testing guidelines)