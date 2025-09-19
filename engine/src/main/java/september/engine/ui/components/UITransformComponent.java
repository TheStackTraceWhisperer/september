package september.engine.ui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Vector2f;
import september.engine.ecs.Component;

/**
 * Defines the position, size, and layout of a UI element in screen space.
 */
public final class UITransformComponent implements Component {

  /**
   * The anchor point on the PARENT element that this element attaches to.
   * (0, 0) is the bottom-left corner of the parent, (1, 1) is the top-right.
   */
  public final Vector2f anchor;

  /**
   * The pivot point on THIS element that aligns with the anchor point.
   * (0, 0) is the bottom-left corner of this element, (1, 1) is the top-right.
   */
  public final Vector2f pivot;

  /**
   * The absolute pixel size of the UI element.
   */
  public final Vector2f size;

  /**
   * An additional pixel offset applied after anchor/pivot calculations.
   */
  public final Vector2f offset;

  // --- Fields Calculated by the UISystem ---

  /**
   * The final, calculated screen-space rectangular bounds (minX, minY, maxX, maxY).
   * This field is mutated by the UISystem each frame.
   */
  public final float[] screenBounds = new float[4];

  public UITransformComponent() {
    this.anchor = new Vector2f(0.5f, 0.5f);
    this.pivot = new Vector2f(0.5f, 0.5f);
    this.size = new Vector2f(100, 30);
    this.offset = new Vector2f(0, 0);
  }

  @JsonCreator
  public UITransformComponent(
    @JsonProperty("anchor") Vector2f anchor,
    @JsonProperty("pivot") Vector2f pivot,
    @JsonProperty("size") Vector2f size,
    @JsonProperty("offset") Vector2f offset) {
    this.anchor = anchor != null ? anchor : new Vector2f(0.5f, 0.5f);
    this.pivot = pivot != null ? pivot : new Vector2f(0.5f, 0.5f);
    this.size = size != null ? size : new Vector2f(100, 30);
    this.offset = offset != null ? offset : new Vector2f(0, 0);
  }
}
