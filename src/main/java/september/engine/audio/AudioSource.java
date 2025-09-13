package september.engine.audio;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * Represents an OpenAL audio source that can play AudioBuffers.
 * This is analogous to how a sprite renderer can display textures.
 * <p>
 * An AudioSource can be positioned in 3D space and configured with various playback
 * properties like volume, pitch, and looping. Multiple sources can play the same
 * buffer simultaneously.
 */
public final class AudioSource implements AutoCloseable {

  private final int sourceId;
  private boolean closed = false;
  private final boolean ciMode;
  
  // Fields for storing values in CI mode
  private float volume = 1.0f;
  private float pitch = 1.0f;
  private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
  private boolean looping = false;

  public AudioSource() {
    // Check if running in CI mode
    this.ciMode = "true".equalsIgnoreCase(System.getenv("CI"));
    
    if (ciMode) {
      // In CI mode, use a dummy source ID
      this.sourceId = -1;
    } else {
      this.sourceId = alGenSources();
      
      if (alGetError() != AL_NO_ERROR) {
        throw new RuntimeException("Failed to create OpenAL audio source");
      }
      
      // Set sensible defaults
      setVolume(1.0f);
      setPitch(1.0f);
      setPosition(0.0f, 0.0f, 0.0f);
      setLooping(false);
    }
  }

  /**
   * Binds an audio buffer to this source and starts playback.
   *
   * @param buffer The audio buffer to play
   */
  public void play(AudioBuffer buffer) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourcei(sourceId, AL_BUFFER, buffer.getBufferId());
    alSourcePlay(sourceId);
  }

  /**
   * Pauses audio playback. Can be resumed with resume().
   */
  public void pause() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourcePause(sourceId);
  }

  /**
   * Resumes paused audio playback.
   */
  public void resume() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourcePlay(sourceId);
  }

  /**
   * Stops audio playback and rewinds to the beginning.
   */
  public void stop() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourceStop(sourceId);
  }

  /**
   * Sets the volume/gain of this audio source.
   *
   * @param volume Volume level (0.0f = silent, 1.0f = full volume)
   */
  public void setVolume(float volume) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    this.volume = Math.max(0.0f, volume);
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourcef(sourceId, AL_GAIN, this.volume);
  }

  /**
   * Gets the current volume of this audio source.
   *
   * @return Current volume level
   */
  public float getVolume() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Return stored value in CI mode
    if (ciMode) {
      return volume;
    }
    
    return alGetSourcef(sourceId, AL_GAIN);
  }

  /**
   * Sets the pitch of this audio source, affecting playback speed and tone.
   *
   * @param pitch Pitch multiplier (1.0f = normal, 2.0f = double speed/higher pitch)
   */
  public void setPitch(float pitch) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    this.pitch = Math.max(0.1f, pitch);
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourcef(sourceId, AL_PITCH, this.pitch);
  }

  /**
   * Gets the current pitch of this audio source.
   *
   * @return Current pitch multiplier
   */
  public float getPitch() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Return stored value in CI mode
    if (ciMode) {
      return pitch;
    }
    
    return alGetSourcef(sourceId, AL_PITCH);
  }

  /**
   * Sets the 3D position of this audio source.
   *
   * @param x X coordinate
   * @param y Y coordinate  
   * @param z Z coordinate
   */
  public void setPosition(float x, float y, float z) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    this.position.set(x, y, z);
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSource3f(sourceId, AL_POSITION, x, y, z);
  }

  /**
   * Sets the 3D position of this audio source.
   *
   * @param position Position vector
   */
  public void setPosition(Vector3f position) {
    setPosition(position.x, position.y, position.z);
  }

  /**
   * Gets the current position of this audio source.
   *
   * @return Current position vector (copy)
   */
  public Vector3f getPosition() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Return stored value in CI mode
    if (ciMode) {
      return new Vector3f(position);
    }
    
    // In normal mode, we could query OpenAL but let's return stored value for consistency
    return new Vector3f(position);
  }

  /**
   * Sets whether this audio source should loop when it reaches the end.
   *
   * @param looping true to enable looping, false to play once
   */
  public void setLooping(boolean looping) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    this.looping = looping;
    
    // Skip OpenAL calls in CI mode
    if (ciMode) {
      return;
    }
    
    alSourcei(sourceId, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
  }

  /**
   * Gets whether this audio source is set to loop.
   *
   * @return true if looping, false otherwise
   */
  public boolean isLooping() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Return stored value in CI mode
    if (ciMode) {
      return looping;
    }
    
    return alGetSourcei(sourceId, AL_LOOPING) == AL_TRUE;
  }

  /**
   * Gets the current playback state of this audio source.
   *
   * @return The OpenAL source state (AL_PLAYING, AL_PAUSED, AL_STOPPED, etc.)
   */
  public int getState() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    // Return default stopped state in CI mode
    if (ciMode) {
      return AL_STOPPED;
    }
    
    return alGetSourcei(sourceId, AL_SOURCE_STATE);
  }

  /**
   * Checks if this audio source is currently playing.
   *
   * @return true if playing, false otherwise
   */
  public boolean isPlaying() {
    return getState() == AL_PLAYING;
  }

  /**
   * Checks if this audio source is currently paused.
   *
   * @return true if paused, false otherwise
   */
  public boolean isPaused() {
    return getState() == AL_PAUSED;
  }

  /**
   * Checks if this audio source is stopped.
   *
   * @return true if stopped, false otherwise
   */
  public boolean isStopped() {
    return getState() == AL_STOPPED;
  }

  @Override
  public void close() {
    if (!closed) {
      // Skip OpenAL calls in CI mode
      if (!ciMode) {
        alDeleteSources(sourceId);
      }
      closed = true;
    }
  }

  /**
   * Checks if this source has been closed.
   *
   * @return true if closed, false otherwise
   */
  public boolean isClosed() {
    return closed;
  }
}