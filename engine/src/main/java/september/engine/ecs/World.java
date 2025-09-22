package september.engine.ecs;

import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A concrete implementation of the IWorld interface.
 * <p>
 * This class orchestrates the various managers (Entity, Component, System)
 * to provide a coherent ECS framework.
 */
@Singleton
public class World implements IWorld {

  private final EntityManager entityManager;
  private final ComponentManager componentManager;
  private final SystemManager systemManager;

  public World() {
    this.entityManager = new EntityManager();
    this.componentManager = new ComponentManager();
    this.systemManager = new SystemManager();
  }

  @Override
  public int createEntity() {
    return entityManager.createEntity();
  }

  @Override
  public void destroyEntity(int entityId) {
    // Notify all managers of the entity's destruction.
    componentManager.entityDestroyed(entityId);
    entityManager.destroyEntity(entityId);
  }

  @Override
  public <T> void addComponent(int entityId, T component) {
    componentManager.addComponent(entityId, component);
  }

  @Override
  public <T> T getComponent(int entityId, Class<T> componentClass) {
    return componentManager.getComponent(entityId, componentClass);
  }

  @Override
  public void removeComponent(int entityId, Class<?> componentClass) {
    componentManager.removeComponent(entityId, componentClass);
  }

  @Override
  public boolean hasComponent(int entityId, Class<?> componentClass) {
    return componentManager.hasComponent(entityId, componentClass);
  }

  @Override
  public List<Integer> getEntitiesWith(Class<?>... componentClasses) {
    // Start with a list of all active entities.
    List<Integer> activeEntities = entityManager.getActiveEntities();

    if (componentClasses == null || componentClasses.length == 0) {
      return activeEntities;
    }

    // Filter the list down to only those that have all the required components.
    return activeEntities.stream()
      .filter(entityId -> {
        for (Class<?> componentClass : componentClasses) {
          if (!componentManager.hasComponent(entityId, componentClass)) {
            return false; // This entity doesn't have one of the required components.
          }
        }
        return true; // This entity has all required components.
      })
      .collect(Collectors.toList());
  }
}

class EntityManager {
  private int nextEntityId = 0;
  private final Set<Integer> activeEntities = new HashSet<>();

  int createEntity() {
    int id = nextEntityId++;
    activeEntities.add(id);
    return id;
  }

  void destroyEntity(int entityId) {
    activeEntities.remove(entityId);
  }

  List<Integer> getActiveEntities() {
    return new ArrayList<>(activeEntities);
  }
}

class ComponentManager {
  private final Map<Class<?>, Map<Integer, Object>> componentStores = new HashMap<>();

  <T> void addComponent(int entityId, T component) {
    Class<?> componentClass = component.getClass();
    componentStores
      .computeIfAbsent(componentClass, k -> new HashMap<>())
      .put(entityId, component);
  }

  <T> T getComponent(int entityId, Class<T> componentClass) {
    Map<Integer, Object> store = componentStores.get(componentClass);
    if (store == null) {
      return null;
    }
    return componentClass.cast(store.get(entityId));
  }

  void removeComponent(int entityId, Class<?> componentClass) {
    Map<Integer, Object> store = componentStores.get(componentClass);
    if (store != null) {
      store.remove(entityId);
    }
  }

  boolean hasComponent(int entityId, Class<?> componentClass) {
    Map<Integer, Object> store = componentStores.get(componentClass);
    return store != null && store.containsKey(entityId);
  }

  void entityDestroyed(int entityId) {
    for (Map<Integer, Object> store : componentStores.values()) {
      store.remove(entityId);
    }
  }
}
