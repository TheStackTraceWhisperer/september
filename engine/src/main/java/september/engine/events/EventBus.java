package september.engine.events;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple, synchronous event bus for dispatching events to listeners.
 * This implementation is thread-safe and prevents ConcurrentModificationException
 * by using concurrent collections.
 */
@Singleton
public class EventBus {
  // Use ConcurrentHashMap and CopyOnWriteArrayList to allow for safe concurrent modification.
  private final Map<Class<? extends Event>, List<EventListener>> listeners = new ConcurrentHashMap<>();

  /**
   * Subscribes a listener to a specific type of event.
   *
   * @param eventClass The class of the event to listen for.
   * @param listener   The listener that will handle the event.
   * @param <T>        The type of the event.
   */
  public <T extends Event> void subscribe(Class<T> eventClass, EventListener<T> listener) {
    // computeIfAbsent is atomic for ConcurrentHashMap
    listeners.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>()).add(listener);
  }

  /**
   * Unsubscribes a listener from a specific type of event.
   *
   * @param eventClass The class of the event.
   * @param listener   The listener to remove.
   * @param <T>        The type of the event.
   */
  public <T extends Event> void unsubscribe(Class<T> eventClass, EventListener<T> listener) {
    // computeIfPresent is atomic for ConcurrentHashMap
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
      // Iteration on CopyOnWriteArrayList is safe from ConcurrentModificationException.
      // The iterator holds a snapshot of the list at the time of its creation.
      for (EventListener listener : eventListeners) {
        listener.handle(event);
      }
    }
  }
}
