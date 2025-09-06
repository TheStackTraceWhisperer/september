package io.thestacktracewhisperer.september;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class WindowContextTest {

  @Test
  void open_creates_window_and_close_cleans_up() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      long handle = 777L;
      glfw.when(() -> GLFW.glfwCreateWindow(800, 600, "GLFW", 0L, 0L)).thenReturn(handle);

      assertDoesNotThrow(() -> {
        try (WindowContext ctx = WindowContext.open(800, 600, "GLFW")) {
          // no-op; lifecycle validated via verifications
        }
      });

      glfw.verify(GLFW::glfwDefaultWindowHints);
      glfw.verify(() -> GLFW.glfwCreateWindow(800, 600, "GLFW", 0L, 0L));
      glfw.verify(() -> GLFW.glfwDestroyWindow(handle));
    }
  }

  @Test
  void open_throws_when_glfw_create_window_fails() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      glfw.when(() -> GLFW.glfwCreateWindow(1, 1, "fail", 0L, 0L)).thenReturn(0L);
      assertThrows(IllegalStateException.class, () -> WindowContext.open(1, 1, "fail"));
      glfw.verify(() -> GLFW.glfwDestroyWindow(Mockito.anyLong()), Mockito.never());
    }
  }
}
