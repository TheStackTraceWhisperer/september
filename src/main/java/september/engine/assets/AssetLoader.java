package september.engine.assets;

import org.lwjgl.BufferUtils;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

/**
 * A utility class for loading raw asset data from files.
 * This class handles the low-level details of reading files from the classpath.
 */
public final class AssetLoader {

  // Private constructor to prevent instantiation of this utility class.
  private AssetLoader() {}

  /**
   * Loads a shader program by reading vertex and fragment shader source files.
   * @param vertexPath   The classpath resource path to the vertex shader file.
   * @param fragmentPath The classpath resource path to the fragment shader file.
   * @return A new, compiled Shader object.
   */
  public static Shader loadShader(String vertexPath, String fragmentPath) {
    String vertexSource = loadResourceAsString(fragmentPath);
    String fragmentSource = loadResourceAsString(fragmentPath);
    return new Shader(vertexSource, fragmentSource);
  }

  /**
   * Loads a texture from an image file on the classpath.
   * @param filePath The classpath resource path to the image file.
   * @return A new Texture object.
   */
  public static Texture loadTexture(String filePath) {
    try {
      ByteBuffer imageBuffer = loadResourceAsByteBuffer(filePath);
      return new Texture(imageBuffer);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load texture resource: " + filePath, e);
    }
  }

  /**
   * Reads a resource file from the classpath into a single String.
   * @param filePath The classpath resource path.
   * @return The contents of the file as a string.
   */
  private static String loadResourceAsString(String filePath) {
    try (InputStream is = AssetLoader.class.getClassLoader().getResourceAsStream(filePath)) {
      if (is == null) {
        throw new IOException("Resource not found: " + filePath);
      }
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read resource: " + filePath, e);
    }
  }

  private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }

  /**
   * Reads a resource file from the classpath into a direct ByteBuffer.
   * This method reads the resource in chunks and is robust for use inside JARs.
   * @param filePath The classpath resource path.
   * @return A ByteBuffer containing the file data.
   */
  private static ByteBuffer loadResourceAsByteBuffer(String filePath) throws IOException {
    int bufferSize = 8192;
    ByteBuffer buffer = BufferUtils.createByteBuffer(bufferSize);

    try (InputStream source = AssetLoader.class.getClassLoader().getResourceAsStream(filePath);
         ReadableByteChannel rbc = Channels.newChannel(source)) {

      if (source == null) {
        throw new IOException("Resource not found: " + filePath);
      }

      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          buffer = resizeBuffer(buffer, buffer.capacity() * 2);
        }
      }
    }

    buffer.flip();
    return buffer;
  }
}
