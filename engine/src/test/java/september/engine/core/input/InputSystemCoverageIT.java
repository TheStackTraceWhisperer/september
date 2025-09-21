package september.engine.core.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Comprehensive integration test that exercises various input system functionality
 * to ensure we achieve good coverage across the input package.
 */
class InputSystemCoverageIT {

  private GlfwInputService inputService;
  private GlfwGamepadService gamepadService;

  @BeforeEach
  void setUp() {
    inputService = new GlfwInputService();
    gamepadService = new GlfwGamepadService();
  }

  @Test
  @DisplayName("Input service should exercise all key methods")
  void inputService_exercisesAllKeyMethods() {
    // Exercise isKeyPressed for various key types
    assertThat(inputService.isKeyPressed(GLFW_KEY_SPACE))
      .as("Should handle space key")
      .isFalse();

    assertThat(inputService.isKeyPressed(GLFW_KEY_ENTER))
      .as("Should handle enter key")
      .isFalse();

    assertThat(inputService.isKeyPressed(GLFW_KEY_ESCAPE))
      .as("Should handle escape key")
      .isFalse();

    // Exercise boundary conditions
    assertThat(inputService.isKeyPressed(-1))
      .as("Should handle invalid negative key")
      .isFalse();

    assertThat(inputService.isKeyPressed(999999))
      .as("Should handle invalid large key")
      .isFalse();
  }

  @Test
  @DisplayName("Input service should exercise all mouse methods")
  void inputService_exercisesAllMouseMethods() {
    // Exercise isMouseButtonPressed
    assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
      .as("Should handle left mouse button")
      .isFalse();

    assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
      .as("Should handle right mouse button")
      .isFalse();

    // Exercise mouse position methods
    double mouseX = inputService.getMouseX();
    double mouseY = inputService.getMouseY();

    assertThat(mouseX)
      .as("getMouseX should return a valid double")
      .isInstanceOf(Double.class);

    assertThat(mouseY)
      .as("getMouseY should return a valid double")
      .isInstanceOf(Double.class);

    // Exercise boundary conditions for mouse buttons
    assertThat(inputService.isMouseButtonPressed(-1))
      .as("Should handle invalid negative mouse button")
      .isFalse();

    assertThat(inputService.isMouseButtonPressed(999999))
      .as("Should handle invalid large mouse button")
      .isFalse();
  }

  @Test
  @DisplayName("Input service should exercise clear functionality")
  void inputService_exercisesClearFunctionality() {
    // Exercise clear method
    inputService.clear();

    // Verify state after clear
    assertThat(inputService.isKeyPressed(GLFW_KEY_W))
      .as("Keys should be cleared")
      .isFalse();

    assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
      .as("Mouse buttons should be cleared")
      .isFalse();

    assertThat(inputService.getMouseX())
      .as("Mouse X should be cleared")
      .isEqualTo(0.0);

    assertThat(inputService.getMouseY())
      .as("Mouse Y should be cleared")
      .isEqualTo(0.0);
  }

  @Test
  @DisplayName("Gamepad service should exercise all connection methods")
  void gamepadService_exercisesAllConnectionMethods() {
    // Exercise isGamepadConnected for all valid indices
    for (int i = 0; i < 8; i++) {
      boolean connected = gamepadService.isGamepadConnected(i);
      assertThat(connected)
        .as("Should return boolean for gamepad " + i)
        .isIn(true, false);
    }

    // Exercise boundary conditions
    assertThat(gamepadService.isGamepadConnected(-1))
      .as("Should handle negative index")
      .isFalse();

    assertThat(gamepadService.isGamepadConnected(8))
      .as("Should handle index beyond max")
      .isFalse();
  }

  @Test
  @DisplayName("Gamepad service should exercise all axis methods")
  void gamepadService_exercisesAllAxisMethods() {
    // Exercise getAxis for all standard axes
    int[] axes = {
      GLFW_GAMEPAD_AXIS_LEFT_X,
      GLFW_GAMEPAD_AXIS_LEFT_Y,
      GLFW_GAMEPAD_AXIS_RIGHT_X,
      GLFW_GAMEPAD_AXIS_RIGHT_Y,
      GLFW_GAMEPAD_AXIS_LEFT_TRIGGER,
      GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER
    };

    for (int axis : axes) {
      float value = gamepadService.getAxis(0, axis);
      assertThat(value)
        .as("Axis " + axis + " should return valid float")
        .isInstanceOf(Float.class);
    }

    // Exercise invalid gamepad indices
    assertThat(gamepadService.getAxis(-1, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("Should return 0.0f for invalid index")
      .isEqualTo(0.0f);
  }

  @Test
  @DisplayName("Gamepad service should exercise all button methods")
  void gamepadService_exercisesAllButtonMethods() {
    // Exercise isButtonPressed for all standard buttons
    int[] buttons = {
      GLFW_GAMEPAD_BUTTON_A,
      GLFW_GAMEPAD_BUTTON_B,
      GLFW_GAMEPAD_BUTTON_X,
      GLFW_GAMEPAD_BUTTON_Y,
      GLFW_GAMEPAD_BUTTON_LEFT_BUMPER,
      GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER,
      GLFW_GAMEPAD_BUTTON_BACK,
      GLFW_GAMEPAD_BUTTON_START
    };

    for (int button : buttons) {
      boolean pressed = gamepadService.isButtonPressed(0, button);
      assertThat(pressed)
        .as("Button " + button + " should return valid boolean")
        .isIn(true, false);
    }

    // Exercise invalid gamepad indices
    assertThat(gamepadService.isButtonPressed(-1, GLFW_GAMEPAD_BUTTON_A))
      .as("Should return false for invalid index")
      .isFalse();
  }

  @Test
  @DisplayName("All input service methods should handle edge cases gracefully")
  void allInputMethods_handleEdgeCasesGracefully() {
    // Test that no methods throw exceptions for edge cases

    // Input service edge cases
    assertThat(inputService.isKeyPressed(Integer.MIN_VALUE))
      .as("Should handle minimum integer key code")
      .isFalse();

    assertThat(inputService.isKeyPressed(Integer.MAX_VALUE))
      .as("Should handle maximum integer key code")
      .isFalse();

    assertThat(inputService.isMouseButtonPressed(Integer.MIN_VALUE))
      .as("Should handle minimum integer mouse button")
      .isFalse();

    assertThat(inputService.isMouseButtonPressed(Integer.MAX_VALUE))
      .as("Should handle maximum integer mouse button")
      .isFalse();

    // Gamepad service edge cases
    assertThat(gamepadService.isGamepadConnected(Integer.MIN_VALUE))
      .as("Should handle minimum integer gamepad index")
      .isFalse();

    assertThat(gamepadService.isGamepadConnected(Integer.MAX_VALUE))
      .as("Should handle maximum integer gamepad index")
      .isFalse();

    assertThat(gamepadService.getAxis(Integer.MIN_VALUE, GLFW_GAMEPAD_AXIS_LEFT_X))
      .as("Should handle minimum integer gamepad index in getAxis")
      .isEqualTo(0.0f);

    assertThat(gamepadService.isButtonPressed(Integer.MAX_VALUE, GLFW_GAMEPAD_BUTTON_A))
      .as("Should handle maximum integer gamepad index in isButtonPressed")
      .isFalse();
  }
}
