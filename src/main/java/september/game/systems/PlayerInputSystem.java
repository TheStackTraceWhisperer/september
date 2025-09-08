package september.game.systems;

import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.ControllableComponent;
import september.game.input.GameAction;
import september.game.input.InputMappingService;

/**
 * This system reads from the InputMappingService and updates the state of all
 * ControllableComponent instances based on the player's 2D input.
 */
public class PlayerInputSystem implements ISystem {

  private final IWorld world;
  private final InputMappingService mappingService;

  public PlayerInputSystem(IWorld world, InputMappingService mappingService) {
    this.world = world;
    this.mappingService = mappingService;
  }

  @Override
  public void update(float deltaTime) {
    for (int entityId : world.getEntitiesWith(ControllableComponent.class)) {
      ControllableComponent control = world.getComponent(entityId, ControllableComponent.class);
      int playerId = control.playerId;

      // Update the component's state based on the abstract 2D actions
      control.wantsToMoveUp = mappingService.isActionActive(playerId, GameAction.MOVE_UP);
      control.wantsToMoveDown = mappingService.isActionActive(playerId, GameAction.MOVE_DOWN);
      control.wantsToMoveLeft = mappingService.isActionActive(playerId, GameAction.MOVE_LEFT);
      control.wantsToMoveRight = mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT);
    }
  }
}


