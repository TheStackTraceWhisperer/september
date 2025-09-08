package september.engine.ecs;

import java.util.List;

public interface IWorld {

  // --- Entity Management ---

  /**
   * Creates a new, empty entity with a unique ID.
   * @return The unique integer ID of the created entity.
   */
  int createEntity();

  /**
   * Schedules an entity and all its associated components to be destroyed.
   * The actual removal may be deferred to the end of the current frame/update cycle.
   * @param entityId The ID of the entity to destroy.
   */
  void destroyEntity(int entityId);


  // --- Component Management ---

  /**
   * Attaches a component instance to an entity. If the entity already has a
   * component of this type, it will be replaced.
   * @param entityId The entity to which the component will be added.
   * @param component The component data instance.
   * @param <T> The type of the component.
   */
  <T> void addComponent(int entityId, T component);

  /**
   * Retrieves a component of a specific type for a given entity.
   * @param entityId The entity that owns the component.
   * @param componentClass The .class object of the component type to retrieve.
   * @param <T> The type of the component.
   * @return The component instance, or null if the entity does not have that component.
   */
  <T> T getComponent(int entityId, Class<T> componentClass);

  /**
   * Removes a component of a specific type from an entity.
   * @param entityId The entity from which to remove the component.
   * @param componentClass The .class object of the component type to remove.
   */
  void removeComponent(int entityId, Class<?> componentClass);

  /**
   * Checks if an entity possesses a component of a specific type.
   * @param entityId The entity to check.
   * @param componentClass The .class object of the component type.
   * @return true if the entity has the component, false otherwise.
   */
  boolean hasComponent(int entityId, Class<?> componentClass);


  // --- System Management ---

  /**
   * Registers a system with the World. The order of registration is the order
   * in which the systems will be updated each frame.
   * @param system The system instance to register.
   */
  void registerSystem(ISystem system);

  /**
   * Updates all registered systems in the order they were added.
   * This is the main "tick" method for the entire ECS, called once per frame.
   * @param deltaTime The time elapsed since the last frame.
   */
  void update(float deltaTime);


  // --- Querying ---

  /**
   * Retrieves a list of all entity IDs that possess ALL the specified component types.
   * This is the primary method used by systems to find entities to process.
   * @param componentClasses A varargs list of .class objects for the required components.
   * @return A list of entity IDs that match the query.
   */
  List<Integer> getEntitiesWith(Class<?>... componentClasses);

}
