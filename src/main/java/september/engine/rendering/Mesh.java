package september.engine.rendering;

import lombok.Getter;

import static org.lwjgl.opengl.GL30.*;

/**
 * Represents a 3D model stored on the GPU.
 *
 * This class is a low-level container for the handles to the Vertex Array Object (VAO),
 * Vertex Buffer Object (VBO), and Element Buffer Object (EBO). It encapsulates the
 * setup and cleanup of these OpenGL resources.
 */
public class Mesh implements AutoCloseable {
  @Getter
  private final int vaoId;
  private final int vboId;
  private final int eboId;
  @Getter
  private final int vertexCount;

  public Mesh(float[] vertices, int[] indices) {
    this.vertexCount = indices.length;

    // Create and bind a VAO
    vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    // Create, bind, and buffer a VBO
    vboId = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboId);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    // Create, bind, and buffer an EBO
    eboId = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    // Define vertex attribute pointers (e.g., position)
    // Assuming vertices are just 3 floats for position
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(0);

    // Unbind the VAO
    glBindVertexArray(0);
  }

  @Override
  public void close() {
    glDeleteBuffers(vboId);
    glDeleteBuffers(eboId);
    glDeleteVertexArrays(vaoId);
  }
}
