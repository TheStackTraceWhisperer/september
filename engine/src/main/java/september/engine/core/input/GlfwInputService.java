package september.engine.core.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import september.engine.core.WindowContext;

import java.util.Arrays;

/**
 * A concrete implementation of InputService that uses GLFW callbacks to track input state.
 * This class maintains arrays for key and mouse button states and updates them
 * when GLFW signals an event.
 */
public final class GlfwInputService implements InputService {
  private final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
  private final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST + 1];
  private double mouseX;
  private double mouseY;

  /**
   * Installs the necessary GLFW callbacks on the given window to capture input events.
   *
   * @param window the WindowContext to listen to for input.
   */
  public void installCallbacks(WindowContext window) {
    GLFW.glfwSetKeyCallback(window.handle(), new GLFWKeyCallback() {
      @Override
      public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key <= GLFW.GLFW_KEY_LAST) {
          keys[key] = (action != GLFW.GLFW_RELEASE);
        }
      }
    });

    GLFW.glfwSetMouseButtonCallback(window.handle(), new GLFWMouseButtonCallback() {
      @Override
      public void invoke(long window, int button, int action, int mods) {
        if (button >= 0 && button <= GLFW.GLFW_MOUSE_BUTTON_LAST) {
          mouseButtons[button] = (action != GLFW.GLFW_RELEASE);
        }
      }
    });

    GLFW.glfwSetCursorPosCallback(window.handle(), new GLFWCursorPosCallback() {
      @Override
      public void invoke(long window, double xpos, double ypos) {
        mouseX = xpos;
        mouseY = ypos;
      }
    });
  }

  /**
   * Clears all input states. Useful for resetting state between frames if needed,
   * though the callback approach makes this less necessary.
   */
  public void clear() {
    Arrays.fill(keys, false);
    Arrays.fill(mouseButtons, false);
    mouseX = 0.0;
    mouseY = 0.0;
  }

  @Override
  public boolean isKeyPressed(int keyCode) {
    if (keyCode < 0 || keyCode > GLFW.GLFW_KEY_LAST) {
      return false;
    }
    return keys[keyCode];
  }

  @Override
  public boolean isMouseButtonPressed(int button) {
    if (button < 0 || button > GLFW.GLFW_MOUSE_BUTTON_LAST) {
      return false;
    }
    return mouseButtons[button];
  }

  @Override
  public double getMouseX() {
    return mouseX;
  }

  @Override
  public double getMouseY() {
    return mouseY;
  }
}
