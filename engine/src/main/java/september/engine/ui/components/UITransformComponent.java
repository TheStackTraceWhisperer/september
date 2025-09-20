package september.engine.ui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Vector2f;
import org.joml.Vector3f;
import september.engine.ecs.Component;

/**
 * Defines the position, size, and layout of a UI element in screen space.
 */
public final class UITransformComponent implements Component {

  public final Vector2f anchor;
  public final Vector2f pivot;
  public final Vector2f size;
  // Use a Vector3f for offset to include a Z-coordinate for depth.
  public final Vector3f offset;

  // This is calculated by the UISystem and does not need to be in the constructor.
  public final float[] screenBounds = new float[4];

  public UITransformComponent() {
    this.anchor = new Vector2f(0.5f, 0.5f);
    this.pivot = new Vector2f(0.5f, 0.5f);
    this.size = new Vector2f(100, 30);
    this.offset = new Vector3f(0, 0, 0);
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
