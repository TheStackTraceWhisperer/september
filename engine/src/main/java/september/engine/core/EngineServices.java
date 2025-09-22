package september.engine.core;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwGamepadService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.Component;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.events.EventBus;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.scene.SceneManager;
import september.engine.state.GameStateManager;

import java.util.Map;

/**
 * Central aggregator for all engine services, managed by the DI container.
 * This class provides access to all engine subsystems and is injected
 * into classes that need access to multiple services.
 */
@Singleton
public class EngineServices {
  
  private final IWorld world;
  private final SystemManager systemManager;
  private final GameStateManager gameStateManager;
  private final ResourceManager resourceManager;
  private final EventBus eventBus;
  private final GlfwInputService inputService;
  private final GamepadService gamepadService;
  private final TimeService timeService;
  private final AudioManager audioManager;
  private final PreferencesService preferencesService;
  private final Camera camera;
  private final Renderer renderer;
  private final WindowContext window;
  
  // SceneManager is created manually due to component registry dependency
  private SceneManager sceneManager;

  @Inject
  public EngineServices(
      IWorld world,
      SystemManager systemManager,
      GameStateManager gameStateManager,
      ResourceManager resourceManager,
      EventBus eventBus,
      GlfwInputService inputService,
      GamepadService gamepadService,
      TimeService timeService,
      AudioManager audioManager,
      PreferencesService preferencesService,
      Camera camera,
      Renderer renderer,
      WindowContext window) {
    this.world = world;
    this.systemManager = systemManager;
    this.gameStateManager = gameStateManager;
    this.resourceManager = resourceManager;
    this.eventBus = eventBus;
    this.inputService = inputService;
    this.gamepadService = gamepadService;
    this.timeService = timeService;
    this.audioManager = audioManager;
    this.preferencesService = preferencesService;
    this.camera = camera;
    this.renderer = renderer;
    this.window = window;
  }

  /**
   * Constructor for test utilities. Creates an EngineServices with provided services.
   * Used for backwards compatibility with tests.
   */
  public EngineServices(
      IWorld world,
      SystemManager systemManager,
      GameStateManager gameStateManager,
      ResourceManager resourceManager,
      SceneManager sceneManager,
      EventBus eventBus,
      GlfwInputService inputService,
      GamepadService gamepadService,
      TimeService timeService,
      AudioManager audioManager,
      PreferencesService preferencesService,
      Camera camera,
      Renderer renderer,
      WindowContext window) {
    this.world = world;
    this.systemManager = systemManager;
    this.gameStateManager = gameStateManager;
    this.resourceManager = resourceManager;
    this.sceneManager = sceneManager;
    this.eventBus = eventBus;
    this.inputService = inputService;
    this.gamepadService = gamepadService;
    this.timeService = timeService;
    this.audioManager = audioManager;
    this.preferencesService = preferencesService;
    this.camera = camera;
    this.renderer = renderer;
    this.window = window;
  }

  /**
   * Sets the scene manager. This must be called after the component registry is available.
   */
  public void setSceneManager(Map<String, Class<? extends Component>> componentRegistry) {
    this.sceneManager = new SceneManager(componentRegistry, resourceManager);
  }

  /**
   * Builder for test purposes. This maintains backwards compatibility with existing tests.
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private IWorld world;
    private SystemManager systemManager;
    private GameStateManager gameStateManager;
    private ResourceManager resourceManager;
    private SceneManager sceneManager;
    private EventBus eventBus;
    private GlfwInputService inputService;
    private GamepadService gamepadService;
    private TimeService timeService;
    private AudioManager audioManager;
    private PreferencesService preferencesService;
    private Camera camera;
    private Renderer renderer;
    private WindowContext window;

    public Builder world(IWorld world) { this.world = world; return this; }
    public Builder systemManager(SystemManager systemManager) { this.systemManager = systemManager; return this; }
    public Builder gameStateManager(GameStateManager gameStateManager) { this.gameStateManager = gameStateManager; return this; }
    public Builder resourceManager(ResourceManager resourceManager) { this.resourceManager = resourceManager; return this; }
    public Builder sceneManager(SceneManager sceneManager) { this.sceneManager = sceneManager; return this; }
    public Builder eventBus(EventBus eventBus) { this.eventBus = eventBus; return this; }
    public Builder inputService(GlfwInputService inputService) { this.inputService = inputService; return this; }
    public Builder gamepadService(GamepadService gamepadService) { this.gamepadService = gamepadService; return this; }
    public Builder timeService(TimeService timeService) { this.timeService = timeService; return this; }
    public Builder audioManager(AudioManager audioManager) { this.audioManager = audioManager; return this; }
    public Builder preferencesService(PreferencesService preferencesService) { this.preferencesService = preferencesService; return this; }
    public Builder camera(Camera camera) { this.camera = camera; return this; }
    public Builder renderer(Renderer renderer) { this.renderer = renderer; return this; }
    public Builder window(WindowContext window) { this.window = window; return this; }

    public EngineServices build() {
      return new EngineServices(world, systemManager, gameStateManager, resourceManager, 
                               sceneManager, eventBus, inputService, gamepadService, 
                               timeService, audioManager, preferencesService, 
                               camera, renderer, window);
    }
  }

  // Getter methods for all services
  public IWorld world() { return world; }
  public SystemManager systemManager() { return systemManager; }
  public GameStateManager gameStateManager() { return gameStateManager; }
  public ResourceManager resourceManager() { return resourceManager; }
  public SceneManager sceneManager() { return sceneManager; }
  public EventBus eventBus() { return eventBus; }
  public GlfwInputService inputService() { return inputService; }
  public GamepadService gamepadService() { return gamepadService; }
  public TimeService timeService() { return timeService; }
  public AudioManager audioManager() { return audioManager; }
  public PreferencesService preferencesService() { return preferencesService; }
  public Camera camera() { return camera; }
  public Renderer renderer() { return renderer; }
  public WindowContext window() { return window; }
}
