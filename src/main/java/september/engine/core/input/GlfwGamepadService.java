package september.engine.core.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

/**
 * A concrete implementation of GamepadService that uses GLFW to query gamepad state.
 * Supports up to 8 gamepads (indices 0-7) mapped to GLFW joystick IDs.
 */
public final class GlfwGamepadService implements GamepadService {

  private static final int MAX_GAMEPADS = 8;

  @Override
  public boolean isGamepadConnected(int index) {
    if (index < 0 || index >= MAX_GAMEPADS) {
      return false;
    }

    int joystickId = GLFW.GLFW_JOYSTICK_1 + index;
    return GLFW.glfwJoystickPresent(joystickId) && GLFW.glfwJoystickIsGamepad(joystickId);
  }

  @Override
  public float getAxis(int index, int axisConst) {
    if (!isGamepadConnected(index)) {
      return 0.0f;
    }

    int joystickId = GLFW.GLFW_JOYSTICK_1 + index;
    GLFWGamepadState state = GLFWGamepadState.malloc();
    try {
      if (GLFW.glfwGetGamepadState(joystickId, state)) {
        return state.axes(axisConst);
      }
      return 0.0f;
    } finally {
      state.free();
    }
  }

  @Override
  public boolean isButtonPressed(int index, int buttonConst) {
    if (!isGamepadConnected(index)) {
      return false;
    }

    int joystickId = GLFW.GLFW_JOYSTICK_1 + index;
    GLFWGamepadState state = GLFWGamepadState.malloc();
    try {
      if (GLFW.glfwGetGamepadState(joystickId, state)) {
        return state.buttons(buttonConst) == GLFW.GLFW_PRESS;
      }
      return false;
    } finally {
      state.free();
    }
  }
}