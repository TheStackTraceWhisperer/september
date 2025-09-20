package september.engine.systems;

import september.engine.core.input.InputService;
import september.engine.ecs.IWorld;
import september.engine.ecs.World;
import september.engine.events.EventBus;
import september.engine.events.UIButtonClickedEvent;
import september.engine.ui.components.UIButtonComponent;
import september.engine.ui.components.UITransformComponent;

// NOTE: This is a simplified test file due to the lack of a testing framework.

public class UISystemTest {

  // Mock for InputService
  static class MockInputService implements InputService {
    private double mouseX, mouseY;
    private boolean isPressed;

    public void setMouse(double x, double y) {
      this.mouseX = x;
      this.mouseY = y;
    }

    public void setPressed(boolean pressed) {
      this.isPressed = pressed;
    }

    @Override
    public double getMouseX() {
      return mouseX;
    }

    @Override
    public double getMouseY() {
      return mouseY;
    }

    @Override
    public boolean isMouseButtonPressed(int button) {
      return isPressed;
    }

    // Unused methods
    @Override
    public boolean isKeyPressed(int key) {
      return false;
    }

    @Override
    public boolean isKeyReleased(int key) {
      return false;
    }
  }

  // Mock for EventBus
  static class MockEventBus extends EventBus {
    public UIButtonClickedEvent publishedEvent = null;

    @Override
    public <T extends september.engine.events.Event> void publish(T event) {
      if (event instanceof UIButtonClickedEvent) {
        publishedEvent = (UIButtonClickedEvent) event;
      }
    }
  }

  public static void main(String[] args) {
    System.out.println("Running UISystem tests...");

    // 1. Set up mocks and the UISystem instance
    IWorld world = new World();
    MockInputService mockInput = new MockInputService();
    MockEventBus mockEventBus = new MockEventBus();
    // WindowContext is not directly used in the tested logic, so null is fine
    UISystem uiSystem = new UISystem(world, null, mockInput, mockEventBus);

    // 2. Create a button entity for testing
    int buttonEntity = world.createEntity();
    UITransformComponent transform = new UITransformComponent();
    transform.screenBounds = new float[]{100, 100, 300, 150}; // Manually set screen bounds for testing
    UIButtonComponent button = new UIButtonComponent();
    button.actionEvent = "TEST_ACTION";
    world.addComponent(buttonEntity, transform);
    world.addComponent(buttonEntity, button);

    // --- Test 1: Button Hover ---
    mockInput.setMouse(150, 475); // Y is flipped (600 - 125)
    mockInput.setPressed(false);
    uiSystem.update(0.0f);
    assert button.currentState == UIButtonComponent.ButtonState.HOVERED : "Test Failed: Button should be in HOVERED state";
    assert mockEventBus.publishedEvent == null : "Test Failed: No event should be published on hover";
    System.out.println("- Button Hover test PASSED");

    // --- Test 2: Button Press (but not release) ---
    mockInput.setPressed(true);
    uiSystem.update(0.0f);
    assert button.currentState == UIButtonComponent.ButtonState.PRESSED : "Test Failed: Button should be in PRESSED state";
    assert mockEventBus.publishedEvent == null : "Test Failed: No event should be published on press";
    System.out.println("- Button Press test PASSED");

    // --- Test 3: Button Click (Press and Release) ---
    // The state is already PRESSED from the previous test.
    mockInput.setPressed(false); // Simulate release
    uiSystem.update(0.0f);
    assert mockEventBus.publishedEvent != null : "Test Failed: A click event should have been published";
    assert "TEST_ACTION".equals(mockEventBus.publishedEvent.actionEvent()) : "Test Failed: Published event has wrong action string";
    System.out.println("- Button Click test PASSED");

    // --- Test 4: Mouse leaves button area ---
    mockEventBus.publishedEvent = null; // Reset for next test
    mockInput.setMouse(0, 0);
    uiSystem.update(0.0f);
    assert button.currentState == UIButtonComponent.ButtonState.NORMAL : "Test Failed: Button should be in NORMAL state";
    assert mockEventBus.publishedEvent == null : "Test Failed: No event should be published when mouse leaves";
    System.out.println("- Mouse Leave test PASSED");

    System.out.println("All UISystem tests passed!");
  }
}
