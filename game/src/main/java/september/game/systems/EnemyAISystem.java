package september.game.systems;

import september.engine.core.TimeService;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.TransformComponent;
import september.game.components.EnemyComponent;

/**
 * A simple AI system that makes enemies move back and forth horizontally.
 */
public class EnemyAISystem implements ISystem {
  private final IWorld world;
  private final TimeService timeService;
  private final float travelDistance = 3.0f; // How far from the center they patrol

  public EnemyAISystem(IWorld world, TimeService timeService) {
    this.world = world;
    this.timeService = timeService;
  }

  @Override
  public void update(float deltaTime) {
    var entities = world.getEntitiesWith(EnemyComponent.class, TransformComponent.class);

    for (int entityId : entities) {
      TransformComponent transform = world.getComponent(entityId, TransformComponent.class);

      // This is a simple sine wave patrol. The enemy's X position will oscillate
      // between -travelDistance and +travelDistance based on the total game time.
      // This creates a smooth back-and-forth movement.
      float horizontalPosition = (float) Math.sin(timeService.getTotalTime()) * travelDistance;

      // We must use the vector's methods to modify it, not direct field access.
      transform.position.set(horizontalPosition, transform.position.y(), transform.position.z());
    }
  }
}
