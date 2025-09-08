package september.engine.rendering.gl;

import org.joml.Matrix4f;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;

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
 */
public class OpenGLRenderer implements Renderer {

  private final Shader basicShader; // In a real engine, you'd have a material/shader system
  private Camera currentCamera;

  public OpenGLRenderer() {
    // A very basic shader for demonstration purposes.
    // In a real application, you would load these from files.
    String vertexSrc = "#version 330 core\n" +
      "layout (location = 0) in vec3 aPos;\n" +
      "uniform mat4 model;\n" +
      "uniform mat4 view;\n" +
      "uniform mat4 projection;\n" +
      "void main() {\n" +
      "    gl_Position = projection * view * model * vec4(aPos, 1.0);\n" +
      "}";
    String fragmentSrc = "#version 330 core\n" +
      "out vec4 FragColor;\n" +
      "void main() {\n" +
      "    FragColor = vec4(1.0, 1.0, 1.0, 1.0);\n" + // Just draw white
      "}";
    this.basicShader = new Shader(vertexSrc, fragmentSrc);
  }

  @Override
  public void beginScene(Camera camera) {
    this.currentCamera = camera;
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the screen
    glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
  }

  @Override
  public void submit(Mesh mesh, Matrix4f transform) {
    if (currentCamera == null) {
      throw new IllegalStateException("Renderer.beginScene() must be called before submit().");
    }

    basicShader.bind();

    // Set shader uniforms
    basicShader.setUniform("model", transform);
    basicShader.setUniform("view", currentCamera.getViewMatrix());
    basicShader.setUniform("projection", currentCamera.getProjectionMatrix());

    // Bind the mesh's VAO and draw it
    glBindVertexArray(mesh.getVaoId());
    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

    // Unbind for good practice
    glBindVertexArray(0);
    basicShader.unbind();
  }

  @Override
  public void endScene() {
    // In this simple renderer, there's nothing to do here.
    // The swapBuffers call will happen in the main engine loop.
  }
}
