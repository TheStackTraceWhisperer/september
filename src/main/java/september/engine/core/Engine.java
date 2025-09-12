package september.engine.core;

import september.engine.assets.ResourceManager;
import september.engine.core.input.GlfwInputService;
import september.engine.core.input.InputService;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.rendering.gl.OpenGLRenderer;
import september.engine.systems.RenderSystem;

public final class Engine implements Runnable {
    private final IWorld world;
    private final TimeService timeService;
    private final ResourceManager resourceManager;
    private final Camera camera;
    private final InputService inputService;
    private final ISystem[] systemsToRegister;
    private final MainLoopPolicy loopPolicy;

    // Fields to hold the context-dependent resources
    private GlfwContext glfwContext;
    private WindowContext window;
    private Renderer renderer;

    public Engine(IWorld world, TimeService timeService, ResourceManager resourceManager, Camera camera,
                  InputService inputService, MainLoopPolicy loopPolicy, ISystem... systemsToRegister) {
        this.world = world;
        this.timeService = timeService;
        this.resourceManager = resourceManager;
        this.camera = camera;
        this.inputService = inputService;
        this.loopPolicy = loopPolicy;
        this.systemsToRegister = systemsToRegister;
    }

    public void init() {
        try {
            glfwContext = new GlfwContext();
            window = new WindowContext(800, 600, "September Engine");

            // --- SET UP CALLBACKS ---
            window.setResizeListener(camera::resize);
            if (inputService instanceof GlfwInputService) {
                ((GlfwInputService) inputService).installCallbacks(window);
            }

            // --- INITIALIZE RENDERER AND GPU ASSETS *AFTER* CONTEXT CREATION ---
            renderer = new OpenGLRenderer();
            loadGpuAssets();

            // --- REGISTER SYSTEMS ---
            if (systemsToRegister != null) {
                for (ISystem system : systemsToRegister) {
                    world.registerSystem(system);
                }
            }
            world.registerSystem(new RenderSystem(world, renderer, resourceManager, camera));
        } catch (Exception e) {
            // If init fails, we should clean up what might have been created.
            shutdown();
            throw new RuntimeException("Engine initialization failed", e);
        }
    }

    private void mainLoop() {
        int frames = 0;
        while (loopPolicy.continueRunning(frames, window.handle())) {
            window.pollEvents();
            timeService.update();
            float dt = timeService.getDeltaTime();
            world.update(dt);
            window.swapBuffers();
            frames++;
        }
    }

    public void shutdown() {
        // The resources must be closed in the reverse order of their creation.
        if (window != null) {
            window.close();
        }
        if (glfwContext != null) {
            glfwContext.close();
        }
        // The resource manager is passed in, but we can close it here if the engine is responsible for it.
        // The original try-with-resources did this, so we will replicate that behavior.
        if (resourceManager != null) {
            resourceManager.close();
        }
    }

    @Override
    public void run() {
        try {
            init();
            mainLoop();
        } finally {
            shutdown();
        }
    }

    private void loadGpuAssets() {
        float[] vertices = {
                0.5f, 0.5f, 0.0f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.0f, 0.0f, 1.0f
        };
        int[] indices = {0, 1, 3, 1, 2, 3};
        resourceManager.loadProceduralMesh("quad", vertices, indices);
        resourceManager.loadTexture("player_texture", "textures/player.png");
        resourceManager.loadTexture("enemy_texture", "textures/enemy.png");
    }

    // Getters for tests
    public IWorld getWorld() {
        return world;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public WindowContext getWindow() {
        return window;
    }
}
