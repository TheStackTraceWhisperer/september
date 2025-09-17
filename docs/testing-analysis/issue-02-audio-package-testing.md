# Audio Package Testing - High Priority

## Summary
The `september.engine.audio` package has **1% test coverage**, with all three core audio classes essentially untested. This represents a significant gap in audio system verification.

## Affected Classes
- `AudioBuffer` - 0% coverage (144 missed instructions, 16 missed branches)
- `AudioSource` - 0% coverage (222 missed instructions, 32 missed branches)
- `AudioManager` - 4% coverage (227 missed instructions, 25 missed branches)

## Current State
- ✅ Comprehensive AudioSystemIT exists with detailed test scenarios
- ❌ AudioSystemIT fails to execute due to OpenAL context initialization issues
- ❌ No unit tests for AudioBuffer, AudioSource, AudioManager core logic
- ❌ Coverage shows 1% only because integration tests cannot run

## Required Testing Strategy

### Fix Existing Integration Tests (Priority 1)
**AudioSystemIT.java already exists** with comprehensive test coverage:
- Audio component lifecycle management
- Music vs sound effect handling  
- Audio source creation and cleanup
- Volume and pitch controls
- 3D positioning and attenuation
- Background music state management

**Main Issue**: Tests fail due to OpenAL context initialization in CI environment

### Add Missing Unit Tests (Priority 2)

### Test Types Needed

#### Pure Logic Tests (AudioBuffer)
`AudioBuffer` manages audio data and should have harness-free unit tests:
- [ ] Buffer creation and resource management
- [ ] Data loading from byte arrays
- [ ] Resource cleanup (`AutoCloseable` contract)
- [ ] Error handling for invalid data

#### Integration Tests (AudioSource, AudioManager)
These classes require `EngineTestHarness` due to OpenAL dependencies:
- [ ] Audio source creation and configuration
- [ ] Playback state management (play, pause, stop)
- [ ] 3D positioning and attenuation
- [ ] Volume and pitch controls
- [ ] Multiple source management

### Specific Test Scenarios

#### AudioBuffer Tests
```java
class AudioBufferTest {
    @Test void creation_withValidData_succeeds();
    @Test void close_releasesNativeResources();
    @Test void creation_withInvalidData_throwsException();
}
```

#### AudioSource Integration Tests  
```java
class AudioSourceIT extends EngineTestHarness {
    @Test void playback_withValidBuffer_succeeds();
    @Test void positioning_updates3DLocation();
    @Test void volumeControl_affectsPlayback();
    @Test void multipleSourcesPlaySimultaneously();
}
```

#### AudioManager Integration Tests
```java
class AudioManagerIT extends EngineTestHarness {
    @Test void initialization_createsOpenALContext();
    @Test void sourceManagement_createsAndDestroysCorrectly();
    @Test void cleanup_releasesAllResources();
}
```

## Implementation Notes
- Follow existing audio test patterns in `AudioSystemIT.java`
- Mock external dependencies where appropriate (file I/O)
- Test resource management carefully (memory leaks)
- Verify OpenAL state changes where observable

## Acceptance Criteria
- [ ] AudioSystemIT executes successfully in CI environment
- [ ] EngineTestHarness properly initializes OpenAL context for audio tests
- [ ] AudioBuffer has comprehensive unit test suite (new)
- [ ] AudioSource has unit tests for core logic (new)
- [ ] AudioManager has unit tests for resource management (new)
- [ ] Test coverage >70% for audio package (currently 1% due to execution failures)
- [ ] Tests verify proper resource cleanup
- [ ] Tests pass in headless CI environment with proper audio context

## Dependencies
- Requires working `EngineTestHarness` for integration tests
- May need test audio files in `src/test/resources`

## Related Files
- `engine/src/main/java/september/engine/audio/`
- `game/src/test/java/september/engine/systems/AudioSystemIT.java` (reference)
- Audio test resources in `src/test/resources/audio/`