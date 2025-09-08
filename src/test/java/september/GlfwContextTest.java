package september;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import september.engine.core.GlfwContext;

import static org.junit.jupiter.api.Assertions.*;

class GlfwContextTest {

  @Test
  void construct_and_close_succeeds_when_glfw_init_returns_true() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange GLFW static methods
      glfw.when(GLFW::glfwInit).thenReturn(true);
      glfw.when(() -> GLFW.glfwSetErrorCallback(Mockito.any(GLFWErrorCallbackI.class))).thenReturn(null);
      glfw.when(() -> GLFW.glfwSetErrorCallback(null)).thenReturn(null);

      // Act & Assert
      assertDoesNotThrow(() -> {
        try (GlfwContext ignored = new GlfwContext()) {
          // no-op: just ensure open() works under mocked GLFW
        }
      });

      // Verify key calls
      glfw.verify(GLFW::glfwInit);
      glfw.verify(() -> GLFW.glfwTerminate());
    }
  }

  @Test
  void constructor_throws_when_glfw_init_returns_false() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange GLFW static methods
      glfw.when(GLFW::glfwInit).thenReturn(false);
      glfw.when(() -> GLFW.glfwSetErrorCallback(Mockito.any(GLFWErrorCallbackI.class))).thenReturn(null);

      // Act & Assert
      assertThrows(IllegalStateException.class, GlfwContext::new);

      // Verify init was attempted, and terminate was not called
      glfw.verify(GLFW::glfwInit);
      glfw.verify(() -> GLFW.glfwTerminate(), Mockito.never());
    }
  }
}
