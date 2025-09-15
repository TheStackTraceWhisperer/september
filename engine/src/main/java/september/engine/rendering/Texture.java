package september.engine.rendering;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * Represents a 2D texture stored on the GPU.
 *
 * This class encapsulates an OpenGL texture ID and handles loading image data
 * from a file using STB. It is an AutoCloseable resource that must be managed
 * by a ResourceManager.
 */
public class Texture implements AutoCloseable {
  private final int textureId;
  private final int width;
  private final int height;

  /**
   * Loads a texture from an in-memory image buffer.
   *
   * @param imageBuffer A ByteBuffer containing the raw image file data (e.g., a PNG or JPG).
   */
  public Texture(ByteBuffer imageBuffer) {
    ByteBuffer decodedImage;
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);

      // Tell STB to flip the image vertically on load, which is necessary for OpenGL's coordinate system.
      stbi_set_flip_vertically_on_load(true);
      decodedImage = stbi_load_from_memory(imageBuffer, w, h, channels, 4); // Request 4 channels (RGBA)
      if (decodedImage == null) {
        throw new RuntimeException("Failed to load a texture from memory! Reason: " + stbi_failure_reason());
      }
      this.width = w.get(0);
      this.height = h.get(0);
    }

    // --- Upload texture to GPU ---
    this.textureId = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, this.textureId);

    // Set texture parameters for wrapping and filtering
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // Use nearest neighbor filtering for sharp, pixelated sprites
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // Upload the image data to the texture
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0,
      GL_RGBA, GL_UNSIGNED_BYTE, decodedImage);

    // Generate mipmaps for better quality at smaller scales (optional but good practice)
    glGenerateMipmap(GL_TEXTURE_2D);

    // Free the image memory now that it's on the GPU
    stbi_image_free(decodedImage);

    // Unbind the texture
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  /**
   * Binds the texture to the specified texture unit.
   *
   * @param textureUnit The texture unit to activate (e.g., 0 for GL_TEXTURE0).
   */
  public void bind(int textureUnit) {
    glActiveTexture(GL_TEXTURE0 + textureUnit);
    glBindTexture(GL_TEXTURE_2D, textureId);
  }

  /**
   * Unbinds the texture from the currently active texture unit.
   */
  public void unbind() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  @Override
  public void close() {
    glDeleteTextures(textureId);
  }
}
