package september.engine.ecs;

/**
 * The contract for any System in the ECS.
 * <p>
 * A System contains the "verb" logic of the application. It operates on a set of
 * entities that possess a specific combination of components.
 */
public interface ISystem {

  class Priority {
    public static final int INPUT = 100;
    public static final int LOGIC = 200;
    public static final int PHYSICS = 300;
    public static final int UI_LOGIC = 900;
    public static final int RENDER = 1000;
    public static final int UI_RENDER = 1100;
  }

  /**
   * Executes the system's logic for a single frame.
   *
   * @param deltaTime The time in seconds since the last frame.
   */
  void update(float deltaTime);

  default int getPriority() {
    return Priority.LOGIC;
  }
}
