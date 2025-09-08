package september.engine.rendering;

import org.joml.Matrix4f;

/**
 * Defines the contract for a rendering engine.
 *
 * This provides a high-level, graphics-API-agnostic interface for submitting
 * renderable objects to be drawn to the screen. The RenderSystem uses this
 * interface to remain decoupled from low-level details like OpenGL.
 */
public interface Renderer {

  /**
   * Initializes the renderer for a new frame. This is where operations like
   * clearing the screen and setting up the camera happen.
   * @param camera The camera defining the viewpoint for this frame.
   */
  void beginScene(Camera camera);

  /**
   * Submits a single mesh to be rendered.
   * @param mesh The mesh object containing GPU buffer handles.
   * @param transform The model-to-world transformation matrix.
   */
  void submit(Mesh mesh, Matrix4f transform);

  /**
   * Finalizes the frame. In a simple renderer, this might do nothing.
   * In a batch renderer, this is where the queued draw calls are executed.
   */
  void endScene();
}
