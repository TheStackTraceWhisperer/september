package september.engine.rendering.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.IntSupplier;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;

/**
 * Utility to enable OpenGL debug output.
 * Provides much more informative error messages than glGetError.
 * This class assumes it is being run in an OpenGL 4.3+ context, where debug output is guaranteed to be available.
 */
public final class OpenGLDebugger {

    private static final Logger log = LoggerFactory.getLogger(OpenGLDebugger.class);
    private static Callback debugCallback;

    /**
     * Initializes and enables OpenGL debug output.
     * Must be called after a GL context has been created (e.g., after GL.createCapabilities()).
     */
    public static void init() {
        log.info("Initializing OpenGL Debugger...");

        // Create and set our custom callback that routes messages to SLF4J.
        debugCallback = new Slf4jGLDebugCallback(log);
        GL43.glDebugMessageCallback((Slf4jGLDebugCallback) debugCallback, 0);

        // Set the debug level. Use GL_DEBUG_SEVERITY_NOTIFICATION for verbose output.
        glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_NOTIFICATION, (int[]) null, true);
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        log.info("OpenGL Debugger Initialized.");
    }

    /**
     * Checks for any accumulated OpenGL errors and throws a RuntimeException if any are found.
     * This is the public entry point that uses the real OpenGL error supplier.
     */
    public static void checkErrors() {
        checkErrors(GL11::glGetError);
    }

    /**
     * Test-visible method that checks for errors using a provided supplier.
     * @param errorSupplier A supplier that returns sequential OpenGL error codes.
     */
    static void checkErrors(IntSupplier errorSupplier) {
        int error;
        StringBuilder sb = new StringBuilder();
        while ((error = errorSupplier.getAsInt()) != GL_NO_ERROR) {
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
