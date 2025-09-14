package september.engine.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for X11Configuration to ensure it properly configures environment
 * without causing side effects.
 */
class X11ConfigurationTest {

  @BeforeEach
  void setUp() {
    X11Configuration.resetForTesting();
  }

  @AfterEach
  void tearDown() {
    X11Configuration.resetForTesting();
  }

  @Test
  @DisplayName("configureForCleanOutput should be idempotent")
  void configureForCleanOutputShouldBeIdempotent() {
    assertFalse(X11Configuration.isConfigured());
    
    X11Configuration.configureForCleanOutput();
    assertTrue(X11Configuration.isConfigured());
    
    // Calling again should be safe and not change state
    X11Configuration.configureForCleanOutput();
    assertTrue(X11Configuration.isConfigured());
  }

  @Test
  @DisplayName("configureForCleanOutput should set system properties for XKB")
  void configureForCleanOutputShouldSetSystemProperties() {
    X11Configuration.configureForCleanOutput();
    
    // Verify that XKB-related system properties are set
    // These may be set in various formats that native libraries can read
    assertTrue(
        System.getProperty("xkb.log.level") != null ||
        System.getProperty("XKB_LOG_LEVEL") != null,
        "XKB log level should be configured"
    );
    
    assertTrue(X11Configuration.isConfigured());
  }

  @Test
  @DisplayName("configuration should not throw exceptions")
  void configurationShouldNotThrowExceptions() {
    assertDoesNotThrow(() -> {
      X11Configuration.configureForCleanOutput();
    });
  }
}