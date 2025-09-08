package september.engine.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.assets.ResourceManager;
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

    @Test
    void initializeOnly_doesNotUpdateWorldOrTime() {
        // Arrange: Mock the construction of all native-dependent classes
        try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> ignoredWindow = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, MainLoopPolicy.initializeOnly());

            // Act
            engine.run();

            // Assert: Verify that the main loop did not run
            verify(time, never()).update();
            verify(world, never()).update(anyFloat());
        }
    }

    @Test
    void fixedFramePolicy_updatesWorldAndTimeSeries() {
        // Arrange
        int frameCount = 3;
        when(time.getDeltaTime()).thenReturn(0.016f);

        // Arrange: Mock the construction of native-dependent classes
        try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> window = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, MainLoopPolicy.frames(frameCount));

            // Act
            engine.run();

            // Assert: Verify the main loop ran the correct number of times
            verify(time, times(frameCount)).update();
            verify(world, times(frameCount)).update(anyFloat());
            // Verify swapBuffers was called each frame on the single window instance
            verify(window.constructed().get(0), times(frameCount)).swapBuffers();
        }
    }

    @Test
    void run_closesAllAutoCloseableResources() {
        // Arrange
        try (MockedConstruction<GlfwContext> glfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> window = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> renderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, MainLoopPolicy.initializeOnly());

            // Act
            engine.run();

            // Assert: Verify close() was called on all resources in the try-with-resources block
            verify(glfw.constructed().get(0)).close();
            verify(window.constructed().get(0)).close();
            verify(resources).close();
        }
    }

    @Test
    void run_catchesAndRethrowsExceptionsAsRuntimeException() {
        // Arrange: Cause an exception to be thrown from within the engine's run loop
        doThrow(new IllegalStateException("Test Exception")).when(time).update();

        try (MockedConstruction<GlfwContext> ignoredGlfw = mockConstruction(GlfwContext.class);
             MockedConstruction<WindowContext> ignoredWindow = mockConstruction(WindowContext.class);
             MockedConstruction<OpenGLRenderer> ignoredRenderer = mockConstruction(OpenGLRenderer.class)) {

            Engine engine = new Engine(world, time, resources, camera, MainLoopPolicy.frames(1));

            // Act & Assert: Expect a RuntimeException to be thrown
            assertThrows(RuntimeException.class, engine::run);
        }
    }
}
