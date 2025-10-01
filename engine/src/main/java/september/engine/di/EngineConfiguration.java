package september.engine.di;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.event.ApplicationEventPublisher;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.EngineServices;
import september.engine.core.GlfwContext;
import september.engine.core.TimeService;
import september.engine.core.WindowContext;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.events.UIButtonClickedEvent;
import september.engine.rendering.Camera;
import september.engine.rendering.gl.OpenGLRenderer;
import september.engine.scene.SceneManager;
import september.engine.state.GameStateManager;

/**
 * DI configuration factory for the September Engine.
 * Handles complex initialization logic that can't be handled by simple @Singleton annotations.
 */
@Factory
public class EngineConfiguration {

  private static final int INITIAL_WIDTH = 800;
  private static final int INITIAL_HEIGHT = 600;

  @Bean
  public GlfwContext glfwContext() {
    return new GlfwContext();
  }

  @Bean
  public WindowContext windowContext(GlfwContext glfwContext) {
    // GlfwContext dependency ensures GLFW is initialized before creating window
    return new WindowContext(INITIAL_WIDTH, INITIAL_HEIGHT, "September Engine");
  }

  @Bean
  public Camera camera() {
    Camera camera = new Camera(INITIAL_WIDTH, INITIAL_HEIGHT);
    camera.setPerspective(45.0f, (float) INITIAL_WIDTH / INITIAL_HEIGHT, 0.1f, 100.0f);
    return camera;
  }

  @Bean
  public OpenGLRenderer renderer() {
    return new OpenGLRenderer();
  }

  @Bean
  public PreferencesService preferencesService() {
    return new PreferencesService("september-engine");
  }

  @Bean
  public EngineServices engineServices(
      IWorld world,
      SystemManager systemManager,
      GameStateManager gameStateManager,
      ResourceManager resourceManager,
      SceneManager sceneManager,
      GlfwInputService inputService,
      GamepadService gamepadService,
      TimeService timeService,
      AudioManager audioManager,
      PreferencesService preferencesService,
      Camera camera,
      OpenGLRenderer renderer,
      WindowContext window,
      ApplicationEventPublisher<UIButtonClickedEvent> buttonClickedEvent) {
    return EngineServices.builder()
        .world(world)
        .systemManager(systemManager)
        .gameStateManager(gameStateManager)
        .resourceManager(resourceManager)
        .sceneManager(sceneManager)
        .inputService(inputService)
        .gamepadService(gamepadService)
        .timeService(timeService)
        .audioManager(audioManager)
        .preferencesService(preferencesService)
        .camera(camera)
        .renderer(renderer)
        .window(window)
        .buttonClickedEvent(buttonClickedEvent)
        .build();
  }
}