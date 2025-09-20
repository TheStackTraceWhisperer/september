package september.engine.events;

/**
 * A functional interface for listeners that can handle a specific type of event.
 *
 * @param <T> The type of event this listener handles.
 */
@FunctionalInterface
public interface EventListener<T extends Event> {
  /**
   * Handles the dispatched event.
   *
   * @param event The event object containing relevant data.
   */
  void handle(T event);
}
