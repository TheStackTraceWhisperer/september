package september.game.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.input.GamepadService;
import september.engine.core.input.GlfwInputService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MultiDeviceMappingService.
 * Tests keyboard fallback, gamepad behavior, deadzone handling, and multi-player scenarios.
 */
@ExtendWith(MockitoExtension.class)
class MultiDeviceMappingServiceTest {

  @Mock
  private GlfwInputService mockInputService;

  @Mock
  private GamepadService mockGamepadService;

  private MultiDeviceMappingService mappingService;

  @BeforeEach
  void setUp() {
    // Setup minimal stubbing for constructor - only gamepad 0 is checked in refreshAssignments
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(false);
    mappingService = new MultiDeviceMappingService(mockInputService, mockGamepadService);
  }

  @Test
  @DisplayName("Player 0 should use keyboard when no gamepads are connected")
  void player0UsesKeyboardWhenNoGamepadsConnected() {
    // Arrange: No gamepads connected (service constructor calls refreshAssignments, which checks gamepad 0)
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(false);
    when(mockInputService.isKeyPressed(GLFW_KEY_W)).thenReturn(true);
    when(mockInputService.isKeyPressed(GLFW_KEY_S)).thenReturn(false);
    when(mockInputService.isKeyPressed(GLFW_KEY_SPACE)).thenReturn(true);

    // Recreate service to trigger refreshAssignments with new stubbing
    mappingService = new MultiDeviceMappingService(mockInputService, mockGamepadService);

    // Act & Assert
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_UP)).isTrue();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_DOWN)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.ATTACK)).isTrue();
  }

  @Test
  @DisplayName("Player 0 should use gamepad 0 when connected")
  void player0UsesGamepad0WhenConnected() {
    // Arrange: Gamepad 0 connected with left stick up and A button pressed
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
    when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y)).thenReturn(-0.8f); // Up
    when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(0.1f); // Center
    when(mockGamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A)).thenReturn(true);

    mappingService.refreshAssignments();

    // Act & Assert
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_UP)).isTrue();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_DOWN)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_LEFT)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_RIGHT)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.ATTACK)).isTrue();
  }

  @Test
  @DisplayName("Gamepad axes should respect deadzone threshold")
  void gamepadAxesRespectDeadzone() {
    // Arrange: Gamepad 0 connected with axes within deadzone
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
    when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y)).thenReturn(-0.2f); // Within deadzone
    when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(0.15f); // Within deadzone

    mappingService.refreshAssignments();

    // Act & Assert: Movement should be false due to deadzone
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_UP)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_DOWN)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_LEFT)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_RIGHT)).isFalse();
  }

  @Test
  @DisplayName("Gamepad axes outside deadzone should trigger movement")
  void gamepadAxesOutsideDeadzoneTriggersMovement() {
    // Arrange: Gamepad 0 connected with axes outside deadzone
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
    when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y)).thenReturn(0.5f); // Down
    when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(-0.7f); // Left

    mappingService.refreshAssignments();

    // Act & Assert
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_UP)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_DOWN)).isTrue();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_LEFT)).isTrue();
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_RIGHT)).isFalse();
  }

  @Test
  @DisplayName("Should support multiple connected gamepads")
  void supportsMultipleConnectedGamepads() {
    // Arrange: Gamepads 0, 1, and 2 connected
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
    when(mockGamepadService.isGamepadConnected(1)).thenReturn(true);
    when(mockGamepadService.isGamepadConnected(2)).thenReturn(true);
    for (int i = 3; i < 8; i++) {
      when(mockGamepadService.isGamepadConnected(i)).thenReturn(false);
    }

    // Gamepad 1: right movement
    when(mockGamepadService.getAxis(1, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(0.8f);

    // Gamepad 2: A button pressed
    when(mockGamepadService.isButtonPressed(2, GLFW_GAMEPAD_BUTTON_A)).thenReturn(true);

    mappingService.refreshAssignments();

    // Act & Assert
    assertThat(mappingService.isActionActive(1, GameAction.MOVE_RIGHT)).isTrue();
    assertThat(mappingService.isActionActive(1, GameAction.MOVE_LEFT)).isFalse();
    assertThat(mappingService.isActionActive(2, GameAction.ATTACK)).isTrue();
  }

  @Test
  @DisplayName("Should handle disconnected gamepad gracefully")
  void handlesDisconnectedGamepadGracefully() {
    // Arrange: All gamepads disconnected (already set up in setUp method)

    // Act & Assert: Should not crash and should return false
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_UP)).isFalse();
    assertThat(mappingService.isActionActive(1, GameAction.MOVE_DOWN)).isFalse();
    assertThat(mappingService.isActionActive(7, GameAction.ATTACK)).isFalse();
  }

  @Test
  @DisplayName("Should handle out-of-range player IDs gracefully")
  void handlesOutOfRangePlayerIdsGracefully() {
    // Arrange: Some gamepads connected
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
    mappingService.refreshAssignments();

    // Act & Assert: Out-of-range player IDs should return false
    assertThat(mappingService.isActionActive(-1, GameAction.MOVE_UP)).isFalse();
    assertThat(mappingService.isActionActive(8, GameAction.MOVE_DOWN)).isFalse();
    assertThat(mappingService.isActionActive(99, GameAction.ATTACK)).isFalse();
  }

  @Test
  @DisplayName("Manual binding should override auto-assignment")
  void manualBindingOverridesAutoAssignment() {
    // Arrange: Gamepad 0 connected but manually bind player 0 to keyboard
    when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
    when(mockInputService.isKeyPressed(GLFW_KEY_W)).thenReturn(true);

    mappingService.refreshAssignments(); // Auto-assign gamepad
    mappingService.bindPlayerToKeyboard(0); // Override with keyboard

    // Act & Assert: Should use keyboard instead of gamepad
    assertThat(mappingService.isActionActive(0, GameAction.MOVE_UP)).isTrue();
  }

  @Test
  @DisplayName("Should support up to 8 players")
  void supportsUpTo8Players() {
    // Arrange: All 8 gamepads connected
    for (int i = 0; i < 8; i++) {
      when(mockGamepadService.isGamepadConnected(i)).thenReturn(true);
      when(mockGamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_LEFT_Y)).thenReturn(-0.5f); // Up
    }

    mappingService.refreshAssignments();

    // Act & Assert: All 8 players should be able to move up
    for (int i = 0; i < 8; i++) {
      assertThat(mappingService.isActionActive(i, GameAction.MOVE_UP)).isTrue();
    }

    assertThat(mappingService.getMaxSupportedPlayers()).isEqualTo(8);
  }

  @Test
  @DisplayName("Should handle unmapped actions gracefully")
  void handlesUnmappedActionsGracefully() {
    // Arrange: Player 0 with keyboard (only basic actions mapped) - already set up

    // Act & Assert: Unmapped actions should return false
    assertThat(mappingService.isActionActive(0, GameAction.INTERACT)).isFalse();
    assertThat(mappingService.isActionActive(0, GameAction.OPEN_MENU)).isFalse();
  }

  // Helper methods (removed unnecessary ones that were causing stubbing issues)
}
