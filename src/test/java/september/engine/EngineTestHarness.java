package september.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import september.engine.assets.ResourceManager;
import september.engine.core.Engine;
import september.engine.core.MainLoopPolicy;
import september.engine.core.SystemTimer;
import september.engine.core.TimeService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.input.InputService;
import september.engine.ecs.IWorld;
import september.engine.ecs.World;
import september.engine.rendering.Camera;

/**
 * A JUnit 5 test harness that bootstraps a live game engine with a valid OpenGL context before each test.
 * <p>
 * Tests can extend this class to safely work with OpenGL-dependent components like Shaders, Textures,
 * or any system that requires a live graphics context, without needing to run the full game loop.
 * It provides direct access to the core engine components like the {@link IWorld} and {@link ResourceManager}.
 */
public abstract class EngineTestHarness {

    protected Engine engine;
    protected IWorld world;
    protected ResourceManager resourceManager;
    protected Camera camera;

    @BeforeEach
    void setUp() {
        // 1. Instantiate all the real, live services needed to construct the engine.
        world = new World();
        TimeService timeService = new SystemTimer();
        resourceManager = new ResourceManager();
        camera = new Camera(800.0f, 600.0f);
        InputService inputService = new GlfwInputService();

        // 2. Create the Engine instance. We use MainLoopPolicy.never() because we don't want
        // the main loop to run. We will control updates manually in our tests if needed.
        // No systems are registered by default; tests are responsible for adding the systems they need.
        engine = new Engine(
                world,
                timeService,
                resourceManager,
                camera,
                inputService,
                MainLoopPolicy.skip()
        );

        // 3. Initialize the engine. This creates the window and the OpenGL context.
        // After this call, it is safe to perform OpenGL operations.
        engine.init();
    }

    @AfterEach
    void tearDown() {
        // 4. Shut down the engine to release the window and OpenGL context.
        if (engine != null) {
            engine.shutdown();
        }
    }
}
