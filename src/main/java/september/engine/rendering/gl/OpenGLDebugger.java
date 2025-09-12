package september.engine.rendering.gl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;

/**
 * Utility to enable OpenGL debug output.
 * Provides much more informative error messages than glGetError.
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
        if (GL.getCapabilities().GL_KHR_debug) {
            // Create a custom callback that routes messages to our SLF4J logger
            GLDebugMessageCallback callback = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                String msg = GLDebugMessageCallback.getMessage(length, message);
                switch (severity) {
                    case GL_DEBUG_SEVERITY_HIGH:
                        log.error("[GL] High - Source: 0x{}, Type: 0x{}, ID: {}: {}", Integer.toHexString(source), Integer.toHexString(type), id, msg);
                        break;
                    case GL_DEBUG_SEVERITY_MEDIUM:
                        log.warn("[GL] Medium - Source: 0x{}, Type: 0x{}, ID: {}: {}", Integer.toHexString(source), Integer.toHexString(type), id, msg);
                        break;
                    case GL_DEBUG_SEVERITY_LOW:
                        log.info("[GL] Low - Source: 0x{}, Type: 0x{}, ID: {}: {}", Integer.toHexString(source), Integer.toHexString(type), id, msg);
                        break;
                    case GL_DEBUG_SEVERITY_NOTIFICATION:
                        log.debug("[GL] Notification: {}", msg);
                        break;
                    default:
                        log.trace("[GL] Unknown: {}", msg);
                        break;
                }
            });

            debugCallback = callback; // Store it to be freed later
            GL43.glDebugMessageCallback(callback, 0);

            // Set the debug level. Use GL_DEBUG_SEVERITY_NOTIFICATION for verbose output.
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_NOTIFICATION, (int[]) null, true);
            glEnable(GL_DEBUG_OUTPUT);
            glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
            log.info("OpenGL Debugger Initialized.");
        } else {
            log.warn("GL_KHR_debug not supported. Full debug output is unavailable.");
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
