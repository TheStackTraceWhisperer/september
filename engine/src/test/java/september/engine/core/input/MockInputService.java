package september.engine.core.input;

import java.util.HashSet;
import java.util.Set;

/**
 * A mock implementation of an input service for testing purposes.
 * This class provides full control over the simulated input state and does not
 * depend on a live GLFW window.
 */
public class MockInputService {

    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> pressedMouseButtons = new HashSet<>();
    private double mouseX = 0.0;
    private double mouseY = 0.0;

    public boolean isKeyPressed(int key) {
        return pressedKeys.contains(key);
    }

    public boolean isMouseButtonPressed(int button) {
        return pressedMouseButtons.contains(button);
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    // --- Test Control Methods ---

    public void setKeyPressed(int key, boolean isPressed) {
        if (isPressed) {
            pressedKeys.add(key);
        } else {
            pressedKeys.remove(key);
        }
    }

    public void setMouseButtonPressed(int button, boolean isPressed) {
        if (isPressed) {
            pressedMouseButtons.add(button);
        } else {
            pressedMouseButtons.remove(button);
        }
    }

    public void setMousePosition(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    public void clear() {
        pressedKeys.clear();
        pressedMouseButtons.clear();
        mouseX = 0.0;
        mouseY = 0.0;
    }
}
