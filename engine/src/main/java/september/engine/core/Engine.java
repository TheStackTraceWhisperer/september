package september.engine.core;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwGamepadService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.events.EventBus;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.rendering.gl.OpenGLRenderer;
import september.engine.scene.SceneManager;
import september.engine.state.GameState;
import september.engine.state.GameStateManager;

public final class Engine implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(Engine.class);
  private static final int INITIAL_WIDTH = 800;
  private static final int INITIAL_HEIGHT = 600;

  private final Game game;
  private final ApplicationLoopPolicy loopPolicy;

  // Getters for tests
  // Services managed by the Engine
  @Getter private IWorld world;
  private TimeService timeService;
  @Getter private ResourceManager resourceManager;
  @Getter private Camera camera;
  private GlfwInputService inputService;
  private GamepadService gamepadService;
  @Getter private AudioManager audioManager;
  private PreferencesService preferencesService;
  private GlfwContext glfwContext;
  @Getter private WindowContext window;
  @Getter private Renderer renderer;
  @Getter private SystemManager systemManager;
  private GameStateManager gameStateManager;
  private EngineServices services;
  private SceneManager sceneManager;
  private EventBus eventBus;

  public Engine(Game game, ApplicationLoopPolicy loopPolicy) {
    this.game = game;
    this.loopPolicy = loopPolicy;
  }

  public void init() {
    try {
      // --- INITIALIZE ALL CORE ENGINE SERVICES ---
      world = new september.engine.ecs.World();
      systemManager = new SystemManager();
      gameStateManager = new GameStateManager();
      eventBus = new EventBus();
      timeService = new SystemTimer();
      resourceManager = new ResourceManager();
      inputService = new GlfwInputService();
      gamepadService = new GlfwGamepadService();
      audioManager = new AudioManager();
      sceneManager = new SceneManager(game.getComponentRegistry(), resourceManager);
      preferencesService = new PreferencesService("september-engine");
      glfwContext = new GlfwContext();
      window = new WindowContext(INITIAL_WIDTH, INITIAL_HEIGHT, "September Engine");
      renderer = new OpenGLRenderer();
      camera = new Camera(INITIAL_WIDTH, INITIAL_HEIGHT);
      camera.setPerspective(45.0f, (float) INITIAL_WIDTH / INITIAL_HEIGHT, 0.1f, 100.0f);

      // --- CREATE THE FINAL SERVICES OBJECT ---
      this.services =
          EngineServices.builder()
              .world(world)
              .systemManager(systemManager)
              .gameStateManager(gameStateManager)
              .resourceManager(resourceManager)
              .sceneManager(sceneManager)
              .eventBus(eventBus)
              .inputService(inputService)
              .gamepadService(gamepadService)
              .timeService(timeService)
              .audioManager(audioManager)
              .preferencesService(preferencesService)
              .camera(camera)
              .renderer(renderer)
              .window(window)
              .build();

      // --- SET UP CALLBACKS ---
      window.setResizeListener(
          (width, height) -> {
            camera.resize(width, height);
            camera.setPerspective(45.0f, (float) width / height, 0.1f, 100.0f);
          });

      inputService.installCallbacks(window);

      audioManager.initialize();

      // --- INITIALIZE THE GAME AND SET THE INITIAL STATE ---
      game.init(this.services);
      GameState initialState = game.getInitialState(services);
      gameStateManager.pushState(initialState, services);

    } catch (Exception e) {
      shutdown();
      throw new RuntimeException("Engine initialization failed", e);
    }
  }

  private void mainLoop() {
    int frames = 0;
    while (loopPolicy.continueRunning(frames, window.handle()) && !gameStateManager.isEmpty()) {
      window.pollEvents();
      timeService.update();
      float dt = timeService.getDeltaTime();
      gameStateManager.update(services, dt);
      systemManager.updateAll(dt);
      window.swapBuffers();
      frames++;
    }
  }

  public void shutdown() {
    // Game shutdown is now handled by the states' onExit methods.
    // We just need to clean up engine resources.

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
}
