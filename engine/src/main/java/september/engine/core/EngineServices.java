package september.engine.core;

import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GamepadService;
import september.engine.core.input.InputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.IWorld;
import september.engine.ecs.SystemManager;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;

public record EngineServices(
  IWorld world,
  SystemManager systemManager,
  ResourceManager resourceManager,
  InputService inputService,
  GamepadService gamepadService,
  TimeService timeService,
  AudioManager audioManager,
  PreferencesService preferencesService,
  Camera camera,
  Renderer renderer,
  WindowContext window
) {}
