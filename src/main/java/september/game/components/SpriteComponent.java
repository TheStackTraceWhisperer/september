package september.game.components;

import org.joml.Vector4f;
import september.engine.ecs.Component;

/**
 * A component that defines the visual appearance of a 2D sprite.
 * It holds a handle to the texture to be rendered and an optional color tint.
 */
public class SpriteComponent implements Component {
  /**
   * The handle to the texture asset that should be drawn for this sprite.
   */
  public String textureHandle;

  /**
   * A color tint to apply to the sprite. Default is white (no tint).
   */
  public Vector4f color;

  public SpriteComponent(String textureHandle) {
    this.textureHandle = textureHandle;
    this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f); // Default to white
  }

  public SpriteComponent(String textureHandle, Vector4f color) {
    this.textureHandle = textureHandle;
    this.color = color;
  }
}
