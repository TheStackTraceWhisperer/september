package september.engine.events;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enhanced event bus that supports both programmatic subscription and annotation-based
 * event listener discovery. This maintains backward compatibility with the existing
 * EventBus while adding Jakarta pattern support.
 */
@Slf4j
@Singleton
public class EnhancedEventBus extends EventBus {

  /**
   * Annotation to mark methods as event listeners.
   */
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface EventListener {
  }

  private final Map<Class<? extends Event>, List<AnnotatedEventListener>> annotatedListeners = new ConcurrentHashMap<>();

  /**
   * Wraps an annotated method as an event listener.
   */
  private static class AnnotatedEventListener {
    private final Object instance;
    private final Method method;
    private final Class<? extends Event> eventType;

    public AnnotatedEventListener(Object instance, Method method, Class<? extends Event> eventType) {
      this.instance = instance;
      this.method = method;
      this.eventType = eventType;
      method.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public void handle(Event event) {
      try {
        method.invoke(instance, event);
      } catch (Exception e) {
        log.error("Error invoking event listener method {} on {}", method.getName(), instance.getClass().getSimpleName(), e);
      }
    }
  }

  /**
   * Registers all @EventListener annotated methods in the given object.
   *
   * @param listener The object containing @EventListener annotated methods
   */
  public void registerAnnotatedListeners(Object listener) {
    Class<?> listenerClass = listener.getClass();
    
    for (Method method : listenerClass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(EventListener.class)) {
        // Validate method signature
        if (method.getParameterCount() != 1) {
          log.warn("@EventListener method {} in {} must have exactly one parameter", 
                   method.getName(), listenerClass.getSimpleName());
          continue;
        }

        Class<?> parameterType = method.getParameterTypes()[0];
        if (!Event.class.isAssignableFrom(parameterType)) {
          log.warn("@EventListener method {} in {} parameter must be a subtype of Event",
                   method.getName(), listenerClass.getSimpleName());
          continue;
        }

        @SuppressWarnings("unchecked")
        Class<? extends Event> eventType = (Class<? extends Event>) parameterType;
        
        AnnotatedEventListener annotatedListener = new AnnotatedEventListener(listener, method, eventType);
        annotatedListeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(annotatedListener);
        
        log.debug("Registered @EventListener method {} in {} for event type {}", 
                  method.getName(), listenerClass.getSimpleName(), eventType.getSimpleName());
      }
    }
  }

  /**
   * Unregisters all @EventListener annotated methods in the given object.
   *
   * @param listener The object to unregister
   */
  public void unregisterAnnotatedListeners(Object listener) {
    annotatedListeners.values().forEach(listeners -> 
      listeners.removeIf(annotatedListener -> annotatedListener.instance == listener));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Event> void publish(T event) {
    // First call the parent implementation for programmatic listeners
    super.publish(event);
    
    // Then notify annotated listeners
    List<AnnotatedEventListener> eventAnnotatedListeners = annotatedListeners.get(event.getClass());
    if (eventAnnotatedListeners != null) {
      for (AnnotatedEventListener listener : eventAnnotatedListeners) {
        listener.handle(event);
      }
    }
  }
}