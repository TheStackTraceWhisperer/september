package september.engine.rendering;

import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glMapBuffer;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * An extension of the basic Mesh class that supports instanced rendering.
 * <p>
 * This mesh can render multiple instances of the same geometry with different
 * transformation matrices in a single draw call using glDrawElementsInstanced.
 * Instance data (transformation matrices) are stored in a separate VBO and
 * updated each frame with the current instance transforms.
 */
public class InstancedMesh implements AutoCloseable {
  @Getter
  private final int vaoId;
  private final int vboId;
  private final int eboId;
  private final int instanceVboId;
  @Getter
  private final int vertexCount;

  private static final int MAX_INSTANCES = 1000; // Maximum instances per batch
  private static final int MATRIX_SIZE_FLOATS = 16; // 4x4 matrix = 16 floats

  /**
   * Creates a new instanced mesh with interleaved vertex data.
   *
   * @param vertices The interleaved vertex data. Expected layout: [posX, posY, posZ, texU, texV, ...]
   * @param indices  The indices for the EBO.
   */
  public InstancedMesh(float[] vertices, int[] indices) {
    this.vertexCount = indices.length;
    FloatBuffer vertexBuffer = null;
    IntBuffer indicesBuffer = null;

    try {
      // --- Allocate and buffer static vertex data ---
      vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
      vertexBuffer.put(vertices).flip();

      indicesBuffer = MemoryUtil.memAllocInt(indices.length);
      indicesBuffer.put(indices).flip();

      // --- Create and bind OpenGL objects ---
      vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // Static vertex data (positions, UVs)
      vboId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

      eboId = glGenBuffers();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

      // --- Define Vertex Attribute Pointers for static data ---
      final int VERTEX_SIZE_BYTES = 5 * Float.BYTES; // 3 floats for pos, 2 for UV

      // Attribute 0: Vertex Position (3 floats)
      glVertexAttribPointer(0, 3, GL_FLOAT, false, VERTEX_SIZE_BYTES, 0);
      glEnableVertexAttribArray(0);

      // Attribute 1: Texture Coordinates (2 floats)
      long texCoordOffset = 3 * Float.BYTES;
      glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE_BYTES, texCoordOffset);
      glEnableVertexAttribArray(1);

      // --- Create instance data VBO ---
      instanceVboId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, instanceVboId);
      // Allocate space for MAX_INSTANCES matrices (will be updated dynamically)
      glBufferData(GL_ARRAY_BUFFER, MAX_INSTANCES * MATRIX_SIZE_FLOATS * Float.BYTES, GL_DYNAMIC_DRAW);

      // --- Instance matrix attributes (locations 2-5 for mat4) ---
      // A mat4 takes up 4 attribute locations, so we need to set up 4 vec4 attributes
      int matrixSizeBytes = MATRIX_SIZE_FLOATS * Float.BYTES;
      int vec4SizeBytes = 4 * Float.BYTES;

      for (int i = 0; i < 4; i++) {
        int location = 2 + i; // Attributes 2, 3, 4, 5
        glVertexAttribPointer(location, 4, GL_FLOAT, false, matrixSizeBytes, i * vec4SizeBytes);
        glEnableVertexAttribArray(location);
        glVertexAttribDivisor(location, 1); // Instance data (update per instance, not per vertex)
      }

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

  /**
   * Updates the instance data with transformation matrices and renders all instances.
   *
   * @param transforms The transformation matrices for each instance.
   */
  public void renderInstanced(List<Matrix4f> transforms) {
    if (transforms.isEmpty()) {
      return;
    }

    int instanceCount = Math.min(transforms.size(), MAX_INSTANCES);

    // Update instance buffer with transformation matrices
    glBindBuffer(GL_ARRAY_BUFFER, instanceVboId);

    // Map buffer for efficient updates
    FloatBuffer instanceBuffer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY).asFloatBuffer();

    for (int i = 0; i < instanceCount; i++) {
      transforms.get(i).get(instanceBuffer);
    }

    glUnmapBuffer(GL_ARRAY_BUFFER);

    // Bind VAO and render all instances
    glBindVertexArray(vaoId);
    glDrawElementsInstanced(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0, instanceCount);
    glBindVertexArray(0);
  }

  /**
   * Gets the maximum number of instances that can be rendered in a single batch.
   *
   * @return The maximum instance count.
   */
  public int getMaxInstances() {
    return MAX_INSTANCES;
  }

  @Override
  public void close() {
    glDeleteBuffers(vboId);
    glDeleteBuffers(eboId);
    glDeleteBuffers(instanceVboId);
    glDeleteVertexArrays(vaoId);
  }
}
