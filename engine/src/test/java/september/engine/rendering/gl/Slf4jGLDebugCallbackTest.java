package september.engine.rendering.gl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.function.BiFunction;

import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_HIGH;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_LOW;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_MEDIUM;
import static org.lwjgl.opengl.GL43.GL_DEBUG_SEVERITY_NOTIFICATION;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for the Slf4jGLDebugCallback.
 * This test verifies that the callback correctly routes OpenGL debug messages
 * to the appropriate SLF4J log level based on severity.
 */
@ExtendWith(MockitoExtension.class)
class Slf4jGLDebugCallbackTest {

  @Mock
  private Logger mockLogger;

  @Mock
  private BiFunction<Integer, Long, String> mockMessageSupplier;

  private Slf4jGLDebugCallback callback;

  @BeforeEach
  void setUp() {
    // Use the test-visible constructor to inject our mocks.
    callback = new Slf4jGLDebugCallback(mockLogger, mockMessageSupplier);
  }

  @Test
  @DisplayName("High severity messages should be logged as ERROR")
  void highSeverity_logsAsError() {
    // Arrange
    when(mockMessageSupplier.apply(anyInt(), anyLong())).thenReturn("Test Error");

    // Act
    callback.invoke(1, 2, 3, GL_DEBUG_SEVERITY_HIGH, 10, 100L, 0);

    // Assert: Verify the logger was called with the raw integer values.
    verify(mockLogger).error(anyString(), eq(1), eq(2), eq(3), eq("Test Error"));
  }

  @Test
  @DisplayName("Medium severity messages should be logged as WARN")
  void mediumSeverity_logsAsWarn() {
    // Arrange
    when(mockMessageSupplier.apply(anyInt(), anyLong())).thenReturn("Test Warning");

    // Act
    callback.invoke(1, 2, 3, GL_DEBUG_SEVERITY_MEDIUM, 12, 100L, 0);

    // Assert: Verify the logger was called with the raw integer values.
    verify(mockLogger).warn(anyString(), eq(1), eq(2), eq(3), eq("Test Warning"));
  }

  @Test
  @DisplayName("Low severity messages should be logged as INFO")
  void lowSeverity_logsAsInfo() {
    // Arrange
    when(mockMessageSupplier.apply(anyInt(), anyLong())).thenReturn("Test Info");

    // Act
    callback.invoke(1, 2, 3, GL_DEBUG_SEVERITY_LOW, 9, 100L, 0);

    // Assert: Verify the logger was called with the raw integer values.
    verify(mockLogger).info(anyString(), eq(1), eq(2), eq(3), eq("Test Info"));
  }

  @Test
  @DisplayName("Notification severity messages should be logged as DEBUG")
  void notificationSeverity_logsAsDebug() {
    // Arrange
    when(mockMessageSupplier.apply(anyInt(), anyLong())).thenReturn("Test Notification");

    // Act
    callback.invoke(1, 2, 3, GL_DEBUG_SEVERITY_NOTIFICATION, 17, 100L, 0);

    // Assert
    verify(mockLogger).debug(anyString(), eq("Test Notification"));
  }

  @Test
  @DisplayName("Unknown severity messages should be logged as TRACE")
  void unknownSeverity_logsAsTrace() {
    // Arrange
    when(mockMessageSupplier.apply(anyInt(), anyLong())).thenReturn("Test Unknown");

    // Act
    callback.invoke(1, 2, 3, 0x9999, 12, 100L, 0);

    // Assert
    verify(mockLogger).trace(anyString(), eq("Test Unknown"));
  }
}
