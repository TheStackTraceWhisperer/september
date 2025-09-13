package september.engine.audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Represents an OpenAL audio buffer that holds audio data.
 * This is analogous to how a Texture holds image data for rendering.
 * <p>
 * Audio buffers are immutable once created and can be shared among multiple AudioSources.
 * This class manages the native OpenAL buffer lifecycle and provides automatic cleanup.
 */
public final class AudioBuffer implements AutoCloseable {

  private final int bufferId;
  private boolean closed = false;
  private final boolean ciMode;

  /**
   * Creates an AudioBuffer from raw PCM audio data.
   *
   * @param data        The audio data as 16-bit signed integers
   * @param channels    Number of audio channels (1 for mono, 2 for stereo)
   * @param sampleRate  Sample rate in Hz (e.g., 44100)
   */
  public AudioBuffer(ShortBuffer data, int channels, int sampleRate) {
    // Check if running in CI mode
    this.ciMode = "true".equalsIgnoreCase(System.getenv("CI"));
    
    if (ciMode) {
      // In CI mode, use a dummy buffer ID
      this.bufferId = -1;
    } else {
      this.bufferId = alGenBuffers();
      
      int format = channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
      alBufferData(bufferId, format, data, sampleRate);
      
      if (alGetError() != AL_NO_ERROR) {
        throw new RuntimeException("Failed to upload audio data to OpenAL buffer");
      }
    }
  }

  /**
   * Creates an AudioBuffer by loading an OGG Vorbis file from the classpath.
   *
   * @param resourcePath The classpath path to the OGG file
   * @return A new AudioBuffer containing the loaded audio data
   */
  public static AudioBuffer loadFromOggFile(String resourcePath) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      // Load the OGG file into a ByteBuffer
      ByteBuffer oggData = loadResourceAsBuffer(resourcePath);
      if (oggData == null) {
        throw new RuntimeException("Failed to load audio file: " + resourcePath);
      }

      // Decode the OGG file
      IntBuffer channelsBuffer = stack.mallocInt(1);
      IntBuffer sampleRateBuffer = stack.mallocInt(1);
      
      ShortBuffer audioData = stb_vorbis_decode_memory(oggData, channelsBuffer, sampleRateBuffer);
      if (audioData == null) {
        throw new RuntimeException("Failed to decode OGG file: " + resourcePath);
      }

      int channels = channelsBuffer.get(0);
      int sampleRate = sampleRateBuffer.get(0);

      return new AudioBuffer(audioData, channels, sampleRate);
    }
  }

  /**
   * Helper method to load a resource file into a ByteBuffer.
   * This follows the same pattern used elsewhere in the engine for asset loading.
   */
  private static ByteBuffer loadResourceAsBuffer(String resourcePath) {
    try (var inputStream = AudioBuffer.class.getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        return null;
      }
      
      byte[] bytes = inputStream.readAllBytes();
      ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
      buffer.put(bytes);
      buffer.flip();
      return buffer;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load audio resource: " + resourcePath, e);
    }
  }

  /**
   * Gets the OpenAL buffer ID for this audio buffer.
   * This is used internally by AudioSource to bind the buffer.
   *
   * @return The OpenAL buffer ID
   */
  public int getBufferId() {
    if (closed) {
      throw new IllegalStateException("AudioBuffer has been closed");
    }
    return bufferId;
  }

  @Override
  public void close() {
    if (!closed) {
      // Skip OpenAL calls in CI mode
      if (!ciMode) {
        alDeleteBuffers(bufferId);
      }
      closed = true;
    }
  }

  /**
   * Checks if this buffer has been closed.
   *
   * @return true if the buffer has been closed, false otherwise
   */
  public boolean isClosed() {
    return closed;
  }
}