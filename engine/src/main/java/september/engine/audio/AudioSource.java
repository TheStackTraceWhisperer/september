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

  public AudioSource() {
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

  /**
   * Binds an audio buffer to this source and starts playback.
   *
   * @param buffer The audio buffer to play
   */
  public void play(AudioBuffer buffer) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
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
    
    alSourcePause(sourceId);
  }

  /**
   * Resumes paused audio playback.
   */
  public void resume() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
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
    
    alSourcef(sourceId, AL_GAIN, Math.max(0.0f, volume));
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
    
    alSourcef(sourceId, AL_PITCH, Math.max(0.1f, pitch));
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
   * Sets whether this audio source should loop when it reaches the end.
   *
   * @param looping true to enable looping, false to play once
   */
  public void setLooping(boolean looping) {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    alSourcei(sourceId, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
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

  /**
   * Gets the current volume of this audio source.
   *
   * @return The current volume level
   */
  public float getVolume() {
    if (closed) {
      throw new IllegalStateException("AudioSource has been closed");
    }
    
    return alGetSourcef(sourceId, AL_GAIN);
  }

  @Override
  public void close() {
    if (!closed) {
      alDeleteSources(sourceId);
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