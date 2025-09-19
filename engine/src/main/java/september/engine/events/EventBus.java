package september.engine.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple, synchronous event bus for dispatching events to listeners.
 */
public class EventBus {
  private final Map<Class<? extends Event>, List<EventListener>> listeners = new HashMap<>();

  /**
   * Subscribes a listener to a specific type of event.
   *
   * @param eventClass The class of the event to listen for.
   * @param listener   The listener that will handle the event.
   * @param <T>        The type of the event.
   */
  public <T extends Event> void subscribe(Class<T> eventClass, EventListener<T> listener) {
    listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
  }

  /**
   * Unsubscribes a listener from a specific type of event.
   *
   * @param eventClass The class of the event.
   * @param listener   The listener to remove.
   * @param <T>        The type of the event.
   */
  public <T extends Event> void unsubscribe(Class<T> eventClass, EventListener<T> listener) {
    listeners.computeIfPresent(eventClass, (k, v) -> {
      v.remove(listener);
      return v;
    });
  }

  /**
   * Publishes an event, immediately dispatching it to all subscribed listeners.
   *
   * @param event The event to dispatch.
   * @param <T>   The type of the event.
   */
  @SuppressWarnings("unchecked")
  public <T extends Event> void publish(T event) {
    List<EventListener> eventListeners = listeners.get(event.getClass());
    if (eventListeners != null) {
      for (EventListener listener : eventListeners) {
        // The cast is safe due to the structure of our map and subscribe method.
        listener.handle(event);
      }
    }
  }
}
