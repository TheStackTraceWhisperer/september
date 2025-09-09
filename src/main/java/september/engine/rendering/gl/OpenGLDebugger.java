package september.engine.rendering.gl;

import org.lwjgl.opengl.GL;

import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL11.*; // for glGetError and error codes

/**
 * Utility to enable OpenGL debug output.
 * Provides much more informative error messages than glGetError.
 */
public final class OpenGLDebugger {

  private static Callback debugCallback;

  /**
   * Initializes and enables OpenGL debug output.
   * Must be called after a GL context has been created (e.g., after GL.createCapabilities()).
   */
  public static void init() {
    System.out.println("Initializing OpenGL Debugger...");
    // Check if the context supports debug output
    if (GL.getCapabilities().GL_KHR_debug) {
      debugCallback = GLUtil.setupDebugMessageCallback(System.err);
      // Set the debug level. Use GL_DEBUG_SEVERITY_NOTIFICATION for verbose output.
      GL43.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_HIGH, (int[]) null, true);
      glEnable(GL_DEBUG_OUTPUT);
      glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
      System.out.println("OpenGL Debugger Initialized.");
    } else {
      System.err.println("Warning: GL_KHR_debug not supported. Full debug output is unavailable.");
    }
  }

  /**
   * Checks for any accumulated OpenGL errors and throws a RuntimeException if any are found.
   * This method is intended mainly for unit testing and development diagnostics.
   */
  public static void checkErrors() {
    int error;
    StringBuilder sb = new StringBuilder();
    while ((error = glGetError()) != GL_NO_ERROR) {
      if (sb.length() == 0) {
        sb.append("OpenGL error(s): ");
      }
      sb.append(errorToString(error)).append(' ');
    }
    if (sb.length() > 0) {
      throw new RuntimeException(sb.toString().trim());
    }
  }

  private static String errorToString(int code) {
    return switch (code) {
      case GL_INVALID_ENUM -> "GL_INVALID_ENUM";
      case GL_INVALID_VALUE -> "GL_INVALID_VALUE";
      case GL_INVALID_OPERATION -> "GL_INVALID_OPERATION";
      case GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW";
      case GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW";
      case GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY";
      case GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION";
      default -> "UNKNOWN_ERROR(" + code + ")";
    };
  }

  /**
   * Cleans up the debug callback. Should be called before the context is destroyed.
   */
  public static void cleanup() {
    if (debugCallback != null) {
      debugCallback.free();
      debugCallback = null;
    }
  }
}
