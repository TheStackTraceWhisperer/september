package september.engine.ecs;

import java.util.HashMap;
import java.util.Map;

/**
 * A registry that maps component names (as strings) to their corresponding Class objects.
 * This allows for decoupled scene loading, where the scene file only needs to know the
 * name of a component, not its fully qualified class path.
 */
public class ComponentRegistry {
  private final Map<String, Class<? extends Component>> registry = new HashMap<>();

  /**
   * Registers a component class with its simple name.
   *
   * @param componentClass The component class to register.
   */
  public void register(Class<? extends Component> componentClass) {
    registry.put(componentClass.getSimpleName(), componentClass);
  }

  /**
   * Registers a component class with a custom name.
   *
   * @param name           The custom name for the component.
   * @param componentClass The component class to register.
   */
  public void register(String name, Class<? extends Component> componentClass) {
    registry.put(name, componentClass);
  }

  /**
   * Retrieves the Class object for a given component name.
   *
   * @param name The name of the component.
   * @return The corresponding Class object.
   * @throws IllegalArgumentException if the component name is not registered.
   */
  public Class<? extends Component> getClassFor(String name) {
    if (!registry.containsKey(name)) {
      throw new IllegalArgumentException("Component not registered: " + name);
    }
    return registry.get(name);
  }
}
