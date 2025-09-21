package september.engine.ui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Vector2f;
import org.joml.Vector3f;
import september.engine.ecs.Component;

/**
 * Represents the position, rotation, and scale of a UI entity in screen space. This component is
 * used by the UISystem to calculate the final layout.
 */
public class UITransformComponent implements Component {

  // --- Layout Properties ---

  /** The point on the parent element that this element will attach to (0,0 to 1,1). */
  public final Vector2f anchor;

  /** The point on this element that will be placed at the anchor point (0,0 to 1,1). */
  public final Vector2f pivot;

  /** The size of the element. Can be absolute (pixels) or relative (percentage). */
  public final Vector2f size;

  /**
   * A fine-tuning offset in pixels, applied after anchor and pivot calculations. Z is used for
   * depth.
   */
  public final Vector3f offset;

  /** If true, the `size` vector is interpreted as a percentage (0.0-1.0) of the parent size. */
  public boolean relativeSize = false;

  // --- Calculated Properties ---

  /**
   * The final calculated screen-space bounds [minX, minY, maxX, maxY]. Managed by the UISystem.
   */
  @JsonIgnore public final float[] screenBounds = new float[4];

  public UITransformComponent() {
    this.anchor = new Vector2f(0.5f, 0.5f);
    this.pivot = new Vector2f(0.5f, 0.5f);
    this.size = new Vector2f(100.0f, 100.0f);
    this.offset = new Vector3f(0.0f, 0.0f, 0.0f);
  }

  @JsonCreator
  public UITransformComponent(
      @JsonProperty("anchor") Vector2f anchor,
      @JsonProperty("pivot") Vector2f pivot,
      @JsonProperty("size") Vector2f size,
      @JsonProperty("offset") Vector3f offset) {
    this.anchor = anchor != null ? anchor : new Vector2f(0.5f, 0.5f);
    this.pivot = pivot != null ? pivot : new Vector2f(0.5f, 0.5f);
    this.size = size != null ? size : new Vector2f(100, 30);
    this.offset = offset != null ? offset : new Vector3f(0, 0, 0);
  }
}
