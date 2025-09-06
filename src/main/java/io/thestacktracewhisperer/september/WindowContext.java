package io.thestacktracewhisperer.september;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GLFW window wrapper that creates an OpenGL 4.6 CORE context. Attempts EGL → OSMesa → default (GLX).
 */
public final class WindowContext implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WindowContext.class);

  private boolean created = false;
  private long handle = 0L;

  private WindowContext() {}

  private static String name(int i) {
    return switch(i) {
      case GLFW.GLFW_OSMESA_CONTEXT_API -> "GLFW_OSMESA_CONTEXT_API";
      case GLFW.GLFW_EGL_CONTEXT_API -> "GLFW_EGL_CONTEXT_API";
      case 0 -> "GLFW";
      default -> "";
    };
  }
  public static WindowContext open(int width, int height, String title) {
    WindowContext ctx = new WindowContext();

    long window = 0L;

    int[][] versions = new int[][] { {4, 6} };
    // Prefer EGL first so zink (OpenGL-on-Vulkan) can provide GL 4.6 when available, then try OSMesa and default
    int[] apis = new int[] { GLFW.GLFW_EGL_CONTEXT_API, GLFW.GLFW_OSMESA_CONTEXT_API, 0 /* default */ };

    outer:
    for (int api : apis) {
      for (int[] ver : versions) {
        log.info("attempting {} with version {}", name(api), ver);

        GLFW.glfwDefaultWindowHints();
        if (api != 0) {
          GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_CREATION_API, api);
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_API);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, ver[0]);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, ver[1]);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        window = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);
        if (window != 0L) {
          break outer;
        }
      }
    }

    if (window == 0L) {
      throw new IllegalStateException("Unable to create GLFW window");
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
