package september.game.input;

import org.lwjgl.glfw.GLFW;
import september.engine.core.input.GamepadService;
import september.engine.core.input.InputService;

import java.util.HashMap;
import java.util.Map;

/**
 * A mapping service that supports both keyboard and gamepad inputs.
 * Automatically assigns players to connected gamepads and provides keyboard fallback
 * for player 0 when no gamepad is available.
 */
public class MultiDeviceMappingService implements InputMappingService {

  private static final int MAX_PLAYERS = 8;
  private static final float DEADZONE = 0.25f;

  private final InputService inputService;
  private final GamepadService gamepadService;
  
  // Device bindings: player -> device type
  private final Map<Integer, DeviceBinding> playerBindings = new HashMap<>();
  
  // Keyboard mappings
  private final Map<Integer, Map<GameAction, Integer>> playerKeyMappings = new HashMap<>();

  public MultiDeviceMappingService(InputService inputService, GamepadService gamepadService) {
    this.inputService = inputService;
    this.gamepadService = gamepadService;
    loadDefaultMappings();
    refreshAssignments();
  }

  private void loadDefaultMappings() {
    // Player 0 keyboard controls: WASD + Space
    Map<GameAction, Integer> player0KeyMap = new HashMap<>();
    player0KeyMap.put(GameAction.MOVE_UP, GLFW.GLFW_KEY_W);
    player0KeyMap.put(GameAction.MOVE_DOWN, GLFW.GLFW_KEY_S);
    player0KeyMap.put(GameAction.MOVE_LEFT, GLFW.GLFW_KEY_A);
    player0KeyMap.put(GameAction.MOVE_RIGHT, GLFW.GLFW_KEY_D);
    player0KeyMap.put(GameAction.ATTACK, GLFW.GLFW_KEY_SPACE);
    playerKeyMappings.put(0, player0KeyMap);
  }

  @Override
  public boolean isActionActive(int playerId, GameAction action) {
    DeviceBinding binding = playerBindings.get(playerId);
    if (binding == null) {
      return false;
    }

    switch (binding.type) {
      case KEYBOARD:
        return isKeyboardActionActive(playerId, action);
      case GAMEPAD:
        return isGamepadActionActive(binding.deviceIndex, action);
      default:
        return false;
    }
  }

  private boolean isKeyboardActionActive(int playerId, GameAction action) {
    Map<GameAction, Integer> mappings = playerKeyMappings.get(playerId);
    if (mappings == null) {
      return false;
    }

    Integer keyCode = mappings.get(action);
    if (keyCode == null) {
      return false;
    }

    return inputService.isKeyPressed(keyCode);
  }

  private boolean isGamepadActionActive(int gamepadIndex, GameAction action) {
    if (!gamepadService.isGamepadConnected(gamepadIndex)) {
      return false;
    }

    switch (action) {
      case MOVE_UP:
        return gamepadService.getAxis(gamepadIndex, GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y) < -DEADZONE;
      case MOVE_DOWN:
        return gamepadService.getAxis(gamepadIndex, GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y) > DEADZONE;
      case MOVE_LEFT:
        return gamepadService.getAxis(gamepadIndex, GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) < -DEADZONE;
      case MOVE_RIGHT:
        return gamepadService.getAxis(gamepadIndex, GLFW.GLFW_GAMEPAD_AXIS_LEFT_X) > DEADZONE;
      case ATTACK:
        return gamepadService.isButtonPressed(gamepadIndex, GLFW.GLFW_GAMEPAD_BUTTON_A);
      default:
        return false;
    }
  }

  /**
   * Bind a player to keyboard input.
   *
   * @param playerId The player ID (0-7).
   */
  public void bindPlayerToKeyboard(int playerId) {
    if (playerId >= 0 && playerId < MAX_PLAYERS) {
      playerBindings.put(playerId, new DeviceBinding(DeviceType.KEYBOARD, -1));
    }
  }

  /**
   * Bind a player to a specific gamepad.
   *
   * @param playerId The player ID (0-7).
   * @param gamepadIndex The gamepad index (0-7).
   */
  public void bindPlayerToGamepad(int playerId, int gamepadIndex) {
    if (playerId >= 0 && playerId < MAX_PLAYERS && gamepadIndex >= 0 && gamepadIndex < MAX_PLAYERS) {
      playerBindings.put(playerId, new DeviceBinding(DeviceType.GAMEPAD, gamepadIndex));
    }
  }

  /**
   * Get the maximum number of supported players.
   *
   * @return Maximum supported players (8).
   */
  public int getMaxSupportedPlayers() {
    return MAX_PLAYERS;
  }

  /**
   * Refresh device assignments by scanning for connected gamepads.
   * Auto-assigns players 0..N-1 to gamepads 0..N-1 when connected.
   * Ensures player 0 always has a binding (prefer gamepad 0, fallback to keyboard).
   */
  public void refreshAssignments() {
    // Clear all bindings except explicit keyboard bindings
    playerBindings.clear();
    
    // Auto-assign connected gamepads to players
    for (int gamepadIndex = 0; gamepadIndex < MAX_PLAYERS; gamepadIndex++) {
      if (gamepadService.isGamepadConnected(gamepadIndex)) {
        playerBindings.put(gamepadIndex, new DeviceBinding(DeviceType.GAMEPAD, gamepadIndex));
      }
    }
    
    // Ensure player 0 always has a binding
    if (!playerBindings.containsKey(0)) {
      bindPlayerToKeyboard(0);
    }
  }

  private enum DeviceType {
    KEYBOARD,
    GAMEPAD
  }

  private static class DeviceBinding {
    final DeviceType type;
    final int deviceIndex; // -1 for keyboard, 0-7 for gamepad

    DeviceBinding(DeviceType type, int deviceIndex) {
      this.type = type;
      this.deviceIndex = deviceIndex;
    }
  }
}