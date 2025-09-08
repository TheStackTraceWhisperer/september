package september.engine.rendering.gl;

import org.lwjgl.opengl.GL;

import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import static org.lwjgl.opengl.GL43.*;

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
   * Cleans up the debug callback. Should be called before the context is destroyed.
   */
  public static void cleanup() {
    if (debugCallback != null) {
      debugCallback.free();
      debugCallback = null;
    }
  }
}
