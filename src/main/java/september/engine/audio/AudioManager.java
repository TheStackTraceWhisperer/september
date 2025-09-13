package september.engine.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

/**
 * Manages the OpenAL audio context and provides high-level audio services.
 * This is analogous to how the Engine class manages the OpenGL context.
 * <p>
 * The AudioManager is responsible for:
 * - Initializing and cleaning up the OpenAL context
 * - Managing global audio settings (master volume, listener properties)
 * - Providing factory methods for creating audio resources
 */
public final class AudioManager implements AutoCloseable {

  private long device;
  private long context;
  private boolean initialized = false;

  /**
   * Initializes the OpenAL audio system.
   * This must be called before any other audio operations.
   */
  public void initialize() {
    if (initialized) {
      throw new IllegalStateException("AudioManager is already initialized");
    }

    // Open the default audio device
    device = alcOpenDevice((CharSequence) null);
    if (device == 0) {
      throw new RuntimeException("Failed to open the default OpenAL device");
    }

    // Create an OpenAL context
    context = alcCreateContext(device, (int[]) null);
    if (context == 0) {
      alcCloseDevice(device);
      throw new RuntimeException("Failed to create OpenAL context");
    }

    // Make the context current
    if (!alcMakeContextCurrent(context)) {
      alcDestroyContext(context);
      alcCloseDevice(device);
      throw new RuntimeException("Failed to make OpenAL context current");
    }

    // Initialize OpenAL capabilities
    ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
    ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

    if (!alCapabilities.OpenAL10) {
      throw new RuntimeException("OpenAL 1.0 is not supported");
    }

    initialized = true;

    // Set up the audio listener at the origin
    setListenerPosition(0.0f, 0.0f, 0.0f);
    setListenerOrientation(0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
    setMasterVolume(1.0f);
  }

  /**
   * Sets the master volume for all audio playback.
   *
   * @param volume Master volume level (0.0f = silent, 1.0f = full volume)
   */
  public void setMasterVolume(float volume) {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    alListenerf(AL_GAIN, Math.max(0.0f, volume));
  }

  /**
   * Gets the current master volume.
   *
   * @return The current master volume level
   */
  public float getMasterVolume() {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    return alGetListenerf(AL_GAIN);
  }

  /**
   * Sets the position of the audio listener (the "ear" in the 3D audio space).
   *
   * @param x X coordinate
   * @param y Y coordinate
   * @param z Z coordinate
   */
  public void setListenerPosition(float x, float y, float z) {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    alListener3f(AL_POSITION, x, y, z);
  }

  /**
   * Sets the orientation of the audio listener.
   *
   * @param forwardX Forward vector X component
   * @param forwardY Forward vector Y component
   * @param forwardZ Forward vector Z component
   * @param upX      Up vector X component
   * @param upY      Up vector Y component
   * @param upZ      Up vector Z component
   */
  public void setListenerOrientation(float forwardX, float forwardY, float forwardZ,
                                     float upX, float upY, float upZ) {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    float[] orientation = {forwardX, forwardY, forwardZ, upX, upY, upZ};
    alListenerfv(AL_ORIENTATION, orientation);
  }

  /**
   * Creates a new AudioSource.
   * The caller is responsible for closing the returned source.
   *
   * @return A new AudioSource instance
   */
  public AudioSource createSource() {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    return new AudioSource();
  }

  /**
   * Creates a new AudioBuffer from raw audio data.
   * The caller is responsible for closing the returned buffer.
   *
   * @param data        The audio data
   * @param channels    Number of channels
   * @param sampleRate  Sample rate in Hz
   * @return A new AudioBuffer instance
   */
  public AudioBuffer createBuffer(java.nio.ShortBuffer data, int channels, int sampleRate) {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    return new AudioBuffer(data, channels, sampleRate);
  }

  /**
   * Loads an AudioBuffer from an OGG Vorbis file.
   * The caller is responsible for closing the returned buffer.
   *
   * @param resourcePath The classpath path to the OGG file
   * @return A new AudioBuffer instance
   */
  public AudioBuffer loadAudioBuffer(String resourcePath) {
    if (!initialized) {
      throw new IllegalStateException("AudioManager is not initialized");
    }
    
    return AudioBuffer.loadFromOggFile(resourcePath);
  }

  /**
   * Checks if the AudioManager has been initialized.
   *
   * @return true if initialized, false otherwise
   */
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void close() {
    if (initialized) {
      // Clean up OpenAL context
      alcMakeContextCurrent(0);
      alcDestroyContext(context);
      alcCloseDevice(device);
      
      initialized = false;
    }
  }
}