package september.engine.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.assets.ResourceManager;
import september.engine.core.input.InputService;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.gl.OpenGLRenderer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests for the {@link Engine} class.
 */
@ExtendWith(MockitoExtension.class)
class EngineTest {

    @Mock
    private IWorld world;
    @Mock
    private TimeService time;
    @Mock
    private ResourceManager resources;
    @Mock
    private Camera camera;
    @Mock
    private InputService inputService;

    @Test
    void initializeOnly_doesNotUpdateWorldOrTime() {
        // Arrange
        try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> ignoredWindow = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, inputService, MainLoopPolicy.initializeOnly());

            // Act
            engine.run();

            // Assert
            verify(time, never()).update();
            verify(world, never()).update(anyFloat());
        }
    }

    @Test
    void fixedFramePolicy_updatesWorldAndTimeSeries() {
        // Arrange
        int frameCount = 3;
        when(time.getDeltaTime()).thenReturn(0.016f);

        try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> window = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, inputService, MainLoopPolicy.frames(frameCount));

            // Act
            engine.run();

            // Assert
            verify(time, times(frameCount)).update();
            verify(world, times(frameCount)).update(anyFloat());
            verify(window.constructed().getFirst(), times(frameCount)).swapBuffers();
        }
    }

    @Test
    void run_closesAllAutoCloseableResources() {
        // Arrange
        try (MockedConstruction<GlfwContext> glfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> window = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignored = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, inputService, MainLoopPolicy.initializeOnly());

            // Act
            engine.run();

            // Assert
            verify(glfw.constructed().getFirst()).close();
            verify(window.constructed().getFirst()).close();
            verify(resources).close();
        }
    }

    @Test
    void run_catchesAndRethrowsExceptionsAsRuntimeException() {
        // Arrange: Cause an exception to be thrown from the system registration phase.
        ISystem mockSystem = mock(ISystem.class);
        doThrow(new IllegalStateException("Test Exception")).when(world).registerSystem(any(ISystem.class));

        try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> ignoredWindow = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, inputService, MainLoopPolicy.frames(1), mockSystem);

            // Act & Assert: Expect a RuntimeException to be thrown
            assertThrows(RuntimeException.class, engine::run);
        }
    }
}
