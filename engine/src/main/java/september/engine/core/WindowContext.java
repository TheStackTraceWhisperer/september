package september.engine.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
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
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // Added for resize support
    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // Create hidden, show after setup

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
      // Set initial state
      GLFW.glfwSwapInterval(1); // Enable v-sync
      GL30.glViewport(0, 0, width, height); // Set initial viewport

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

    GLFW.glfwShowWindow(window);
    log.info("Created GLFW window: handle={}", window);
  }

  public long handle() {
    return handle;
  }

  public void swapBuffers() {
    GLFW.glfwSwapBuffers(handle);
  }

  /**
   * Processes all pending events for the window.
   */
  public void pollEvents() {
    GLFW.glfwPollEvents();
  }

  /**
   * Sets a listener to be called when the window's framebuffer is resized.
   * This method handles setting the GLFW callback and managing the GL viewport.
   *
   * @param listener The listener that will handle the resize event for game logic (e.g., camera).
   */
  public void setResizeListener(WindowResizeListener listener) {
    GLFW.glfwSetFramebufferSizeCallback(handle, (win, w, h) -> {
      if (w > 0 && h > 0) {
        // The context is responsible for the GL call
        GL30.glViewport(0, 0, w, h);
        // The listener is responsible for game-logic updates (like camera)
        if (listener != null) {
          listener.onResize(w, h);
        }
      }
    });
  }

  @Override
  public void close() {
    if (!created) {
      return;
    }
    if (handle != 0L) {
      log.info("Destroying GLFW window: handle={}", handle);
      GLFW.glfwDestroyWindow(handle);
      handle = 0L;
    }
    created = false;
  }
}
