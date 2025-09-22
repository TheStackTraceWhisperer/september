package september.engine.core;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.experimental.Accessors;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.Component;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.events.EventPublisher;
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
@Getter
@Accessors(fluent = true)
public class EngineServices {
  
  private final IWorld world;
  private final SystemManager systemManager;
  private final GameStateManager gameStateManager;
  private final ResourceManager resourceManager;
  private final EventPublisher eventPublisher;
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
      EventPublisher eventPublisher,
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
    this.eventPublisher = eventPublisher;
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
}
