package september.engine.events;

import io.avaje.inject.events.Event;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Simple event publisher using avaje-inject's native event system.
 * This replaces the custom EventBus with avaje-inject native events.
 */
@Singleton
@RequiredArgsConstructor
public class EventPublisher {

  private final Event<UIButtonClickedEvent> buttonClickedEvent;

  public void publishButtonClicked(UIButtonClickedEvent event) {
    buttonClickedEvent.fire(event);
  }
}