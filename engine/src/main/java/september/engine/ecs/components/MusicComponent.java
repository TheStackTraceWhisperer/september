package september.engine.ecs.components;

import september.engine.ecs.Component;

/**
 * A component for background music with advanced playback control.
 * <p>
 * This component provides music-specific features like fading in/out,
 * crossfading between tracks, and global music state management.
 * Unlike AudioSourceComponent, music is typically not positioned in 3D space
 * and plays at full volume regardless of listener position.
 */
public class MusicComponent implements Component {

  /**
   * The handle to the audio buffer containing the music track.
   */
  public String musicBufferHandle;

  /**
   * The base volume level for this music track (0.0f = silent, 1.0f = full volume).
   * This will be modified by fade effects.
   */
  public float baseVolume = 1.0f;

  /**
   * The current effective volume, taking fades into account.
   * This is managed by the AudioSystem and should not be modified directly.
   */
  public transient float currentVolume = 1.0f;

  /**
   * Whether this music track should loop when it reaches the end.
   */
  public boolean looping = true;

  /**
   * Whether this music should start playing immediately when the component is added.
   */
  public boolean autoPlay = true;

  /**
   * Whether this music track is currently fading in.
   */
  public transient boolean fadingIn = false;

  /**
   * Whether this music track is currently fading out.
   */
  public transient boolean fadingOut = false;

  /**
   * The duration of fade effects in seconds.
   */
  public float fadeDuration = 2.0f;

  /**
   * The time elapsed during the current fade operation.
   * This is managed by the AudioSystem.
   */
  public transient float fadeTimer = 0.0f;

  /**
   * Internal state to track if this music is currently playing.
   * This is managed by the AudioSystem and should not be modified directly.
   */
  public transient boolean isPlaying = false;

  /**
   * Internal state to track if this music is currently paused.
   * This is managed by the AudioSystem and should not be modified directly.
   */
  public transient boolean isPaused = false;

  public MusicComponent(String musicBufferHandle) {
    this.musicBufferHandle = musicBufferHandle;
  }

  public MusicComponent(String musicBufferHandle, float baseVolume) {
    this.musicBufferHandle = musicBufferHandle;
    this.baseVolume = baseVolume;
    this.currentVolume = baseVolume;
  }

  public MusicComponent(String musicBufferHandle, float baseVolume, boolean looping) {
    this.musicBufferHandle = musicBufferHandle;
    this.baseVolume = baseVolume;
    this.currentVolume = baseVolume;
    this.looping = looping;
  }

  public MusicComponent(String musicBufferHandle, float baseVolume, boolean looping, float fadeDuration) {
    this.musicBufferHandle = musicBufferHandle;
    this.baseVolume = baseVolume;
    this.currentVolume = baseVolume;
    this.looping = looping;
    this.fadeDuration = fadeDuration;
  }

  /**
   * Starts a fade-in effect for this music track.
   * The AudioSystem will gradually increase the volume from 0 to baseVolume.
   */
  public void startFadeIn() {
    this.fadingIn = true;
    this.fadingOut = false;
    this.fadeTimer = 0.0f;
    this.currentVolume = 0.0f;
  }

  /**
   * Starts a fade-out effect for this music track.
   * The AudioSystem will gradually decrease the volume from currentVolume to 0.
   */
  public void startFadeOut() {
    this.fadingOut = true;
    this.fadingIn = false;
    this.fadeTimer = 0.0f;
  }

  /**
   * Stops any active fade effects and sets the volume to the base volume.
   */
  public void stopFade() {
    this.fadingIn = false;
    this.fadingOut = false;
    this.fadeTimer = 0.0f;
    this.currentVolume = this.baseVolume;
  }
}