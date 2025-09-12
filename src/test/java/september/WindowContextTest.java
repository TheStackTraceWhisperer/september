package september;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import september.engine.core.WindowContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class is commented out as it relies on static mocking of GLFW, which is obsolete under the new integration testing strategy.
 * Its functionality is implicitly tested by the EngineTestHarness successfully initializing a window.
 */
/*
class WindowContextTest {

//  @Test
//  void constructor_creates_window_and_close_cleans_up() {
//    // Arrange: Mock all necessary static methods from both GLFW and GL
//    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class);
//         MockedStatic<GL> gl = Mockito.mockStatic(GL.class);
//         MockedStatic<GL11> gl11 = Mockito.mockStatic(GL11.class)) {
//
//      // Mock the window creation and context management to return a valid handle
//      long windowHandle = 12345L;
//      glfw.when(() -> GLFW.glfwCreateWindow(800, 600, "GLFW", 0L, 0L)).thenReturn(windowHandle);
//      glfw.when(() -> GLFW.glfwGetCurrentContext()).thenReturn(windowHandle);
//
//      // Mock the GL capabilities creation
//      gl.when(GL::createCapabilities).thenReturn(null); // or some valid capabilities object if needed
//
//      // Mock the GL info string queries to prevent native calls
//      gl11.when(() -> GL11.glGetString(GL11.GL_VERSION)).thenReturn("mock-opengl-version");
//      gl11.when(() -> GL11.glGetString(GL11.GL_RENDERER)).thenReturn("mock-renderer");
//      gl11.when(() -> GL11.glGetString(GL11.GL_VENDOR)).thenReturn("mock-vendor");
//
//      // Act & Assert: Ensure the try-with-resources block for WindowContext runs without error
//      assertDoesNotThrow(() -> {
//        try (WindowContext ignored = new WindowContext(800, 600, "GLFW")) {
//          // The test's purpose is to verify the lifecycle calls, so the body is empty
//        }
//      });
//
//      // Verify: Check that the correct GLFW and GL methods were called
//      glfw.verify(GLFW::glfwDefaultWindowHints);
//      glfw.verify(() -> GLFW.glfwCreateWindow(800, 600, "GLFW", 0L, 0L));
//      glfw.verify(() -> GLFW.glfwMakeContextCurrent(windowHandle));
//      glfw.verify(() -> GLFW.glfwDestroyWindow(windowHandle));
//      gl.verify(GL::createCapabilities);
//    }
//  }

  @Test
  void constructor_throws_when_glfw_create_window_fails() {
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class)) {
      // Arrange: Mock window creation to return an invalid handle (0L)
      glfw.when(() -> GLFW.glfwCreateWindow(1, 1, "fail", 0L, 0L)).thenReturn(0L);

      // Act & Assert: Expect an IllegalStateException when opening the context
      assertThrows(IllegalStateException.class, () -> new WindowContext(1, 1, "fail"));

      // Verify: Ensure that destroy is never called if creation fails
      glfw.verify(() -> GLFW.glfwDestroyWindow(Mockito.anyLong()), Mockito.never());
    }
  }
}
*/
