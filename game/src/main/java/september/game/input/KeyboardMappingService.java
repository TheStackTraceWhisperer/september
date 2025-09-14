package september.game.input;
import org.lwjgl.glfw.GLFW;
import september.engine.core.input.InputService;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of InputMappingService that uses hard-coded keyboard mappings
 * for a 2D Action RPG. A more advanced implementation would load these from a config file.
 */
public class KeyboardMappingService implements InputMappingService {

  private final InputService inputService;
  private final Map<Integer, Map<GameAction, Integer>> playerMappings = new HashMap<>();

  public KeyboardMappingService(InputService inputService) {
    this.inputService = inputService;
    loadDefaultMappings();
  }

  private void loadDefaultMappings() {
    // Player 0 (Player 1) gets WASD controls mapped to 2D directions
    Map<GameAction, Integer> player0Map = new HashMap<>();
    player0Map.put(GameAction.MOVE_UP, GLFW.GLFW_KEY_W);
    player0Map.put(GameAction.MOVE_DOWN, GLFW.GLFW_KEY_S);
    player0Map.put(GameAction.MOVE_LEFT, GLFW.GLFW_KEY_A);
    player0Map.put(GameAction.MOVE_RIGHT, GLFW.GLFW_KEY_D);
    playerMappings.put(0, player0Map);

    // TODO: Add mappings for other actions like ATTACK (e.g., Spacebar)
  }

  @Override
  public boolean isActionActive(int playerId, GameAction action) {
    Map<GameAction, Integer> mappings = playerMappings.get(playerId);
    if (mappings == null) {
      return false;
    }

    Integer keyCode = mappings.get(action);
    if (keyCode == null) {
      return false;
    }

    return inputService.isKeyPressed(keyCode);
  }
}


