package september.engine.systems;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import september.engine.core.WindowContext;
import september.engine.core.input.InputService;
import september.engine.ecs.ISystem;
import september.engine.ecs.IWorld;
import september.engine.events.EventBus;
import september.engine.events.UIButtonClickedEvent;
import september.engine.ui.components.UIButtonComponent;
import september.engine.ui.components.UIImageComponent;
import september.engine.ui.components.UITransformComponent;

/**
 * Manages the layout, interaction, and event handling for all UI entities.
 */
public class UISystem implements ISystem {

  private final IWorld world;
  private final WindowContext window;
  private final InputService inputService;
  private final EventBus eventBus;

  public UISystem(IWorld world, WindowContext window, InputService inputService, EventBus eventBus) {
    this.world = world;
    this.window = window;
    this.inputService = inputService;
    this.eventBus = eventBus;
  }

  @Override
  public int getPriority() {
    return Priority.UI_LOGIC;
  }

  @Override
  public void update(float deltaTime) {
    var uiEntities = world.getEntitiesWith(UITransformComponent.class);

    calculateLayout(uiEntities);
    handleButtonInteractions(uiEntities);
  }

  private void calculateLayout(Iterable<Integer> entities) {
    // TODO: Get actual window dimensions from the WindowContext.
    Vector2f parentSize = new Vector2f(800, 600);

    for (int entityId : entities) {
      var transform = world.getComponent(entityId, UITransformComponent.class);
      float anchorPosX = parentSize.x * transform.anchor.x;
      float anchorPosY = parentSize.y * transform.anchor.y;
      float pivotPosX = transform.size.x * transform.pivot.x;
      float pivotPosY = transform.size.y * transform.pivot.y;
      // Use the x and y from the new Vector3f offset
      float minX = anchorPosX - pivotPosX + transform.offset.x;
      float minY = anchorPosY - pivotPosY + transform.offset.y;
      float maxX = minX + transform.size.x;
      float maxY = minY + transform.size.y;

      transform.screenBounds[0] = minX;
      transform.screenBounds[1] = minY;
      transform.screenBounds[2] = maxX;
      transform.screenBounds[3] = maxY;
    }
  }

  private void handleButtonInteractions(Iterable<Integer> entities) {
    double mouseX = inputService.getMouseX();
    // CORRECTED: Flip the Y-coordinate to match OpenGL's bottom-left origin.
    double mouseY = 600 - inputService.getMouseY(); // Assuming window height is 600
    boolean isMouseDown = inputService.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);

    for (int entityId : entities) {
      if (!world.hasComponent(entityId, UIButtonComponent.class) || !world.hasComponent(entityId, UIImageComponent.class)) {
        continue;
      }

      var transform = world.getComponent(entityId, UITransformComponent.class);
      var button = world.getComponent(entityId, UIButtonComponent.class);
      var image = world.getComponent(entityId, UIImageComponent.class);

      float[] bounds = transform.screenBounds;
      boolean isHovered = mouseX >= bounds[0] && mouseX <= bounds[2] &&
        mouseY >= bounds[1] && mouseY <= bounds[3];

      // Determine the new state
      UIButtonComponent.ButtonState previousState = button.currentState;
      if (isHovered) {
        if (isMouseDown) {
          button.currentState = UIButtonComponent.ButtonState.PRESSED;
        } else {
          if (previousState == UIButtonComponent.ButtonState.PRESSED) {
            eventBus.publish(new UIButtonClickedEvent(button.actionEvent));
          }
          button.currentState = UIButtonComponent.ButtonState.HOVERED;
        }
      } else {
        button.currentState = UIButtonComponent.ButtonState.NORMAL;
      }

      // Update the image based on the new state
      if (button.currentState != previousState) {
        switch (button.currentState) {
          case HOVERED:
            image.textureHandle = button.hoveredTexture;
            break;
          case PRESSED:
            image.textureHandle = button.pressedTexture;
            break;
          default: // NORMAL
            image.textureHandle = button.normalTexture;
            break;
        }
      }
    }
  }
}
