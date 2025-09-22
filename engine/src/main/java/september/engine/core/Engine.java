package september.engine.core;

import io.avaje.inject.BeanScope;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GlfwInputService;
import september.engine.di.EngineConfiguration;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.state.GameState;
import september.engine.state.GameStateManager;

public final class Engine implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(Engine.class);

  private final Game game;
  private final ApplicationLoopPolicy loopPolicy;
  private BeanScope beanScope;
  private EngineServices services;

  // Getters for tests - delegate to the injected services
  @Getter private IWorld world;
  @Getter private ResourceManager resourceManager;
  @Getter private Camera camera;
  @Getter private AudioManager audioManager;
  @Getter private WindowContext window;
  @Getter private Renderer renderer;
  @Getter private SystemManager systemManager;

  public Engine(Game game, ApplicationLoopPolicy loopPolicy) {
    this.game = game;
    this.loopPolicy = loopPolicy;
  }

  public void init() {
    try {
      log.info("Initializing September Engine with DI container");
      
      // Create the DI container and get all services
      beanScope = BeanScope.builder().build();

      // Get the main services aggregator
      services = beanScope.get(EngineServices.class);
      
      // Initialize the scene manager with the game's component registry
      services.sceneManager().initialize(game.getComponentRegistry());
      
      // Set up test getters by delegating to the services
      world = services.world();
      resourceManager = services.resourceManager();
      camera = services.camera();
      audioManager = services.audioManager();
      window = services.window();
      renderer = services.renderer();
      systemManager = services.systemManager();

      // --- SET UP CALLBACKS ---
      window.setResizeListener(
          (width, height) -> {
            camera.resize(width, height);
            camera.setPerspective(45.0f, (float) width / height, 0.1f, 100.0f);
          });

      services.inputService().installCallbacks(window);

      audioManager.initialize();

      // --- INITIALIZE THE GAME AND SET THE INITIAL STATE ---
      game.init(services);
      GameState initialState = game.getInitialState(services);
      services.gameStateManager().pushState(initialState, services);

      log.info("September Engine initialized successfully");

    } catch (Exception e) {
      shutdown();
      throw new RuntimeException("Engine initialization failed", e);
    }
  }

  private void mainLoop() {
    int frames = 0;
    while (loopPolicy.continueRunning(frames, window.handle()) && !services.gameStateManager().isEmpty()) {
      window.pollEvents();
      services.timeService().update();
      float dt = services.timeService().getDeltaTime();
      services.gameStateManager().update(services, dt);
      services.systemManager().updateAll(dt);
      window.swapBuffers();
      frames++;
    }
  }

  public void shutdown() {
    log.info("Shutting down September Engine");
    
    // Game shutdown is now handled by the states' onExit methods.
    // We just need to clean up engine resources.
    
    if (services != null) {
      try {
        if (services.audioManager() != null) {
          services.audioManager().close();
        }
        if (services.window() != null) {
          services.window().close();
        }
        if (services.resourceManager() != null) {
          services.resourceManager().close();
        }
        if (services.preferencesService() != null) {
          services.preferencesService().close();
        }
      } catch (Exception e) {
        log.error("Error during engine shutdown", e);
      }
    }

    // Close the DI container
    if (beanScope != null) {
      try {
        beanScope.close();
      } catch (Exception e) {
        log.error("Error closing DI container", e);
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
