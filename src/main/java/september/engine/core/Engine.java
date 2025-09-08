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

/**
 * The runtime core of the application.
 * Manages the main loop, orchestrates service lifecycles, and drives the ECS updates.
 */
public final class Engine implements Runnable {
  private final IWorld world;
  private final TimeService timeService;
  private final ResourceManager resourceManager;
  private final Camera camera;
  private final InputService inputService;
  private final ISystem[] systemsToRegister;
  private final MainLoopPolicy loopPolicy;

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

  @Override
  public void run() {
    // The try-with-resources block safely manages the lifecycles of native resources.
    try (
      GlfwContext ignored = new GlfwContext();
      WindowContext window = new WindowContext(800, 600, "September Engine");
      ResourceManager resources = this.resourceManager // Ensures resources.close() is called
    ) {
      // --- SET UP CALLBACKS ---
      window.setResizeListener(camera::resize);

      if (inputService instanceof GlfwInputService) {
        ((GlfwInputService) inputService).installCallbacks(window);
      }

      // --- INITIALIZE RENDERER AND GPU ASSETS *AFTER* CONTEXT CREATION ---
      Renderer renderer = new OpenGLRenderer();
      loadGpuAssets(); // Load meshes and textures that require an active GL context.

      // --- REGISTER SYSTEMS ---
      if (systemsToRegister != null) {
        for (ISystem system : systemsToRegister) {
          world.registerSystem(system);
        }
      }
      // The RenderSystem is a core engine system that depends on the locally created renderer.
      world.registerSystem(new RenderSystem(world, renderer, resources, camera));

      // --- MAIN LOOP ---
      int frames = 0;
      while (loopPolicy.continueRunning(frames, window.handle())) {
        window.pollEvents();

        timeService.update();
        float dt = timeService.getDeltaTime();

        world.update(dt); // Update all registered systems

        window.swapBuffers();
        frames++;
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Loads all assets that require an active OpenGL context.
   * This includes procedural meshes and textures from files.
   */
  private void loadGpuAssets() {
    // Create the standard "quad" mesh for rendering sprites
    float[] vertices = {
      // Position           // UV Coords
      0.5f, 0.5f, 0.0f, 1.0f, 1.0f, // Top Right
      0.5f, -0.5f, 0.0f, 1.0f, 0.0f, // Bottom Right
      -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Bottom Left
      -0.5f, 0.5f, 0.0f, 0.0f, 1.0f  // Top Left
    };
    int[] indices = {0, 1, 3, 1, 2, 3};
    resourceManager.loadProceduralMesh("quad", vertices, indices);

    // Load the game's texture assets
    resourceManager.loadTexture("player_texture", "textures/player.png");
  }
}
