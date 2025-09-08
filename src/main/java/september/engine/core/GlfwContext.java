package september.engine.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * Minimal GLFW lifecycle manager: initialize and cleanup without creating windows or contexts.
 */
public final class GlfwContext implements AutoCloseable {
  private boolean initialized = false;
  private GLFWErrorCallback errorCallback;

  /**
   * Constructs and initializes a GLFW context.
   *
   * @throws IllegalStateException if GLFW fails to initialize
   */
  public GlfwContext() {
    // Install error callback (prints to stderr)
    this.errorCallback = GLFWErrorCallback.createPrint(System.err);
    this.errorCallback.set();

    if (!GLFW.glfwInit()) {
      if (this.errorCallback != null) {
        this.errorCallback.free();
        this.errorCallback = null;
      }
      throw new IllegalStateException("Unable to initialize GLFW");
    }
    this.initialized = true;
  }

  /**
   * Terminates GLFW and frees the error callback. Safe to call multiple times.
   */
  @Override
  public void close() {
    if (!initialized) return;

    GLFW.glfwTerminate();

    // Clear the GLFW error callback and free it
    GLFWErrorCallback prev = GLFW.glfwSetErrorCallback(null);
    if (prev != null) prev.free();

    errorCallback = null;
    initialized = false;
  }
}
