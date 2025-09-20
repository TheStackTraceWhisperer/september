package september.engine.ecs.components;

import org.joml.Vector4f;
import september.engine.ecs.Component;

import java.util.Objects;

/**
 * A component that defines the visual appearance of a 2D sprite.
 * It holds a handle to the texture to be rendered and an optional color tint.
 * This is a record, which is an immutable data carrier.
 */
public record SpriteComponent(String textureHandle, Vector4f color) implements Component {

  /**
   * A compact constructor for the record.
   * This is automatically called by the canonical constructor.
   * It ensures that if the color is not provided during deserialization (i.e., it's null),
   * it defaults to white.
   */
  public SpriteComponent {
    Objects.requireNonNull(textureHandle, "textureHandle must not be null");
    if (color == null) {
      color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
  }
}
