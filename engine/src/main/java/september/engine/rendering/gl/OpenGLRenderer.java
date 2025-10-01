package september.engine.rendering.gl;

import org.joml.Matrix4f;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.Texture;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * A concrete implementation of the Renderer interface using OpenGL.
 * This implementation includes a default shader for rendering 2D textured sprites.
 */
public final class OpenGLRenderer implements Renderer {

  private static final String DEFAULT_VERTEX_SHADER_SOURCE =
    "#version 460 core\n" +
      "layout (location = 0) in vec3 aPos;\n" +
      "layout (location = 1) in vec2 aTexCoord;\n" +
      "out vec2 vTexCoord;\n" +
      "uniform mat4 uProjection;\n" +
      "uniform mat4 uView;\n" +
      "uniform mat4 uModel;\n" +
      "void main()\n" +
      "{\n" +
      "    gl_Position = uProjection * uView * uModel * vec4(aPos, 1.0);\n" +
      "    vTexCoord = aTexCoord;\n" +
      "}";

  private static final String DEFAULT_FRAGMENT_SHADER_SOURCE =
    "#version 460 core\n" +
      "out vec4 FragColor;\n" +
      "in vec2 vTexCoord;\n" +
      "uniform sampler2D uTextureSampler;\n" +
      "void main()\n" +
      "{\n" +
      "    FragColor = texture(uTextureSampler, vTexCoord);\n" +
      "}";

  private final Shader defaultShader;

  public OpenGLRenderer() {
    // Create the default shader program for sprite rendering.
    // In a more advanced engine, this would be loaded from files by the ResourceManager.
    this.defaultShader = new Shader(DEFAULT_VERTEX_SHADER_SOURCE, DEFAULT_FRAGMENT_SHADER_SOURCE);
  }

  @Override
  public void beginScene(Camera camera) {
    // Clear the screen to a dark grey color
    glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Prepare the shader for the scene by binding it and setting the camera matrices
    defaultShader.bind();
    defaultShader.setUniform("uProjection", camera.getProjectionMatrix());
    defaultShader.setUniform("uView", camera.getViewMatrix());
  }

  @Override
  public void submit(Mesh mesh, Texture texture, Matrix4f transform) {
    // Bind the specific texture for this sprite to texture unit 0
    texture.bind(0);
    defaultShader.setUniform("uTextureSampler", 0); // Tell the shader to use texture unit 0

    // Set the model matrix for this specific object
    defaultShader.setUniform("uModel", transform);

    // Bind the mesh's VAO
    glBindVertexArray(mesh.getVaoId());

    // Draw the object using its index buffer
    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

    // Unbind the VAO for good practice
    glBindVertexArray(0);
  }

  @Override
  public void endScene() {
    // Unbind the shader program
    defaultShader.unbind();
  }
}
