package september.engine.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GLFW window wrapper that creates an OpenGL 4.6 core profile context.
 */
public final class WindowContext implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WindowContext.class);

  private boolean created = false;
  private long handle = 0L;

  public WindowContext(int width, int height, String title) {
    // Single attempt: request OpenGL 4.6 core profile
    GLFW.glfwDefaultWindowHints();
    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

    log.info("Creating GLFW window with OpenGL 4.6 core profile");
    long window = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
    if (window == 0L) {
      throw new IllegalStateException("Unable to create GLFW window (requested OpenGL 4.6 core profile)");
    }

    this.handle = window;
    this.created = true;

    // Make context current
    GLFW.glfwMakeContextCurrent(window);
    if (GLFW.glfwGetCurrentContext() != window) {
      GLFW.glfwDestroyWindow(window);
      this.handle = 0L;
      this.created = false;
      throw new IllegalStateException("Failed to make OpenGL context current");
    }

    try {
      GL.createCapabilities();
      try {
        String glVersion = GL11.glGetString(GL11.GL_VERSION);
        String glRenderer = GL11.glGetString(GL11.GL_RENDERER);
        String glVendor = GL11.glGetString(GL11.GL_VENDOR);
        log.info("OpenGL reported version: {}", glVersion);
        log.info("OpenGL renderer: {}", glRenderer);
        log.info("OpenGL vendor: {}", glVendor);
      } catch (Throwable t) {
        log.warn("Failed to query OpenGL version information", t);
      }
    } catch (IllegalStateException ise) {
      GLFW.glfwDestroyWindow(window);
      this.handle = 0L;
      this.created = false;
      throw ise;
    }

    log.info("Created GLFW window: handle={}", window);
  }

  public long handle() { return handle; }

  public void swapBuffers() { GLFW.glfwSwapBuffers(handle); }

  @Override
  public void close() {
    if (!created) return;
    if (handle != 0L) {
      log.info("Destroying GLFW window: handle={}", handle);
      GLFW.glfwDestroyWindow(handle);
      handle = 0L;
    }
    created = false;
  }
}
