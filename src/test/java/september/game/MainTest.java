package september.game;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import september.engine.core.GlfwContext;
import september.engine.core.MainLoopPolicy;
import september.engine.core.WindowContext;
import september.engine.rendering.gl.OpenGLRenderer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Unit tests for the Main class startup and shutdown path.
 * All native dependencies (LWJGL) are mocked to ensure this is a pure, platform-agnostic unit test.
 */
class MainTest {

  @Test
  void run_with_frames_zero_policy_initializes_and_cleans_up_without_looping() {
    assertDoesNotThrow(() -> {
      // Arrange: Mock all static LWJGL classes and context construction.
      long windowHandle = 12345L;
      try (
        MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class);
        MockedStatic<GL> gl = Mockito.mockStatic(GL.class);
        MockedStatic<GL11> gl11 = Mockito.mockStatic(GL11.class);
        MockedStatic<GL20> gl20 = Mockito.mockStatic(GL20.class);
        MockedStatic<GL30> gl30 = Mockito.mockStatic(GL30.class);
        MockedConstruction<WindowContext> winCtx = Mockito.mockConstruction(WindowContext.class, (mock, context) -> {
          Mockito.when(mock.handle()).thenReturn(windowHandle);
        });
        MockedConstruction<GlfwContext> glfwCtx = Mockito.mockConstruction(GlfwContext.class);
        MockedConstruction<OpenGLRenderer> rendererCtx = Mockito.mockConstruction(OpenGLRenderer.class)) {

        // Mock GL capabilities and version strings
        gl.when(GL::createCapabilities).thenReturn(null);
        gl11.when(() -> GL11.glGetString(anyInt())).thenReturn("mock-opengl-version");

        // Mock main loop condition to immediately terminate
        glfw.when(() -> GLFW.glfwWindowShouldClose(windowHandle)).thenReturn(true);

        // Act: Run the main class with a policy that only initializes and then stops.
        new Main(MainLoopPolicy.frames(0)).run();

        // Assert: Verify that the main lifecycle methods were called.
        // 1. Contexts were created and closed
        Mockito.verify(glfwCtx.constructed().get(0)).close();
        Mockito.verify(winCtx.constructed().get(0)).close();

        // 2. An OpenGLRenderer was constructed. The mock construction itself ensures this.
        // We can assert that at least one was constructed.
        assertFalse(rendererCtx.constructed().isEmpty());

        // NOTE: We do NOT verify VAO/VBO creation (e.g., glGenVertexArrays) because with frames(0),
        // the main loop never runs, so the RenderSystem never gets to update and create meshes.
      }
    });
  }
}
