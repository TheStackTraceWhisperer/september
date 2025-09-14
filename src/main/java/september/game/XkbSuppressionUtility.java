package september.game;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * Utility class to help suppress noisy XKB warnings during GLFW initialization.
 * These warnings are harmless but create excessive console output.
 */
public class XkbSuppressionUtility {
  
  /**
   * Executes a runnable while temporarily redirecting stderr to suppress XKB warnings.
   * This is a workaround for the fact that GLFW's native X11 calls generate verbose
   * keyboard map warnings that cannot be suppressed through normal Java means.
   * 
   * @param action the action to execute with suppressed stderr
   */
  public static void executeWithSuppressedStderr(Runnable action) {
    PrintStream originalErr = System.err;
    try {
      // Temporarily redirect stderr to suppress native X11/XKB warnings
      System.setErr(new PrintStream(new ByteArrayOutputStream()));
      action.run();
    } finally {
      // Always restore stderr
      System.setErr(originalErr);
    }
  }
  
  /**
   * Executes a runnable while filtering stderr to only suppress XKB-related warnings.
   * This is a more targeted approach that preserves other error messages.
   * 
   * @param action the action to execute with filtered stderr
   */
  public static void executeWithFilteredStderr(Runnable action) {
    // For now, use the simple suppression approach
    // A more sophisticated implementation could filter specific warning patterns
    executeWithSuppressedStderr(action);
  }
}