package september.engine.core;

import io.avaje.inject.events.Event;
import lombok.Builder;
import september.engine.assets.ResourceManager;
import september.engine.audio.AudioManager;
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
 * Central aggregator for all engine services, managed by the DI container.
 * This record provides access to all engine subsystems and is injected
 * into classes that need access to multiple services.
 */
@Builder
public record EngineServices(
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
    Event<UIButtonClickedEvent> buttonClickedEvent
) {}
