package september.engine.ui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Vector4f;
import september.engine.ecs.Component;

/**
 * Defines an interactive button element in the UI.
 * The UISystem will use this data to handle input, manage visual state transitions,
 * and trigger events.
 */
public final class UIButtonComponent implements Component {

  /**
   * The event type to publish when this button is clicked. This is a string
   * that the game's event handlers will listen for.
   * Example: "START_NEW_GAME", "OPEN_SETTINGS_MENU"
   */
  public final String actionEvent;

  // --- Visual State Properties ---

  public final String normalTexture;
  public final String hoveredTexture;
  public final String pressedTexture;

  public final Vector4f normalColor;
  public final Vector4f hoveredColor;
  public final Vector4f pressedColor;

  // --- Internal State Fields (mutated by UISystem) ---

  public enum ButtonState {
    NORMAL,
    HOVERED,
    PRESSED
  }

  /**
   * The current interactive state of the button. This is managed by the
   * UISystem and should not be modified manually. It is marked 'transient'
   * to indicate it's a runtime-only property.
   */
  public transient ButtonState currentState = ButtonState.NORMAL;

  @JsonCreator
  public UIButtonComponent(
    @JsonProperty("actionEvent") String actionEvent,
    @JsonProperty("normalTexture") String normalTexture,
    @JsonProperty("hoveredTexture") String hoveredTexture,
    @JsonProperty("pressedTexture") String pressedTexture,
    @JsonProperty("normalColor") Vector4f normalColor,
    @JsonProperty("hoveredColor") Vector4f hoveredColor,
    @JsonProperty("pressedColor") Vector4f pressedColor) {
    this.actionEvent = actionEvent;
    this.normalTexture = normalTexture;
    this.hoveredTexture = hoveredTexture;
    this.pressedTexture = pressedTexture;
    this.normalColor = normalColor != null ? normalColor : new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    this.hoveredColor = hoveredColor != null ? hoveredColor : new Vector4f(0.9f, 0.9f, 0.9f, 1.0f);
    this.pressedColor = pressedColor != null ? pressedColor : new Vector4f(0.7f, 0.7f, 0.7f, 1.0f);
  }
}
