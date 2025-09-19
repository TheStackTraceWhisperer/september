package september.engine.ui.components;

import org.joml.Vector4f;
import september.engine.ecs.Component;

/**
 * Renders a simple, non-interactive texture in the UI.
 * This is the UI equivalent of the world-space SpriteComponent.
 */
public final class UIImageComponent implements Component {

  /**
   * The handle to the texture asset that should be drawn for this UI image.
   */
  public final String textureHandle;

  /**
   * A color tint to apply to the image. Default is white (no tint).
   */
  public final Vector4f color;

  // Constructor for deserialization from scene files will be needed.
  public UIImageComponent(String textureHandle, Vector4f color) {
    this.textureHandle = textureHandle;
    this.color = color != null ? color : new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
  }
}
