package september.engine.ui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Vector4f;
import september.engine.ecs.Component;

/**
 * Renders a simple, non-interactive texture in the UI.
 * This is the UI equivalent of the world-space SpriteComponent.
 */
public class UIImageComponent implements Component { // Removed final

  /**
   * The handle to the texture asset that should be drawn for this UI image.
   */
  public String textureHandle; // Removed final

  /**
   * A color tint to apply to the image. Default is white (no tint).
   */
  public final Vector4f color;

  @JsonCreator
  public UIImageComponent(
    @JsonProperty("textureHandle") String textureHandle,
    @JsonProperty("color") Vector4f color
  ) {
    this.textureHandle = textureHandle;
    this.color = color != null ? color : new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
  }
}
