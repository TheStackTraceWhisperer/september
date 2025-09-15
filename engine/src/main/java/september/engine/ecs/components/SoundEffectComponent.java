package september.engine.ecs.components;

import september.engine.ecs.Component;

/**
 * A component for one-shot sound effects, typically used for UI interactions and 2D gameplay sounds.
 * <p>
 * Unlike AudioSourceComponent and MusicComponent, SoundEffectComponent is designed for
 * fire-and-forget audio playback. Sound effects are typically short, don't loop,
 * and don't require continuous management after playback starts.
 */
public class SoundEffectComponent implements Component {

  /**
   * Interface for different types of sound effects.
   * This allows the game to define its own sound effect categories
   * while keeping the engine generic.
   */
  public interface SoundEffectType {
  }

  /**
   * The handle to the audio buffer containing the sound effect.
   */
  public String soundBufferHandle;

  /**
   * The type/category of this sound effect.
   * This allows the AudioSystem to apply different volume levels
   * or other processing based on the sound type.
   */
  public SoundEffectType soundType;

  /**
   * The volume level for this sound effect (0.0f = silent, 1.0f = full volume).
   */
  public float volume = 1.0f;

  /**
   * The pitch multiplier for this sound effect (1.0f = normal pitch).
   * This can be used to add variation to repetitive sounds.
   */
  public float pitch = 1.0f;

  /**
   * Whether this sound effect should be played immediately when the component is added.
   * For sound effects, this is typically true.
   */
  public boolean autoPlay = true;

  /**
   * Whether this sound effect should be removed from the entity after it finishes playing.
   * This is typically true for one-shot sound effects to prevent component buildup.
   */
  public boolean removeAfterPlay = true;

  /**
   * Internal state to track if this sound effect has been triggered.
   * This is managed by the AudioSystem and should not be modified directly.
   */
  public transient boolean hasBeenTriggered = false;

  public SoundEffectComponent(String soundBufferHandle, SoundEffectType soundType) {
    this.soundBufferHandle = soundBufferHandle;
    this.soundType = soundType;
  }

  public SoundEffectComponent(String soundBufferHandle, SoundEffectType soundType, float volume) {
    this.soundBufferHandle = soundBufferHandle;
    this.soundType = soundType;
    this.volume = volume;
  }

  public SoundEffectComponent(String soundBufferHandle, SoundEffectType soundType, float volume, float pitch) {
    this.soundBufferHandle = soundBufferHandle;
    this.soundType = soundType;
    this.volume = volume;
    this.pitch = pitch;
  }
}