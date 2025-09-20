package september.engine.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.AudioSourceComponent;
import september.engine.ecs.components.MusicComponent;
import september.engine.ecs.components.SoundEffectComponent;
import september.engine.ecs.components.TransformComponent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Integration test for the AudioSystem that runs against a live, engine-managed World
 * with real audio functionality.
 * <p>
 * Note: These tests focus on system behavior and component management rather than
 * actual audio playback, which allows them to work in CI environments.
 */
class AudioSystemIT extends EngineTestHarness {

  private AudioSystem audioSystem;

  @BeforeEach
  void setupSystem() {
    // The harness provides a live world, audio manager, and resource manager
    audioSystem = new AudioSystem(world, audioManager, resourceManager);
  }

  @Test
  @DisplayName("AudioSystem should handle empty world without throwing exceptions")
  void update_emptyWorld_doesNotThrowException() {
    // ARRANGE - Empty world

    // ACT & ASSERT
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle empty world gracefully")
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AudioSystem should handle multiple update calls without errors")
  void update_multipleUpdates_doesNotThrowException() {
    // ARRANGE - Empty world

    // ACT & ASSERT
    assertThatCode(() -> {
      for (int i = 0; i < 10; i++) {
        audioSystem.update(0.016f);
      }
    }).as("AudioSystem should handle multiple updates gracefully")
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AudioSystem should manage AudioSourceComponent properties correctly")
  void audioSourceComponent_properties_managedCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    TransformComponent transform = new TransformComponent();
    transform.position.set(5.0f, 10.0f, 0.0f);

    AudioSourceComponent audioComp = new AudioSourceComponent("nonexistent_audio", 0.8f, false, false);

    world.addComponent(entityId, transform);
    world.addComponent(entityId, audioComp);

    // ACT & ASSERT - Should not throw even with invalid audio handle
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle invalid audio handles gracefully")
      .doesNotThrowAnyException();

    // Verify component properties are maintained
    assertThat(audioComp.volume).as("Volume should be preserved").isEqualTo(0.8f);
    assertThat(audioComp.looping).as("Looping flag should be preserved").isFalse();
    assertThat(audioComp.autoPlay).as("AutoPlay flag should be preserved").isFalse();
  }

  @Test
  @DisplayName("AudioSystem should handle multiple AudioSourceComponents independently")
  void multipleAudioSources_handledIndependently() {
    // ARRANGE
    int entity1 = world.createEntity();
    int entity2 = world.createEntity();

    AudioSourceComponent audio1 = new AudioSourceComponent("audio1", 1.0f, true, false);
    AudioSourceComponent audio2 = new AudioSourceComponent("audio2", 0.5f, false, false);

    world.addComponent(entity1, new TransformComponent());
    world.addComponent(entity1, audio1);
    world.addComponent(entity2, new TransformComponent());
    world.addComponent(entity2, audio2);

    // ACT & ASSERT
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle multiple audio sources")
      .doesNotThrowAnyException();

    // Verify properties are maintained independently
    assertThat(audio1.volume).as("First audio volume").isEqualTo(1.0f);
    assertThat(audio2.volume).as("Second audio volume").isEqualTo(0.5f);
    assertThat(audio1.looping).as("First audio looping").isTrue();
    assertThat(audio2.looping).as("Second audio looping").isFalse();
  }

  @Test
  @DisplayName("AudioSystem should handle MusicComponent fade state management")
  void musicComponent_fadeState_managedCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    MusicComponent musicComp = new MusicComponent("music_track", 1.0f, true, 1.0f);
    musicComp.autoPlay = false; // Avoid audio loading
    world.addComponent(entityId, musicComp);

    // ACT - Initial state
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle music components")
      .doesNotThrowAnyException();

    // ASSERT - Initial state
    assertThat(musicComp.baseVolume).as("Base volume should be preserved").isEqualTo(1.0f);
    assertThat(musicComp.fadeDuration).as("Fade duration should be preserved").isEqualTo(1.0f);
    assertThat(musicComp.looping).as("Looping should be preserved").isTrue();

    // ACT - Test fade state changes
    musicComp.startFadeIn();
    assertThat(musicComp.fadingIn).as("Should be fading in after startFadeIn").isTrue();
    assertThat(musicComp.fadingOut).as("Should not be fading out").isFalse();

    musicComp.startFadeOut();
    assertThat(musicComp.fadingOut).as("Should be fading out after startFadeOut").isTrue();
    assertThat(musicComp.fadingIn).as("Should not be fading in").isFalse();

    musicComp.stopFade();
    assertThat(musicComp.fadingIn).as("Should not be fading in after stopFade").isFalse();
    assertThat(musicComp.fadingOut).as("Should not be fading out after stopFade").isFalse();
  }

  @Test
  @DisplayName("AudioSystem should handle SoundEffectComponent properties")
  void soundEffectComponent_properties_managedCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    TestSoundEffectType soundType = new TestSoundEffectType();
    SoundEffectComponent soundComp = new SoundEffectComponent("sound_effect", soundType, 0.7f);
    soundComp.autoPlay = false; // Avoid audio loading
    world.addComponent(entityId, soundComp);

    // ACT & ASSERT
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle sound effect components")
      .doesNotThrowAnyException();

