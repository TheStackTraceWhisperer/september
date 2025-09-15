package september.engine.ecs;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the registration and execution of all systems.
 * Package-private as it's an implementation detail of the World.
 */
public class SystemManager {
  private final List<ISystem> systems = new ArrayList<>();

  public void register(ISystem system) {
    systems.add(system);
  }

  public void updateAll(float deltaTime) {
    for (ISystem system : systems) {
      system.update(deltaTime);
    }
  }

  public void clear() {
    systems.clear();
  }
}
