package september.engine.core;

import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
import september.engine.core.input.GamepadService;
import september.engine.core.input.InputService;
import september.engine.core.preferences.PreferencesService;
import september.engine.ecs.IWorld;
import september.engine.rendering.Camera;
import september.engine.rendering.Renderer;

/**
 * A service locator record that provides access to all core engine services.
 * This object is passed to the Game implementation to allow it to interact
 * with the engine in a decoupled way.
 */
public record EngineServices(
  IWorld world,
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
