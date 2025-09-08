package september.engine.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import september.engine.assets.ResourceManager;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.gl.OpenGLRenderer;

import static org.mockito.Mockito.*;

/**
 * Engine tests focusing ONLY on its injected dependencies (no GLFW/OpenGL mocking).
 * These tests are now pure unit tests and do not require a display.
 */
class EngineTest {

  private IWorld world;
  private TimeService time;
  private ResourceManager resources;
  private Camera camera;

  @BeforeEach
  void setUp() {
    world = mock(IWorld.class);
    time = mock(TimeService.class);
    resources = mock(ResourceManager.class);
    camera = mock(Camera.class);
  }

  @Test
  void initialize_only_policy_performs_no_world_or_time_updates() {
    // Arrange: Mock the construction of all native-dependent classes to prevent native calls
    try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
         MockedConstruction<WindowContext> ignoredWindow = mockConstruction(WindowContext.class);
         MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

      Engine engine = new Engine(world, time, resources, camera, MainLoopPolicy.frames(0));

      // Act
      engine.run();

      // Assert
      verify(world, never()).update(anyFloat());
      verify(time, never()).update();
    }
  }

  @Test
  void fixed_frame_policy_invokes_world_and_time_expected_times() {
    // Arrange
    int frames = 2;
    when(time.getDeltaTime()).thenReturn(0.016f); // nominal frame time

    // Mock the construction of all native-dependent classes to prevent native calls
    try (MockedStatic<GLFW> glfw = Mockito.mockStatic(GLFW.class);
         MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
         MockedConstruction<WindowContext> ignoredWindow = mockConstruction(WindowContext.class, (mock, context) -> {
             when(mock.handle()).thenReturn(12345L);
         });
         MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

      Engine engine = new Engine(world, time, resources, camera, MainLoopPolicy.frames(frames));

      // Act
      engine.run();

      // Assert
      verify(time, times(frames)).update();
      verify(world, times(frames)).update(anyFloat());
    }
  }
}
