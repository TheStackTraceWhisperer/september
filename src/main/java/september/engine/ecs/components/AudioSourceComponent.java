package september.engine.ecs.components;

import september.engine.ecs.Component;

/**
 * A component that allows an entity to play audio through a positioned 3D audio source.
 * <p>
 * This component stores the handle to an audio buffer that should be played,
 * along with properties that control how the audio is played (volume, pitch, looping).
 * The actual AudioSource is managed by the AudioSystem and tied to the entity's
 * TransformComponent for 3D positioning.
 */
public class AudioSourceComponent implements Component {

  /**
   * The handle to the audio buffer that should be played.
   * This will be resolved by the AudioSystem using the ResourceManager.
   */
  public String audioBufferHandle;

  /**
   * The volume level for this audio source (0.0f = silent, 1.0f = full volume).
   */
  public float volume = 1.0f;

  /**
   * The pitch multiplier for this audio source (1.0f = normal pitch).
   */
  public float pitch = 1.0f;

  /**
   * Whether this audio source should loop when it reaches the end.
   */
  public boolean looping = false;

  /**
   * Whether this audio source should start playing immediately when the component is added.
   */
  public boolean autoPlay = false;

  /**
   * Internal state to track if this audio source is currently playing.
   * This is managed by the AudioSystem and should not be modified directly.
   */
  public transient boolean isPlaying = false;

  public AudioSourceComponent(String audioBufferHandle) {
    this.audioBufferHandle = audioBufferHandle;
  }

  public AudioSourceComponent(String audioBufferHandle, float volume) {
    this.audioBufferHandle = audioBufferHandle;
    this.volume = volume;
  }

  public AudioSourceComponent(String audioBufferHandle, float volume, boolean looping) {
    this.audioBufferHandle = audioBufferHandle;
    this.volume = volume;
    this.looping = looping;
  }

  public AudioSourceComponent(String audioBufferHandle, float volume, boolean looping, boolean autoPlay) {
    this.audioBufferHandle = audioBufferHandle;
    this.volume = volume;
    this.looping = looping;
    this.autoPlay = autoPlay;
  }
}