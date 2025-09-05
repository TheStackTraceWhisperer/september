package io.thestacktracewhisperer.september;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * Minimal GLFW lifecycle manager: initialize and cleanup without creating windows or contexts.
 */
public final class GlfwContext implements AutoCloseable {
  private boolean initialized = false;
  private GLFWErrorCallback errorCallback;

  private GlfwContext() {}

  /**
   * Creates and initializes a GLFW context, ready for use in try-with-resources.
   * @return an initialized GlfwContext
   * @throws IllegalStateException if GLFW fails to initialize
   */
  public static GlfwContext open() {
    GlfwContext ctx = new GlfwContext();

    // Install error callback (prints to stderr)
    ctx.errorCallback = GLFWErrorCallback.createPrint(System.err);
    ctx.errorCallback.set();

    if (!GLFW.glfwInit()) {
      if (ctx.errorCallback != null) {
        ctx.errorCallback.free();
        ctx.errorCallback = null;
      }
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    ctx.initialized = true;
    return ctx;
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
