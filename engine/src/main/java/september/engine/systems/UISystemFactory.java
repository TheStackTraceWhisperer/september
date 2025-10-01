package september.engine.systems;

import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import september.engine.core.WindowContext;
import september.engine.core.input.GlfwInputService;
import september.engine.ecs.IWorld;
import september.engine.events.UIButtonClickedEvent;

/**
 * Factory for creating UI-related systems with proper dependency injection.
 */
@Singleton
public class UISystemFactory {

  private final WindowContext window;
  private final GlfwInputService inputService;
  private final ApplicationEventPublisher<UIButtonClickedEvent> buttonClickedEvent;

  @Inject
  public UISystemFactory(
      WindowContext window,
      GlfwInputService inputService,
      ApplicationEventPublisher<UIButtonClickedEvent> buttonClickedEvent) {
    this.window = window;
    this.inputService = inputService;
    this.buttonClickedEvent = buttonClickedEvent;
  }

  /**
   * Creates a UISystem for the given world.
   *
   * @param world The world containing UI entities
   * @return A configured UISystem
   */
  public UISystem createUISystem(IWorld world) {
    return new UISystem(world, window, inputService, buttonClickedEvent);
  }
}
