package september.engine.ecs.components;

import september.engine.ecs.Component;

/**
 * A component that marks an entity as being controllable by a player.
 * It stores the player's input intent for the current frame, which other
 * systems (like a movement system) can then act upon.
 */
public class ControllableComponent implements Component {
  public int playerId = 0; // 0 for Player 1, 1 for Player 2, etc.

  // State fields updated by the PlayerInputSystem to reflect 2D movement
  public boolean wantsToMoveUp = false;
  public boolean wantsToMoveDown = false;
  public boolean wantsToMoveLeft = false;
  public boolean wantsToMoveRight = false;
}

