//package september.engine.core.input;
//
///**
// * Provides an abstract interface for querying the state of hardware input devices.
// * This service is responsible for reporting the current state of keys and the mouse
// * for the current frame. It is intentionally simple and does not deal with
// * higher-level concepts like "actions" or "mappings".
// */
//public interface InputService {
//  /**
//   * Checks if a specific keyboard key is currently held down.
//   *
//   * @param keyCode The GLFW key code (e.g., GLFW.GLFW_KEY_W).
//   * @return true if the key is pressed, false otherwise.
//   */
//  boolean isKeyPressed(int keyCode);
//
//  /**
//   * Checks if a specific mouse button is currently held down.
//   *
//   * @param button The GLFW mouse button code (e.g., GLFW.GLFW_MOUSE_BUTTON_1).
//   * @return true if the button is pressed, false otherwise.
//   */
//  boolean isMouseButtonPressed(int button);
//
//  /**
//   * @return The current x-coordinate of the mouse cursor, in screen coordinates.
//   */
//  double getMouseX();
//
//  /**
//   * @return The current y-coordinate of the mouse cursor, in screen coordinates.
//   */
//  double getMouseY();
//}