    // Verify properties
    assertThat(soundComp.volume).as("Volume should be preserved").isEqualTo(0.7f);
    assertThat(soundComp.soundType).as("Sound type should be preserved").isEqualTo(soundType);
    assertThat(soundComp.removeAfterPlay).as("Remove after play should be true by default").isTrue();
  }

  @Test
  @DisplayName("AudioSystem should handle entity cleanup when components are removed")
  void audioSourceCleanup_whenComponentsRemoved_worksCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    AudioSourceComponent audioComp = new AudioSourceComponent("audio", 1.0f, false, false);
    world.addComponent(entityId, new TransformComponent());
    world.addComponent(entityId, audioComp);

    // ACT - Initial update
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("Initial update should work")
      .doesNotThrowAnyException();

    // Remove the component
    world.removeComponent(entityId, AudioSourceComponent.class);

    // ACT & ASSERT - Cleanup should work gracefully
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle component removal gracefully")
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AudioSystem should handle entity destruction gracefully")
  void audioSourceCleanup_whenEntitiesDestroyed_worksCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    AudioSourceComponent audioComp = new AudioSourceComponent("audio", 1.0f, false, false);
    world.addComponent(entityId, new TransformComponent());
    world.addComponent(entityId, audioComp);

    // ACT - Initial update
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("Initial update should work")
      .doesNotThrowAnyException();

    // Destroy the entity
    world.destroyEntity(entityId);

    // ACT & ASSERT - Cleanup should work gracefully
    assertThatCode(() -> audioSystem.update(0.016f))
      .as("AudioSystem should handle entity destruction gracefully")
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AudioSystem should handle playSoundEffect method without errors")
  void playSoundEffect_method_worksCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    TestSoundEffectType soundType = new TestSoundEffectType();

    // ACT & ASSERT - Should not throw even with invalid audio handle
    assertThatCode(() -> {
      audioSystem.playSoundEffect(entityId, "test_sound", soundType, 0.9f);
      // Modify the component to avoid audio loading
      SoundEffectComponent soundComp = world.getComponent(entityId, SoundEffectComponent.class);
      if (soundComp != null) {
        soundComp.autoPlay = false;
      }
      audioSystem.update(0.016f);
    }).as("playSoundEffect should work without throwing")
      .doesNotThrowAnyException();

    // Verify component was added
    SoundEffectComponent soundComp = world.getComponent(entityId, SoundEffectComponent.class);
    assertThat(soundComp).as("SoundEffectComponent should be added to entity").isNotNull();
    assertThat(soundComp.volume).as("Volume should be set correctly").isEqualTo(0.9f);
    assertThat(soundComp.soundType).as("Sound type should be set correctly").isEqualTo(soundType);
  }

  @Test
  @DisplayName("AudioSystem should handle fadeOutAllMusic method")
  void fadeOutAllMusic_method_worksCorrectly() {
    // ARRANGE - Create multiple music entities
    int music1 = world.createEntity();
    int music2 = world.createEntity();
    MusicComponent musicComp1 = new MusicComponent("music1", 1.0f);
    MusicComponent musicComp2 = new MusicComponent("music2", 0.8f);

    // Set autoPlay to false to avoid audio loading
    musicComp1.autoPlay = false;
    musicComp2.autoPlay = false;

    world.addComponent(music1, musicComp1);
    world.addComponent(music2, musicComp2);

    // ACT & ASSERT
    assertThatCode(() -> {
      audioSystem.update(0.016f);
      audioSystem.fadeOutAllMusic();
    }).as("fadeOutAllMusic should work without throwing")
      .doesNotThrowAnyException();

    // Verify fade state was set
    assertThat(musicComp1.fadingOut).as("First music should be fading out").isTrue();
    assertThat(musicComp2.fadingOut).as("Second music should be fading out").isTrue();
  }

  @Test
  @DisplayName("AudioSystem should handle pause and resume methods")
  void pauseAndResumeAllMusic_methods_workCorrectly() {
    // ARRANGE
    int entityId = world.createEntity();
    MusicComponent musicComp = new MusicComponent("music", 1.0f);
    musicComp.autoPlay = false; // Avoid audio loading
    world.addComponent(entityId, musicComp);

    // ACT & ASSERT - Should not throw
    assertThatCode(() -> {
      audioSystem.update(0.016f);
      audioSystem.pauseAllMusic();
      audioSystem.resumeAllMusic();
    }).as("Pause and resume methods should work without throwing")
      .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AudioSystem should handle stopAll method")
  void stopAll_method_worksCorrectly() {
    // ARRANGE - Create various audio entities
    int audioEntity = world.createEntity();
    int musicEntity = world.createEntity();
    int soundEntity = world.createEntity();

    AudioSourceComponent audioComp = new AudioSourceComponent("audio", 1.0f, false, false);
    MusicComponent musicComp = new MusicComponent("music", 1.0f);
    musicComp.autoPlay = false; // Avoid audio loading
    TestSoundEffectType soundType = new TestSoundEffectType();
    SoundEffectComponent soundComp = new SoundEffectComponent("sound", soundType);
    soundComp.autoPlay = false; // Avoid audio loading

    world.addComponent(audioEntity, new TransformComponent());
    world.addComponent(audioEntity, audioComp);
    world.addComponent(musicEntity, musicComp);
    world.addComponent(soundEntity, soundComp);

    // ACT & ASSERT
    assertThatCode(() -> {
      audioSystem.update(0.016f);
      audioSystem.stopAll();
      audioSystem.update(0.016f);
    }).as("stopAll should work without throwing")
      .doesNotThrowAnyException();
  }

  // Test implementation of SoundEffectType interface
  private static class TestSoundEffectType implements SoundEffectComponent.SoundEffectType {
  }
}
