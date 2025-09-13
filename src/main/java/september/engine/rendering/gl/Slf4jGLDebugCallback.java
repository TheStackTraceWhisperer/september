package september.engine.rendering.gl;

import org.lwjgl.opengl.GLDebugMessageCallback;
import org.slf4j.Logger;

import java.util.function.BiFunction;

import static org.lwjgl.opengl.GL43.*;

/**
 * A custom GLDebugMessageCallback that routes OpenGL debug messages to a provided SLF4J logger.
 * This class is designed for testability by allowing the message-retrieving function to be injected.
 */
public class Slf4jGLDebugCallback extends GLDebugMessageCallback {

    private final Logger log;
    private final BiFunction<Integer, Long, String> messageSupplier;

    /**
     * Production constructor. Uses the real LWJGL method to get the message string.
     * @param logger The SLF4J logger to use.
     */
    public Slf4jGLDebugCallback(Logger logger) {
        this(logger, GLDebugMessageCallback::getMessage);
    }

    /**
     * Test-visible constructor for injecting a message supplier.
     * @param logger The SLF4J logger to use.
     * @param messageSupplier A function that takes a length and a long pointer and returns a message string.
     */
    Slf4jGLDebugCallback(Logger logger, BiFunction<Integer, Long, String> messageSupplier) {
        this.log = logger;
        this.messageSupplier = messageSupplier;
    }

    @Override
    public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
        String msg = messageSupplier.apply(length, message);
        switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH:
                // Pass raw integers to the logger; let the framework format them.
                log.error("[GL] High - Source: 0x{}, Type: 0x{}, ID: {}: {}", source, type, id, msg);
                break;
            case GL_DEBUG_SEVERITY_MEDIUM:
                log.warn("[GL] Medium - Source: 0x{}, Type: 0x{}, ID: {}: {}", source, type, id, msg);
                break;
            case GL_DEBUG_SEVERITY_LOW:
                log.info("[GL] Low - Source: 0x{}, Type: 0x{}, ID: {}: {}", source, type, id, msg);
                break;
            case GL_DEBUG_SEVERITY_NOTIFICATION:
                log.debug("[GL] Notification: {}", msg);
                break;
            default:
                log.trace("[GL] Unknown: {}", msg);
                break;
        }
    }
}
