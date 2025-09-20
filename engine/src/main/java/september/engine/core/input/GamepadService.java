package september.engine.core.input;

/**
 * Provides an abstract interface for querying the state of gamepad/controller devices.
 * This service is responsible for reporting the current state of gamepad axes and buttons
 * for the current frame. It abstracts GLFW gamepad operations for testability.
 */
public interface GamepadService {
  /**
   * Checks if a gamepad is connected at the specified index.
   *
   * @param index The gamepad index (0-7).
   * @return true if a gamepad is connected and recognized, false otherwise.
   */
  boolean isGamepadConnected(int index);

  /**
   * Gets the current value of a gamepad axis.
   *
   * @param index     The gamepad index (0-7).
   * @param axisConst The GLFW axis constant (e.g., GLFW_GAMEPAD_AXIS_LEFT_X).
   * @return The axis value (-1.0 to 1.0), or 0.0f if gamepad not connected.
   */
  float getAxis(int index, int axisConst);

  /**
   * Checks if a gamepad button is currently pressed.
   *
   * @param index       The gamepad index (0-7).
   * @param buttonConst The GLFW button constant (e.g., GLFW_GAMEPAD_BUTTON_A).
   * @return true if the button is pressed, false if not pressed or gamepad not connected.
   */
  boolean isButtonPressed(int index, int buttonConst);
}
