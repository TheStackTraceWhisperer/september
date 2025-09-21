package september.engine.systems;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.WindowContext;
import september.engine.core.input.GlfwInputService;
import september.engine.ecs.IWorld;
import september.engine.ecs.World;
import september.engine.events.EventBus;
import september.engine.events.UIButtonClickedEvent;
import september.engine.ui.components.UIButtonComponent;
import september.engine.ui.components.UIImageComponent;
import september.engine.ui.components.UITransformComponent;

@ExtendWith(MockitoExtension.class)
class UISystemTest {

  private static final int WINDOW_WIDTH = 800;
  private static final int WINDOW_HEIGHT = 600;

  @Mock private GlfwInputService mockInputService;
  @Mock private EventBus mockEventBus;
  @Mock private WindowContext mockWindowContext;

  private IWorld world;
  private UISystem uiSystem;
  private int buttonEntity;
  private UIButtonComponent button;
  private UIImageComponent image;

  @BeforeEach
  void setUp() {
    world = new World();
    uiSystem = new UISystem(world, mockWindowContext, mockInputService, mockEventBus);

    when(mockWindowContext.getWidth()).thenReturn(WINDOW_WIDTH);
    when(mockWindowContext.getHeight()).thenReturn(WINDOW_HEIGHT);

    buttonEntity = world.createEntity();

    // Set up the transform so that calculateLayout will produce the bounds the test expects.
    // Expected bounds: [100, 100, 300, 150] -> size (200, 50)
    UITransformComponent transform = new UITransformComponent();
    transform.anchor.set(0.0f, 0.0f);
    transform.pivot.set(0.0f, 0.0f);
    transform.size.set(200.0f, 50.0f);
    transform.offset.set(100.0f, 100.0f, 0.0f);

    button = new UIButtonComponent("TEST_ACTION", "normal_tex", "hover_tex", "pressed_tex", null, null, null);
    image = new UIImageComponent(button.normalTexture, null);

    world.addComponent(buttonEntity, transform);
    world.addComponent(buttonEntity, button);
    world.addComponent(buttonEntity, image);
  }

  @Test
  @DisplayName("Button state should be HOVERED and texture should update when mouse is over it")
  void buttonState_isHovered_whenMouseIsOver() {
    // Test with mouse at (150, 125) which is inside the calculated bounds [100,100,300,150]
    when(mockInputService.getMouseX()).thenReturn(150.0);
    when(mockInputService.getMouseY()).thenReturn(475.0); // 600 - 125
    when(mockInputService.isMouseButtonPressed(anyInt())).thenReturn(false);

    uiSystem.update(0.0f);

    assertThat(button.currentState).isEqualTo(UIButtonComponent.ButtonState.HOVERED);
    assertThat(image.textureHandle).isEqualTo(button.hoveredTexture);
    verify(mockEventBus, never()).publish(any());
  }

  @Test
  @DisplayName("Button state should be PRESSED and texture should update when mouse is over and down")
  void buttonState_isPressed_whenMouseIsOverAndDown() {
    when(mockInputService.getMouseX()).thenReturn(150.0);
    when(mockInputService.getMouseY()).thenReturn(475.0);
    when(mockInputService.isMouseButtonPressed(anyInt())).thenReturn(true);

    uiSystem.update(0.0f);

    assertThat(button.currentState).isEqualTo(UIButtonComponent.ButtonState.PRESSED);
    assertThat(image.textureHandle).isEqualTo(button.pressedTexture);
    verify(mockEventBus, never()).publish(any());
  }

  @Test
  @DisplayName("Click event should be published when mouse is released over a pressed button")
  void clickEvent_isPublished_onReleaseOverPressedButton() {
    button.currentState = UIButtonComponent.ButtonState.PRESSED;
    when(mockInputService.getMouseX()).thenReturn(150.0);
    when(mockInputService.getMouseY()).thenReturn(475.0);
    when(mockInputService.isMouseButtonPressed(anyInt())).thenReturn(false);

    uiSystem.update(0.0f);

    ArgumentCaptor<UIButtonClickedEvent> eventCaptor =
        ArgumentCaptor.forClass(UIButtonClickedEvent.class);
    verify(mockEventBus).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().actionEvent()).isEqualTo("TEST_ACTION");
  }

  @Test
  @DisplayName("Button state should be NORMAL and texture should reset when mouse is not over it")
  void buttonState_isNormal_whenMouseIsNotOver() {
    button.currentState = UIButtonComponent.ButtonState.HOVERED;
    image.textureHandle = button.hoveredTexture;
    when(mockInputService.getMouseX()).thenReturn(0.0);
    when(mockInputService.getMouseY()).thenReturn(0.0);

    uiSystem.update(0.0f);

    assertThat(button.currentState).isEqualTo(UIButtonComponent.ButtonState.NORMAL);
    assertThat(image.textureHandle).isEqualTo(button.normalTexture);
    verify(mockEventBus, never()).publish(any());
  }
}
