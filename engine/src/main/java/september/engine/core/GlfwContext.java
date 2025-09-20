package september.engine.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal GLFW lifecycle manager: initialize and cleanup without creating windows or contexts.
 */
public final class GlfwContext implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(GlfwContext.class);
  private boolean initialized = false;
  private GLFWErrorCallback errorCallback;

  /**
   * Constructs and initializes a GLFW context.
   *
   * @throws IllegalStateException if GLFW fails to initialize
   */
  public GlfwContext() {
    // Install an error callback that logs to our SLF4J logger.
    this.errorCallback = GLFWErrorCallback.create((error, description) ->
      log.error("[GLFW Error] Code: {}, Description: {}", error, GLFWErrorCallback.getDescription(description))
    );
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
    if (!initialized) {
      return;
    }

    GLFW.glfwTerminate();

    // Clear the GLFW error callback and free it
    GLFWErrorCallback prev = GLFW.glfwSetErrorCallback(null);
    if (prev != null) {
      prev.free();
    }

    errorCallback = null;
    initialized = false;
  }
}
