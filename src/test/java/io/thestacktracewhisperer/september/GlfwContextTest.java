package io.thestacktracewhisperer.september;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GlfwContextTest {

  @Test
  void open_and_close_succeeds_when_glfw_init_returns_true() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange GLFW static methods
      glfw.when(GLFW::glfwInit).thenReturn(true);
      glfw.when(() -> GLFW.glfwSetErrorCallback(Mockito.any(GLFWErrorCallbackI.class))).thenReturn(null);
      glfw.when(() -> GLFW.glfwSetErrorCallback(null)).thenReturn(null);
      // glfwTerminate is void; no stubbing required

      // Act & Assert
      assertDoesNotThrow(() -> {
        try (GlfwContext ignored = GlfwContext.open()) {
          // no-op: just ensure open() works under mocked GLFW
        }
      });

      // Verify key calls
      glfw.verify(GLFW::glfwInit);
      glfw.verify(() -> GLFW.glfwTerminate());
    }
  }

  @Test
  void open_throws_when_glfw_init_returns_false() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange GLFW static methods
      glfw.when(GLFW::glfwInit).thenReturn(false);
      glfw.when(() -> GLFW.glfwSetErrorCallback(Mockito.any(GLFWErrorCallbackI.class))).thenReturn(null);

      // Act & Assert
      assertThrows(IllegalStateException.class, GlfwContext::open);

      // Verify init was attempted, and terminate was not called
      glfw.verify(GLFW::glfwInit);
      glfw.verify(() -> GLFW.glfwTerminate(), Mockito.never());
    }
  }
}
