package september.engine.ecs;

import java.util.List;

public interface IWorld {

  // --- Entity Management ---
  int createEntity();

  void destroyEntity(int entityId);

  // --- Component Management ---
  <T> void addComponent(int entityId, T component);

  <T> T getComponent(int entityId, Class<T> componentClass);

  void removeComponent(int entityId, Class<?> componentClass);

  boolean hasComponent(int entityId, Class<?> componentClass);

  // --- Querying ---
  List<Integer> getEntitiesWith(Class<?>... componentClasses);
}
