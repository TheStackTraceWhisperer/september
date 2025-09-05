package io.thestacktracewhisperer.september;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the lifecycle of a GLFW window without creating an OpenGL context.
 * Requires GLFW to be initialized (e.g., via GlfwContext.open()).
 */
public final class WindowContext implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WindowContext.class);

  private boolean created = false;
  private long handle = 0L;

  private WindowContext() {}

  /**
   * Creates a window using GLFW without an OpenGL context (GLFW_NO_API).
   * Expects GLFW to be initialized already.
   *
   * @param width  window width in pixels
   * @param height window height in pixels
   * @param title  window title
   * @return an initialized WindowContext wrapping the created window
   * @throws IllegalStateException if the window cannot be created
   */
  public static WindowContext open(int width, int height, String title) {
    WindowContext ctx = new WindowContext();

    // Ensure hints are in a known state
    GLFW.glfwDefaultWindowHints();
    // Do not create an OpenGL context
    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);

    long window = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
    if (window == 0L) {
      throw new IllegalStateException("Unable to create GLFW window");
    }

    ctx.handle = window;
    ctx.created = true;

    // Log created window handle for visual verification
    log.info("Created GLFW window: handle={}", window);

    return ctx;
  }

  /**
   * @return the raw GLFW window handle
   */
  public long handle() {
    return handle;
  }

  /**
   * Destroys the GLFW window. Safe to call multiple times.
   */
  @Override
  public void close() {
    if (!created) return;
    if (handle != 0L) {
      // Log handle before destroying for visibility
      log.info("Destroying GLFW window: handle={}", handle);
      GLFW.glfwDestroyWindow(handle);
      handle = 0L;
    }
    created = false;
  }
}
