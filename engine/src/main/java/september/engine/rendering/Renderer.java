package september.engine.rendering;

import org.joml.Matrix4f;

/**
 * An abstract interface for the rendering engine.
 * This decouples the RenderSystem from the specific graphics API (e.g., OpenGL).
 */
public interface Renderer {
  /**
   * Prepares the renderer for a new frame. This is typically called once at the
   * beginning of the RenderSystem's update method.
   *
   * @param camera The camera defining the view and projection for this frame.
   */
  void beginScene(Camera camera);

  /**
   * Submits a textured mesh to be rendered this frame.
   *
   * @param mesh      The mesh to draw (e.g., a quad for a sprite).
   * @param texture   The texture to apply to the mesh.
   * @param transform The model transformation matrix (position, rotation, scale).
   */
  void submit(Mesh mesh, Texture texture, Matrix4f transform);

  /**
   * Executes any final rendering commands for the frame. This is typically called
   * once at the end of the RenderSystem's update method.
   */
  void endScene();
}

