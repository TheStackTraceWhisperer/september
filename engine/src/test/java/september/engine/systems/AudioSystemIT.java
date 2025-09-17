package september.engine.systems;

import org.joml.Vector3f;
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
 */
class AudioSystemIT extends EngineTestHarness {

    private AudioSystem audioSystem;

    @BeforeEach
    void setupSystem() {
        // The harness provides a live world, audio manager, and resource manager
        // Load test audio resources
        resourceManager.loadAudioBuffer("test_audio", "audio/test-sound.ogg");
        audioSystem = new AudioSystem(world, audioManager, resourceManager);
    }

    @Test
    @DisplayName("AudioSystem should handle AudioSourceComponent lifecycle")
    void audioSourceComponent_lifecycle_managedCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        TransformComponent transform = new TransformComponent();
        transform.position.set(5.0f, 10.0f, 0.0f);
        
        AudioSourceComponent audioComp = new AudioSourceComponent("test_audio", 0.8f, false, true);
        
        world.addComponent(entityId, transform);
        world.addComponent(entityId, audioComp);

        // ACT
        audioSystem.update(0.016f);

        // ASSERT
        assertThat(audioComp.isPlaying).as("Audio should be playing after first update").isTrue();
        
        // Verify component properties are maintained
        assertThat(audioComp.volume).isEqualTo(0.8f);
        assertThat(audioComp.looping).isFalse();
        assertThat(audioComp.autoPlay).isTrue();
    }

    @Test
    @DisplayName("AudioSystem should handle multiple AudioSourceComponents independently")
    void multipleAudioSources_handledIndependently() {
        // ARRANGE
        int entity1 = world.createEntity();
        int entity2 = world.createEntity();
        
        AudioSourceComponent audio1 = new AudioSourceComponent("test_audio", 1.0f, true, true);
        AudioSourceComponent audio2 = new AudioSourceComponent("test_audio", 0.5f, false, false);
        
        world.addComponent(entity1, new TransformComponent());
        world.addComponent(entity1, audio1);
        world.addComponent(entity2, new TransformComponent());
        world.addComponent(entity2, audio2);

        // ACT
        audioSystem.update(0.016f);

        // ASSERT
        assertThat(audio1.isPlaying).as("First audio source should be playing (autoPlay=true)").isTrue();
        assertThat(audio2.isPlaying).as("Second audio source should not be playing (autoPlay=false)").isFalse();
        assertThat(audio1.volume).isEqualTo(1.0f);
        assertThat(audio2.volume).isEqualTo(0.5f);
    }

    @Test
    @DisplayName("AudioSystem should handle MusicComponent with fade effects")
    void musicComponent_fadeEffects_workCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        MusicComponent musicComp = new MusicComponent("test_audio", 1.0f, true, 1.0f);
        world.addComponent(entityId, musicComp);

        // ACT - First update should start the music and fade-in
        audioSystem.update(0.016f);

        // ASSERT
        assertThat(musicComp.isPlaying).as("Music should be playing").isTrue();
        assertThat(musicComp.fadingIn).as("Music should be fading in").isTrue();
        assertThat(musicComp.currentVolume).as("Current volume should be less than base volume during fade-in")
                .isLessThan(musicComp.baseVolume);

        // ACT - Update for half the fade duration
        float halfFadeDuration = musicComp.fadeDuration / 2.0f;
        audioSystem.update(halfFadeDuration);

        // ASSERT
        assertThat(musicComp.fadingIn).as("Music should still be fading in").isTrue();
        assertThat(musicComp.currentVolume).as("Current volume should be about half base volume")
                .isCloseTo(musicComp.baseVolume * 0.5f, org.assertj.core.data.Offset.offset(0.1f));

        // ACT - Complete the fade-in
        audioSystem.update(halfFadeDuration + 0.1f);

        // ASSERT
        assertThat(musicComp.fadingIn).as("Music should have finished fading in").isFalse();
        assertThat(musicComp.currentVolume).as("Current volume should equal base volume")
                .isEqualTo(musicComp.baseVolume);
    }

    @Test
    @DisplayName("AudioSystem should handle MusicComponent fade-out")
    void musicComponent_fadeOut_worksCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        MusicComponent musicComp = new MusicComponent("test_audio", 1.0f, true, 0.5f);
        world.addComponent(entityId, musicComp);

        // Start playing and complete fade-in
        audioSystem.update(0.016f);
        audioSystem.update(0.6f); // Complete fade-in
        
        float initialVolume = musicComp.currentVolume;
        
        // ACT - Start fade-out
        musicComp.startFadeOut();
        audioSystem.update(0.25f); // Half of fade duration

        // ASSERT
        assertThat(musicComp.fadingOut).as("Music should be fading out").isTrue();
        assertThat(musicComp.currentVolume).as("Current volume should be less than initial")
                .isLessThan(initialVolume);

        // ACT - Complete fade-out
        audioSystem.update(0.3f);

        // ASSERT
        assertThat(musicComp.fadingOut).as("Music should have finished fading out").isFalse();
        assertThat(musicComp.currentVolume).as("Current volume should be zero").isEqualTo(0.0f);
        assertThat(musicComp.isPlaying).as("Music should be stopped").isFalse();
    }

    @Test
    @DisplayName("AudioSystem should handle SoundEffectComponent one-shot playback")
    void soundEffectComponent_oneShot_worksCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        TestSoundEffectType soundType = new TestSoundEffectType();
        SoundEffectComponent soundComp = new SoundEffectComponent("test_audio", soundType, 0.7f);
        world.addComponent(entityId, soundComp);

        // ACT
        audioSystem.update(0.016f);

        // ASSERT
        assertThat(soundComp.hasBeenTriggered).as("Sound effect should be triggered").isTrue();
        assertThat(soundComp.volume).isEqualTo(0.7f);
        assertThat(soundComp.removeAfterPlay).as("Should be set to remove after play").isTrue();
    }

    @Test
    @DisplayName("AudioSystem should cleanup audio sources when entities are removed")
    void audioSourceCleanup_whenEntitiesRemoved_worksCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        AudioSourceComponent audioComp = new AudioSourceComponent("test_audio", 1.0f, false, true);
        world.addComponent(entityId, new TransformComponent());
        world.addComponent(entityId, audioComp);

        // ACT - Start audio
        audioSystem.update(0.016f);
        assertThat(audioComp.isPlaying).isTrue();

        // Remove the entity
        world.destroyEntity(entityId);
        audioSystem.update(0.016f);

        // ASSERT - No exception should be thrown during cleanup
        assertThatCode(() -> audioSystem.update(0.016f))
                .as("AudioSystem should handle entity removal gracefully")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("AudioSystem should handle empty scene without errors")
    void update_emptyScene_doesNotThrowException() {
        // ARRANGE - Empty world

        // ACT & ASSERT
        assertThatCode(() -> audioSystem.update(0.016f))
                .as("AudioSystem update on empty world should not throw")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("AudioSystem should handle playSoundEffect method")
    void playSoundEffect_method_worksCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        TestSoundEffectType soundType = new TestSoundEffectType();

        // ACT
        audioSystem.playSoundEffect(entityId, "test_audio", soundType, 0.9f);
        audioSystem.update(0.016f);

        // ASSERT
        SoundEffectComponent soundComp = world.getComponent(entityId, SoundEffectComponent.class);
        assertThat(soundComp).as("SoundEffectComponent should be added to entity").isNotNull();
        assertThat(soundComp.volume).isEqualTo(0.9f);
        assertThat(soundComp.soundType).isEqualTo(soundType);
        assertThat(soundComp.hasBeenTriggered).as("Sound should be triggered").isTrue();
    }

    @Test
    @DisplayName("AudioSystem should handle fadeOutAllMusic method")
    void fadeOutAllMusic_method_worksCorrectly() {
        // ARRANGE - Create multiple music entities
        int music1 = world.createEntity();
        int music2 = world.createEntity();
        MusicComponent musicComp1 = new MusicComponent("test_audio", 1.0f);
        MusicComponent musicComp2 = new MusicComponent("test_audio", 0.8f);
        
        world.addComponent(music1, musicComp1);
        world.addComponent(music2, musicComp2);

        // Start playing
        audioSystem.update(0.016f);
        audioSystem.update(3.0f); // Complete fade-in

        // ACT
        audioSystem.fadeOutAllMusic();

        // ASSERT
        assertThat(musicComp1.fadingOut).as("First music should be fading out").isTrue();
        assertThat(musicComp2.fadingOut).as("Second music should be fading out").isTrue();
    }

    @Test
    @DisplayName("AudioSystem should handle pauseAllMusic and resumeAllMusic methods")
    void pauseAndResumeAllMusic_methods_workCorrectly() {
        // ARRANGE
        int entityId = world.createEntity();
        MusicComponent musicComp = new MusicComponent("test_audio", 1.0f);
        world.addComponent(entityId, musicComp);

        // Start playing
        audioSystem.update(0.016f);
        audioSystem.update(3.0f); // Complete fade-in
        assertThat(musicComp.isPlaying).isTrue();

        // ACT - Pause
        audioSystem.pauseAllMusic();
        audioSystem.update(0.016f);

        // ASSERT
        assertThat(musicComp.isPaused).as("Music should be paused").isTrue();

        // ACT - Resume
        audioSystem.resumeAllMusic();
        audioSystem.update(0.016f);

        // ASSERT
        assertThat(musicComp.isPaused).as("Music should no longer be paused").isFalse();
    }

    @Test
    @DisplayName("AudioSystem should handle stopAll method")
    void stopAll_method_worksCorrectly() {
        // ARRANGE - Create various audio entities
        int audioEntity = world.createEntity();
        int musicEntity = world.createEntity();
        int soundEntity = world.createEntity();

        AudioSourceComponent audioComp = new AudioSourceComponent("test_audio", 1.0f, false, true);
        MusicComponent musicComp = new MusicComponent("test_audio", 1.0f);
        TestSoundEffectType soundType = new TestSoundEffectType();
        SoundEffectComponent soundComp = new SoundEffectComponent("test_audio", soundType);

        world.addComponent(audioEntity, new TransformComponent());
        world.addComponent(audioEntity, audioComp);
        world.addComponent(musicEntity, musicComp);
        world.addComponent(soundEntity, soundComp);

        // Start all audio
        audioSystem.update(0.016f);

        // ACT
        audioSystem.stopAll();

        // ASSERT - Should not throw exception
        assertThatCode(() -> audioSystem.update(0.016f))
                .as("AudioSystem should handle stopAll gracefully")
                .doesNotThrowAnyException();
    }

    // Test implementation of SoundEffectType interface
    private static class TestSoundEffectType implements SoundEffectComponent.SoundEffectType {
    }
}