package september.engine.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for the {@link GlfwContext} lifecycle manager.
 */
class GlfwContextTest {

  @Nested
  @DisplayName("Initialization")
  class Initialization {

    @Test
    @DisplayName("Constructor succeeds and initializes GLFW")
    void constructor_succeedsOnGlfwInit() {
      // Arrange: Mock all static dependencies for the duration of this test.
      try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class);
           MockedStatic<GLFWErrorCallback> callback = mockStatic(GLFWErrorCallback.class)) {

        GLFWErrorCallback mockCallback = mock(GLFWErrorCallback.class);
        callback.when(() -> GLFWErrorCallback.createPrint(System.err)).thenReturn(mockCallback);
        glfw.when(GLFW::glfwInit).thenReturn(true);

        // Act
        assertDoesNotThrow(GlfwContext::new);

        // Assert: Verify that the callback was set and GLFW was initialized.
        verify(mockCallback).set();
        glfw.verify(GLFW::glfwInit);
      }
    }

    @Test
    @DisplayName("Constructor throws IllegalStateException when glfwInit is false")
    void constructor_throwsOnGlfwInitFailure() {
      try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class);
           MockedStatic<GLFWErrorCallback> callback = mockStatic(GLFWErrorCallback.class)) {
        // Arrange
        GLFWErrorCallback mockCallback = mock(GLFWErrorCallback.class);
        callback.when(() -> GLFWErrorCallback.createPrint(System.err)).thenReturn(mockCallback);
        glfw.when(GLFW::glfwInit).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, GlfwContext::new);

        // Verify that the error callback is freed if initialization fails.
        verify(mockCallback).free();
        glfw.verify(() -> GLFW.glfwTerminate(), never());
      }
    }
  }

  @Nested
  @DisplayName("Termination")
  class Termination {

    @Test
    @DisplayName("close() terminates GLFW and frees callbacks")
    void close_terminatesAndFrees() {
      try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class);
           MockedStatic<GLFWErrorCallback> callback = mockStatic(GLFWErrorCallback.class)) {
        // Arrange
        glfw.when(GLFW::glfwInit).thenReturn(true);
        GLFWErrorCallback mockCallback = mock(GLFWErrorCallback.class, "OurCallback");
        callback.when(() -> GLFWErrorCallback.createPrint(System.err)).thenReturn(mockCallback);

        // When close() sets the callback to null, simulate it returning a previous callback
        GLFWErrorCallback previousCallback = mock(GLFWErrorCallback.class, "PreviousCallback");
        glfw.when(() -> GLFW.glfwSetErrorCallback(null)).thenReturn(previousCallback);

        // Act
        GlfwContext context = new GlfwContext();
        context.close();

        // Assert: Verify all expected actions occurred during termination.
        glfw.verify(GLFW::glfwTerminate);
        glfw.verify(() -> GLFW.glfwSetErrorCallback(null));
        verify(previousCallback).free();
      }
    }

    @Test
    @DisplayName("close() is idempotent and does not terminate GLFW multiple times")
    void close_isIdempotent() {
      try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
        // Arrange
        glfw.when(GLFW::glfwInit).thenReturn(true);
        GlfwContext context = new GlfwContext();

        // Act
        context.close();
        context.close(); // Call a second time

        // Assert: Verify that terminate was only called once
        glfw.verify(() -> GLFW.glfwTerminate(), times(1));
      }
    }
  }
}
