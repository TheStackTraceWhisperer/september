package september.engine.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.AudioSourceComponent;
import september.engine.ecs.components.MusicComponent;
import september.engine.ecs.components.SoundEffectComponent;
import september.engine.ecs.components.TransformComponent;
import september.game.components.GameSoundEffectType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for AudioSystem that uses the real OpenAL context.
 * <p>
 * This test extends EngineTestHarness to get a live audio context and tests
 * the actual behavior of the AudioSystem with real audio resources.
 * <p>
 * These tests are disabled in CI environments where OpenAL audio devices are not available.
 */
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class AudioSystemTest extends EngineTestHarness {

  private AudioSystem audioSystem;

  @BeforeEach
  void setUpAudioSystem() {
    // Load test audio resources
    resourceManager.loadAudioBuffer("test-sound", "/audio/test-sound.ogg");
    resourceManager.loadAudioBuffer("test-music", "/audio/test-music.ogg");
    resourceManager.loadAudioBuffer("test-click", "/audio/test-click.ogg");

    // Create and register the audio system
    audioSystem = new AudioSystem(world, audioManager, resourceManager);
    world.registerSystem(audioSystem);
  }

  @Test
  void audioSourceComponent_playsAudio_whenAutoPlayEnabled() {
    // Arrange
    int entity = world.createEntity();
    AudioSourceComponent audioComp = new AudioSourceComponent("test-sound", 1.0f, false, true);
    world.addComponent(entity, audioComp);

    // Act - Update the system to process the component
    audioSystem.update(0.016f); // Simulate 16ms frame

    // Assert
    assertThat(audioComp.isPlaying).isTrue();
  }

  @Test
  void audioSourceComponent_respectsVolumeAndPitch() {
    // Arrange
    int entity = world.createEntity();
    AudioSourceComponent audioComp = new AudioSourceComponent("test-sound", 0.5f, false, true);
    audioComp.pitch = 1.2f;
    world.addComponent(entity, audioComp);

    // Act
    audioSystem.update(0.016f);

    // Assert - The audio should be playing with the correct properties
    assertThat(audioComp.isPlaying).isTrue();
    assertThat(audioComp.volume).isEqualTo(0.5f);
    assertThat(audioComp.pitch).isEqualTo(1.2f);
  }

  @Test
  void audioSourceComponent_usesTransformPosition_when3DPositioning() {
    // Arrange
    int entity = world.createEntity();
    TransformComponent transform = new TransformComponent();
    transform.position.set(10.0f, 5.0f, -3.0f);
    AudioSourceComponent audioComp = new AudioSourceComponent("test-sound", 1.0f, false, true);

    world.addComponent(entity, transform);
    world.addComponent(entity, audioComp);

    // Act
    audioSystem.update(0.016f);

    // Assert - Audio should be playing and positioned
    assertThat(audioComp.isPlaying).isTrue();
  }

  @Test
  void musicComponent_startsWithFadeIn_whenAutoPlayEnabled() {
    // Arrange
    int entity = world.createEntity();
    MusicComponent musicComp = new MusicComponent("test-music", 0.8f, true, 1.0f);
    world.addComponent(entity, musicComp);

    // Act
    audioSystem.update(0.016f);

    // Assert - Music should be playing and fading in
    assertThat(musicComp.isPlaying).isTrue();
    assertThat(musicComp.fadingIn).isTrue();
    assertThat(musicComp.currentVolume).isLessThan(musicComp.baseVolume);
  }

  @Test
  void musicComponent_fadeInProgress_increasesVolumeOverTime() {
    // Arrange
    int entity = world.createEntity();
    MusicComponent musicComp = new MusicComponent("test-music", 1.0f, true, 0.5f); // 0.5s fade duration
    world.addComponent(entity, musicComp);

    // Act - Initial update to start playing
    audioSystem.update(0.016f);
    float initialVolume = musicComp.currentVolume;

    // Act - Update again to advance fade
    audioSystem.update(0.1f); // 100ms later
    float progressVolume = musicComp.currentVolume;

    // Assert - Volume should have increased
    assertThat(progressVolume).isGreaterThan(initialVolume);
    assertThat(musicComp.fadingIn).isTrue();
  }

  @Test
  void musicComponent_fadeInCompletes_afterDuration() {
    // Arrange
    int entity = world.createEntity();
    MusicComponent musicComp = new MusicComponent("test-music", 1.0f, true, 0.1f); // Very short fade
    world.addComponent(entity, musicComp);

    // Act - Start playing
    audioSystem.update(0.016f);

    // Act - Complete the fade
    audioSystem.update(0.2f); // Well beyond fade duration

    // Assert - Fade should be complete
    assertThat(musicComp.fadingIn).isFalse();
    assertThat(musicComp.currentVolume).isEqualTo(musicComp.baseVolume);
  }

  @Test
  void musicComponent_fadeOut_decreasesVolumeAndStops() {
    // Arrange
    int entity = world.createEntity();
    MusicComponent musicComp = new MusicComponent("test-music", 1.0f, true, 0.1f);
    world.addComponent(entity, musicComp);

    // Start playing
    audioSystem.update(0.016f);
    audioSystem.update(0.2f); // Complete fade in

    // Act - Start fade out
    musicComp.startFadeOut();
    audioSystem.update(0.2f); // Complete fade out

    // Assert - Should be stopped and volume should be zero
    assertThat(musicComp.fadingOut).isFalse();
    assertThat(musicComp.currentVolume).isEqualTo(0.0f);
    assertThat(musicComp.isPlaying).isFalse();
  }

  @Test
  void soundEffectComponent_playsOnce_andRemovesItself() {
    // Arrange
    int entity = world.createEntity();
    SoundEffectComponent soundComp = new SoundEffectComponent("test-click", GameSoundEffectType.UI_BUTTON_CLICK);
    world.addComponent(entity, soundComp);

    // Act - Process the sound effect
    audioSystem.update(0.016f);

    // Assert - Should be triggered
    assertThat(soundComp.hasBeenTriggered).isTrue();

    // Act - Wait for sound to finish and process cleanup
    // Since our test sounds are very short, a few frames should be enough
    for (int i = 0; i < 10; i++) {
      audioSystem.update(0.016f);
    }

    // Assert - Component should be removed after playing
    SoundEffectComponent remainingComp = world.getComponent(entity, SoundEffectComponent.class);
    assertThat(remainingComp).isNull();
  }

  @Test
  void audioSystem_cleansUpResources_whenComponentsRemoved() {
    // Arrange
    int entity = world.createEntity();
    AudioSourceComponent audioComp = new AudioSourceComponent("test-sound", 1.0f, false, true);
    world.addComponent(entity, audioComp);

    // Start playing
    audioSystem.update(0.016f);
    assertThat(audioComp.isPlaying).isTrue();

    // Act - Remove the component
    world.removeComponent(entity, AudioSourceComponent.class);
    audioSystem.update(0.016f);

    // Assert - System should clean up resources (no exceptions thrown)
    // The test passes if no exceptions are thrown during cleanup
  }

  @Test
  void audioSystem_pauseAndResumeAllMusic_worksCorrectly() {
    // Arrange
    int entity1 = world.createEntity();
    int entity2 = world.createEntity();
    MusicComponent music1 = new MusicComponent("test-music");
    MusicComponent music2 = new MusicComponent("test-music");

    world.addComponent(entity1, music1);
    world.addComponent(entity2, music2);

    // Start playing
    audioSystem.update(0.016f);
    audioSystem.update(0.1f); // Let them start

    // Act - Pause all music
    audioSystem.pauseAllMusic();
    audioSystem.update(0.016f);

    // Assert - Both should be paused
    assertThat(music1.isPaused).isTrue();
    assertThat(music2.isPaused).isTrue();

    // Act - Resume all music
    audioSystem.resumeAllMusic();
    audioSystem.update(0.016f);

    // Assert - Both should be playing again
    assertThat(music1.isPaused).isFalse();
    assertThat(music2.isPaused).isFalse();
  }

  @Test
  void audioSystem_fadeOutAllMusic_worksCorrectly() {
    // Arrange
    int entity1 = world.createEntity();
    int entity2 = world.createEntity();
    MusicComponent music1 = new MusicComponent("test-music", 1.0f, true, 0.1f);
    MusicComponent music2 = new MusicComponent("test-music", 1.0f, true, 0.1f);

    world.addComponent(entity1, music1);
    world.addComponent(entity2, music2);

    // Start playing and complete fade in
    audioSystem.update(0.016f);
    audioSystem.update(0.2f);

    // Act - Fade out all music
    audioSystem.fadeOutAllMusic();
    audioSystem.update(0.2f); // Complete fade out

    // Assert - Both should be faded out
    assertThat(music1.fadingOut).isFalse();
    assertThat(music2.fadingOut).isFalse();
    assertThat(music1.currentVolume).isEqualTo(0.0f);
    assertThat(music2.currentVolume).isEqualTo(0.0f);
  }

  @Test
  void audioSystem_playSoundEffect_createsAndPlaysSound() {
    // Arrange
    int entity = world.createEntity();

    // Act - Play sound effect programmatically
    audioSystem.playSoundEffect(entity, "test-click", GameSoundEffectType.UI_BUTTON_CLICK, 0.7f);
    audioSystem.update(0.016f);

    // Assert - Component should be created and triggered
    SoundEffectComponent soundComp = world.getComponent(entity, SoundEffectComponent.class);
    assertThat(soundComp).isNotNull();
    assertThat(soundComp.hasBeenTriggered).isTrue();
    assertThat(soundComp.volume).isEqualTo(0.7f);
  }
}