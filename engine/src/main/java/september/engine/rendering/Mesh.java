package september.engine.rendering;

import lombok.Getter;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glBufferData;
import static org.lwjgl.opengl.GL30.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;

/**
 * Represents a 2D or 3D model stored on the GPU.
 * <p>
 * For 2D sprites, this class handles interleaved vertex data (position and texture coordinates)
 * and uses an EBO for indexed drawing. It encapsulates the setup and cleanup of these OpenGL resources.
 */
public class Mesh implements AutoCloseable {
  @Getter
  private final int vaoId;
  private final int vboId;
  private final int eboId;
  @Getter
  private final int vertexCount;

  /**
   * Creates a new mesh with interleaved vertex data (position and texture coordinates).
   *
   * @param vertices The interleaved vertex data. Expected layout: [posX, posY, posZ, texU, texV, ...]
   * @param indices  The indices for the EBO.
   */
  public Mesh(float[] vertices, int[] indices) {
    this.vertexCount = indices.length;
    FloatBuffer vertexBuffer = null;
    IntBuffer indicesBuffer = null;

    try {
      // --- Allocate and buffer data ---
      vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
      vertexBuffer.put(vertices).flip();

      indicesBuffer = MemoryUtil.memAllocInt(indices.length);
      indicesBuffer.put(indices).flip();

      // --- Create and bind OpenGL objects ---
      vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      vboId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

      eboId = glGenBuffers();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

      // --- Define Vertex Attribute Pointers ---
      final int VERTEX_SIZE_BYTES = 5 * Float.BYTES; // 3 floats for pos, 2 for UV

      // Attribute 0: Vertex Position (3 floats)
      glVertexAttribPointer(0, 3, GL_FLOAT, false, VERTEX_SIZE_BYTES, 0);
      glEnableVertexAttribArray(0);

      // Attribute 1: Texture Coordinates (2 floats)
      long texCoordOffset = 3 * Float.BYTES; // UVs start after the 3 position floats
      glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE_BYTES, texCoordOffset);
      glEnableVertexAttribArray(1);

      // --- Unbind VAO ---
      glBindVertexArray(0);

    } finally {
      // --- Free native memory ---
      if (vertexBuffer != null) {
        MemoryUtil.memFree(vertexBuffer);
      }
      if (indicesBuffer != null) {
        MemoryUtil.memFree(indicesBuffer);
      }
    }
  }

  @Override
  public void close() {
    glDeleteBuffers(vboId);
    glDeleteBuffers(eboId);
    glDeleteVertexArrays(vaoId);
  }
}
