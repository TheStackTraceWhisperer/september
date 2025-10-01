package september.game.systems;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import september.engine.core.TimeService;
import september.engine.ecs.IWorld;
import september.game.input.InputMappingService;

/**
 * Factory for creating game-specific systems with proper dependency injection.
 */
@Singleton
public class GameSystemFactory {

  private final InputMappingService inputMappingService;
  private final TimeService timeService;

  @Inject
  public GameSystemFactory(
      InputMappingService inputMappingService,
      TimeService timeService) {
    this.inputMappingService = inputMappingService;
    this.timeService = timeService;
  }

  /**
   * Creates a PlayerInputSystem for the given world.
   *
   * @param world The world containing player entities
   * @return A configured PlayerInputSystem
   */
  public PlayerInputSystem createPlayerInputSystem(IWorld world) {
    return new PlayerInputSystem(world, inputMappingService);
  }

  /**
   * Creates an EnemyAISystem for the given world.
   *
   * @param world The world containing enemy entities
   * @return A configured EnemyAISystem
   */
  public EnemyAISystem createEnemyAISystem(IWorld world) {
    return new EnemyAISystem(world, timeService);
  }

  /**
   * Creates a CollisionSystem for the given world.
   *
   * @param world The world containing entities with colliders
   * @return A configured CollisionSystem
   */
  public CollisionSystem createCollisionSystem(IWorld world) {
    return new CollisionSystem(world);
  }
}
