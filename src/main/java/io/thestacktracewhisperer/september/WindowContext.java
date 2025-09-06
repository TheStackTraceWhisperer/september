package io.thestacktracewhisperer.september;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GLFW window wrapper that creates an OpenGL 4.6 CORE context.
 */
public final class WindowContext implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WindowContext.class);

  private boolean created = false;
  private long handle = 0L;

  private WindowContext() {}

  public static WindowContext open(int width, int height, String title) {
    WindowContext ctx = new WindowContext();

    // Configure OpenGL 4.6 CORE context
    GLFW.glfwDefaultWindowHints();
    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
    GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
    GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

    long window = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
    if (window == 0L) {
      throw new IllegalStateException("Unable to create GLFW window with OpenGL 4.6 CORE context");
    }

    ctx.handle = window;
    ctx.created = true;

    // Make context current (may fail in mocked tests)
    GLFW.glfwMakeContextCurrent(window);
    if (GLFW.glfwGetCurrentContext() == window) {
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
    } else {
      log.debug("Skipping GL capability creation (no current context) – likely running under mocked GLFW in tests.");
    }

    log.info("Created GLFW window: handle={}", window);
    return ctx;
  }

  public long handle() {
    return handle;
  }

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
