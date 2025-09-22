package september.engine.events;

import jakarta.inject.Singleton;
import io.avaje.inject.events.Event;

/**
 * Simple event publisher using avaje-inject's event system.
 * This replaces the custom EventBus with avaje-inject native events.
 */
@Singleton
public class EventPublisher {

  private final Event<UIButtonClickedEvent> buttonClickedEvent;

  public EventPublisher(Event<UIButtonClickedEvent> buttonClickedEvent) {
    this.buttonClickedEvent = buttonClickedEvent;
  }

  public void publishButtonClicked(UIButtonClickedEvent event) {
    buttonClickedEvent.fire(event);
  }
}