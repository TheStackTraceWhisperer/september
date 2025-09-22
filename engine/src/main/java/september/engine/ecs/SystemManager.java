package september.engine.ecs;

import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the registration and execution of all systems.
 */
@Singleton
public class SystemManager {
  private final List<ISystem> systems = new ArrayList<>();
  private boolean sorted = true;

  public void register(ISystem system) {
    systems.add(system);
    sorted = false;
  }

  public void updateAll(float deltaTime) {
    if (!sorted) {
      systems.sort(Comparator.comparingInt(ISystem::getPriority));
      sorted = true;
    }

    // By creating a copy for iteration, we prevent ConcurrentModificationException
    // if a system's update method causes a state change that clears the system manager.
    for (ISystem system : new ArrayList<>(systems)) {
      system.update(deltaTime);
    }
  }

  public void clear() {
    systems.clear();
  }
}
