package september.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test to verify that XKB warning suppression utility works correctly.
 */
class XkbSuppressionUtilityTest {

  @Test
  @DisplayName("executeWithSuppressedStderr should suppress stderr output")
  void suppressesStderrOutput() {
    // Arrange
    PrintStream originalErr = System.err;
    ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
    
    try {
      // Capture what would normally go to stderr 
      System.setErr(new PrintStream(capturedOutput));
      
      // Act - execute something that writes to stderr with suppression
      XkbSuppressionUtility.executeWithSuppressedStderr(() -> {
        System.err.println("This should be suppressed");
        System.err.println("Warning: Could not resolve keysym XF86CameraAccessEnable");
      });
      
      // Assert - stderr should be empty because it was suppressed
      String actualOutput = capturedOutput.toString();
      assertTrue(actualOutput.isEmpty() || !actualOutput.contains("Warning:"), 
          "Expected stderr to be suppressed, but got: " + actualOutput);
      
    } finally {
      // Always restore stderr
      System.setErr(originalErr);
    }
  }

  @Test
  @DisplayName("executeWithSuppressedStderr should restore stderr after execution")
  void restoresStderrAfterExecution() {
    // Arrange
    PrintStream originalErr = System.err;
    
    try {
      // Act
      XkbSuppressionUtility.executeWithSuppressedStderr(() -> {
        // Do nothing - just test that stderr is restored
      });
      
      // Assert
      assertSame(originalErr, System.err, "stderr should be restored to original");
      
    } finally {
      // Ensure cleanup even if test fails
      System.setErr(originalErr);
    }
  }
}