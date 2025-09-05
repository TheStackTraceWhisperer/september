package io.thestacktracewhisperer.september;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WindowContextTest {

  @Test
  void open_creates_and_close_destroys_window_when_success() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange: create window succeeds
      long handle = 12345L;
      glfw.when(() -> GLFW.glfwCreateWindow(640, 480, "Test", 0L, 0L)).thenReturn(handle);

      // Act
      try (WindowContext window = WindowContext.open(640, 480, "Test")) {
        assertEquals(handle, window.handle());
      }

      // Assert: verify lifecycle interactions
      glfw.verify(GLFW::glfwDefaultWindowHints);
      glfw.verify(() -> GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API));
      glfw.verify(() -> GLFW.glfwCreateWindow(640, 480, "Test", 0L, 0L));
      glfw.verify(() -> GLFW.glfwDestroyWindow(handle));
    }
  }

  @Test
  void open_throws_when_create_window_fails() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange: create window fails
      glfw.when(() -> GLFW.glfwCreateWindow(1, 1, "Fail", 0L, 0L)).thenReturn(0L);

      // Act & Assert
      assertThrows(IllegalStateException.class, () -> WindowContext.open(1, 1, "Fail"));

      // Ensure we did not attempt to destroy any window
      glfw.verify(() -> GLFW.glfwDestroyWindow(Mockito.anyLong()), Mockito.never());
    }
  }
}

