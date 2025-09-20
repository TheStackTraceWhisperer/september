package september.engine.core.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_A;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_B;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_BACK;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_START;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_Y;

/**
 * Unit test for GlfwGamepadService focusing on logic aspects that can be tested without GLFW.
 * Tests boundary checking, invalid input handling, and service behavior.
 */
class GlfwGamepadServiceTest {

  private GlfwGamepadService gamepadService;

  @BeforeEach
  void setUp() {
    gamepadService = new GlfwGamepadService();
  }

  @Test
  @DisplayName("isGamepadConnected should return false for negative indices")
  void isGamepadConnected_returnsFalseForNegativeIndices() {
    // Act & Assert: Test invalid negative indices
    assertThat(gamepadService.isGamepadConnected(-1))
      .as("isGamepadConnected should return false for index -1")
      .isFalse();

    assertThat(gamepadService.isGamepadConnected(-10))
      .as("isGamepadConnected should return false for index -10")
      .isFalse();
  }

  @Test
  @DisplayName("isGamepadConnected should return false for indices beyond MAX_GAMEPADS")
  void isGamepadConnected_returnsFalseForIndicesBeyondMax() {
    // Act & Assert: Test indices beyond the supported range (8 gamepads: 0-7)
    assertThat(gamepadService.isGamepadConnected(8))
      .as("isGamepadConnected should return false for index 8")
      .isFalse();

    assertThat(gamepadService.isGamepadConnected(99))
      .as("isGamepadConnected should return false for index 99")
      .isFalse();

    assertThat(gamepadService.isGamepadConnected(Integer.MAX_VALUE))
      .as("isGamepadConnected should return false for very large indices")
      .isFalse();
  }

  @Test
  @DisplayName("isGamepadConnected should handle valid indices within range")
  void isGamepadConnected_handlesValidIndices() {
    // Note: In a headless environment, gamepads likely won't be connected,
    // but the method should not crash and should return a boolean value

    // Act & Assert: Test valid indices (0-7)
    for (int i = 0; i < 8; i++) {
      boolean result = gamepadService.isGamepadConnected(i);
      assertThat(result)
        .as("isGamepadConnected should return a boolean value for valid index " + i)
        .isIn(true, false); // Either true or false is acceptable
    }
  }

  @Test
  @DisplayName("getAxis should return 0.0f for invalid gamepad indices")
  void getAxis_returnsZeroForInvalidIndices() {
    // Act & Assert: Test invalid indices
    assertThat(gamepadService.getAxis(-1, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("getAxis should return 0.0f for negative index")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(99, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("getAxis should return 0.0f for out-of-range index")
      .isEqualTo(0.0f);
  }

  @Test
  @DisplayName("getAxis should return 0.0f for valid indices when gamepad not connected")
  void getAxis_returnsZeroForDisconnectedGamepads() {
    // Note: In headless environment, gamepads are likely not connected
    // Act & Assert: Test that disconnected gamepads return 0.0f
    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("getAxis should return 0.0f for disconnected gamepad")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(1, GLFW_GAMEPAD_AXIS_LEFT_Y))
      .as("getAxis should return 0.0f for disconnected gamepad")
      .isEqualTo(0.0f);
  }

  @Test
  @DisplayName("getAxis should handle all standard gamepad axis constants")
  void getAxis_handlesStandardAxisConstants() {
    // Act & Assert: Test that all standard axis constants are handled
    // (should not crash and should return 0.0f for disconnected gamepads)
    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("getAxis should handle LEFT_X axis")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y))
      .as("getAxis should handle LEFT_Y axis")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_RIGHT_X))
      .as("getAxis should handle RIGHT_X axis")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_RIGHT_Y))
      .as("getAxis should handle RIGHT_Y axis")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_TRIGGER))
      .as("getAxis should handle LEFT_TRIGGER axis")
      .isEqualTo(0.0f);

    assertThat(gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER))
      .as("getAxis should handle RIGHT_TRIGGER axis")
      .isEqualTo(0.0f);
  }

  @Test
  @DisplayName("isButtonPressed should return false for invalid gamepad indices")
  void isButtonPressed_returnsFalseForInvalidIndices() {
    // Act & Assert: Test invalid indices
    assertThat(gamepadService.isButtonPressed(-1, GLFW_GAMEPAD_BUTTON_A))
      .as("isButtonPressed should return false for negative index")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(99, GLFW_GAMEPAD_BUTTON_A))
      .as("isButtonPressed should return false for out-of-range index")
      .isFalse();
  }

  @Test
  @DisplayName("isButtonPressed should return false for valid indices when gamepad not connected")
  void isButtonPressed_returnsFalseForDisconnectedGamepads() {
    // Note: In headless environment, gamepads are likely not connected
    // Act & Assert: Test that disconnected gamepads return false
    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A))
      .as("isButtonPressed should return false for disconnected gamepad")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(1, GLFW_GAMEPAD_BUTTON_B))
      .as("isButtonPressed should return false for disconnected gamepad")
      .isFalse();
  }

  @Test
  @DisplayName("isButtonPressed should handle all standard gamepad button constants")
  void isButtonPressed_handlesStandardButtonConstants() {
    // Act & Assert: Test that all standard button constants are handled
    // (should not crash and should return false for disconnected gamepads)
    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A))
      .as("isButtonPressed should handle A button")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_B))
      .as("isButtonPressed should handle B button")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_X))
      .as("isButtonPressed should handle X button")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_Y))
      .as("isButtonPressed should handle Y button")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_START))
      .as("isButtonPressed should handle START button")
      .isFalse();

    assertThat(gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_BACK))
      .as("isButtonPressed should handle BACK button")
      .isFalse();
  }

  @Test
  @DisplayName("Service methods should be consistent in their validation")
  void serviceMethods_consistentValidation() {
    // Act & Assert: Test that validation logic is consistent across methods

    // Invalid index should behave consistently
    int invalidIndex = -1;
    assertThat(gamepadService.isGamepadConnected(invalidIndex))
      .as("All methods should consistently handle invalid index -1")
      .isFalse();

    assertThat(gamepadService.getAxis(invalidIndex, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("getAxis should return 0.0f for invalid index")
      .isEqualTo(0.0f);

    assertThat(gamepadService.isButtonPressed(invalidIndex, GLFW_GAMEPAD_BUTTON_A))
      .as("isButtonPressed should return false for invalid index")
      .isFalse();

    // Out-of-range index should behave consistently
    int outOfRangeIndex = 99;
    assertThat(gamepadService.isGamepadConnected(outOfRangeIndex))
      .as("All methods should consistently handle out-of-range index")
      .isFalse();

    assertThat(gamepadService.getAxis(outOfRangeIndex, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("getAxis should return 0.0f for out-of-range index")
      .isEqualTo(0.0f);

    assertThat(gamepadService.isButtonPressed(outOfRangeIndex, GLFW_GAMEPAD_BUTTON_A))
      .as("isButtonPressed should return false for out-of-range index")
      .isFalse();
  }
}
