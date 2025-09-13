package september.engine.core;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GlfwGamepadService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.input.InputService;
import september.engine.core.input.GamepadService;
import september.engine.core.preferences.PreferencesService;
import september.engine.core.preferences.PreferencesServiceImpl;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.rendering.gl.OpenGLRenderer;
import september.engine.systems.RenderSystem;

@Slf4j
public final class Engine implements Runnable {
  private final Game game;
  private final MainLoopPolicy loopPolicy;

  // Services managed by the Engine
  private IWorld world;
  private TimeService timeService;
  private ResourceManager resourceManager;
  private Camera camera;
  private InputService inputService;
  private GamepadService gamepadService;
  private AudioManager audioManager;
  private PreferencesService preferencesService;
  private GlfwContext glfwContext;
  private WindowContext window;
  private Renderer renderer;

  public Engine(Game game, MainLoopPolicy loopPolicy) {
    this.game = game;
    this.loopPolicy = loopPolicy;
  }

  public void init() {
    try {
      // --- INITIALIZE ALL CORE ENGINE SERVICES ---
      world = new september.engine.ecs.World();
      timeService = new SystemTimer();
      resourceManager = new ResourceManager();
      inputService = new GlfwInputService();
      gamepadService = new GlfwGamepadService();
      audioManager = new AudioManager();
      preferencesService = new PreferencesServiceImpl("september-engine");
      glfwContext = new GlfwContext();
      window = new WindowContext(800, 600, "September Engine");
      renderer = new OpenGLRenderer();

      // Camera setup can be part of the engine's default initialization
      camera = new Camera(800.0f, 600.0f);
      camera.setPerspective(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);


      // --- SET UP CALLBACKS ---
      window.setResizeListener(camera::resize);
      if (inputService instanceof GlfwInputService) {
        ((GlfwInputService) inputService).installCallbacks(window);
      }
      audioManager.initialize();

      // --- CREATE THE SERVICES OBJECT ---
      var services = new EngineServices(world, resourceManager, inputService, gamepadService,
        timeService, audioManager, preferencesService, camera, renderer, window);

      // --- INITIALIZE THE GAME LOGIC ---
      game.init(services);

      // --- REGISTER ALL SYSTEMS ---
      // Register engine-provided systems first
      world.registerSystem(new RenderSystem(world, renderer, resourceManager, camera));
      // Register all game-provided systems
      for (ISystem system : game.getSystems()) {
        world.registerSystem(system);
      }

    } catch (Exception e) {
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
    game.shutdown();

    if (audioManager != null) {
      audioManager.close();
    }
    if (window != null) {
      window.close();
    }
    if (glfwContext != null) {
      glfwContext.close();
    }
    if (resourceManager != null) {
      resourceManager.close();
    }
    if (preferencesService != null) {
      try {
        preferencesService.close();
      } catch (Exception e) {
        log.error("Error closing preferences service", e);
      }
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

  // Getters for tests
  public IWorld getWorld() { return world; }
  public Renderer getRenderer() { return renderer; }
  public WindowContext getWindow() { return window; }
  public PreferencesService getPreferencesService() { return preferencesService; }
  public ResourceManager getResourceManager() { return resourceManager; }
  public Camera getCamera() { return camera; }
  public AudioManager getAudioManager() { return audioManager; }
}
