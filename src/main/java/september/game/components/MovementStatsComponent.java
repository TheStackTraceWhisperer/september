package september.game.components;

import september.engine.ecs.Component;

/**
 * A component that stores movement-related stats for an entity.
 */
public class MovementStatsComponent implements Component {
  /**
   * The movement speed of the entity, in units per second.
   */
  public float speed = 1;

  public MovementStatsComponent(float speed) {
    this.speed = speed;
  }
}
