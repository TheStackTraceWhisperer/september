package september.engine.core;

import lombok.Builder;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwInputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.events.EventBus;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;
import september.engine.scene.SceneManager;
import september.engine.state.GameStateManager;

@Builder
public record EngineServices(
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
  WindowContext window
) {
}
