package september.engine.ecs;

/**
 * The contract for any System in the ECS.
 * <p>
 * A System contains the "verb" logic of the application. It operates on a set of
 * entities that possess a specific combination of components.
 */
public interface ISystem {
  /**
   * Executes the system's logic for a single frame.
   *
   * @param deltaTime The time in seconds since the last frame.
   */
  void update(float deltaTime);
}
