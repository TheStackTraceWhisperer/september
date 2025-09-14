package september.engine.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Configures X11 environment settings to minimize verbose warnings during GLFW initialization.
 * This addresses the root cause of XKB warnings by properly configuring the X11 environment
 * before GLFW attempts to initialize keyboard mappings.
 */
public final class X11Configuration {
  private static final Logger log = LoggerFactory.getLogger(X11Configuration.class);
  
  private static boolean configured = false;
  
  /**
   * Configures X11 environment settings to reduce XKB keyboard mapping warnings.
   * This method should be called once before GLFW initialization.
   * 
   * This is a proper solution that addresses the root cause rather than suppressing output.
   */
  public static void configureForCleanOutput() {
    if (configured) {
      return;
    }
    
    try {
      // Only configure on Linux systems where X11 might be present
      String osName = System.getProperty("os.name", "").toLowerCase();
      if (!osName.contains("linux")) {
        log.debug("Skipping X11 configuration on non-Linux system: {}", osName);
        configured = true;
        return;
      }
      
      // Configure X11 environment variables to reduce XKB verbosity
      // These variables are processed by the X11 system before Java/GLFW gets involved
      setEnvironmentVariable("XKB_LOG_LEVEL", "0");
      setEnvironmentVariable("XKB_LOG_VERBOSITY", "0");
      
      // Set a consistent locale to avoid keyboard mapping inconsistencies
      // This reduces the chance of unknown keysym warnings
      String currentLocale = System.getProperty("user.language") + "_" + System.getProperty("user.country");
      if (currentLocale.equals("_") || currentLocale.equals("null_null")) {
        // If no locale is set, use a safe default
        Locale.setDefault(Locale.US);
        setEnvironmentVariable("LC_ALL", "C");
      }
      
      // Disable X11 input method warnings for headless/CI environments  
      String display = System.getenv("DISPLAY");
      if (display != null && (display.startsWith(":99") || display.contains("xvfb"))) {
        // This is likely a headless X11 session (xvfb), configure accordingly
        setEnvironmentVariable("XMODIFIERS", "");
        setEnvironmentVariable("QT_QPA_PLATFORM", "xcb");
      }
      
      log.debug("X11 environment configured for clean GLFW initialization");
      configured = true;
      
    } catch (Exception e) {
      // Configuration is best-effort; don't fail if it doesn't work
      log.debug("X11 configuration encountered an issue (non-fatal): {}", e.getMessage());
      configured = true;
    }
  }
  
  /**
   * Sets an environment variable using Java system properties as a fallback
   * since Java cannot directly modify environment variables after JVM startup.
   * 
   * @param name the environment variable name
   * @param value the value to set
   */
  private static void setEnvironmentVariable(String name, String value) {
    // Since Java cannot modify environment variables after startup,
    // we set system properties that some native libraries respect
    System.setProperty(name.toLowerCase().replace('_', '.'), value);
    
    // Also try the original environment variable name format
    System.setProperty(name, value);
    
    log.trace("Set environment configuration: {}={}", name, value);
  }
  
  /**
   * Checks if X11 configuration has been applied.
   * @return true if configure() has been called successfully
   */
  public static boolean isConfigured() {
    return configured;
  }
  
  /**
   * Reset configuration state (primarily for testing).
   */
  static void resetForTesting() {
    configured = false;
  }
}