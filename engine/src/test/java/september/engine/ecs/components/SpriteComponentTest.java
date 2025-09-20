package september.engine.ecs.components;

import org.joml.Vector4f;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpriteComponentTest {

  @Test
  @DisplayName("Constructor with handle should set handle and default to white color")
  void constructor_withHandle_setsDefaultColor() {
    // Arrange
    String expectedHandle = "player_sprite";

    // Act
    // We now test the compact constructor's logic by passing null for the color.
    SpriteComponent component = new SpriteComponent(expectedHandle, null);

    // Assert
    // Use accessor methods for records
    assertThat(component.textureHandle()).as("Texture handle").isEqualTo(expectedHandle);
    assertThat(component.color()).as("Default color").isEqualTo(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
  }

  @Test
  @DisplayName("Constructor with handle and color should set both properties correctly")
  void constructor_withHandleAndColor_setsAllProperties() {
    // Arrange
    String expectedHandle = "enemy_sprite";
    Vector4f expectedColor = new Vector4f(0.8f, 0.2f, 0.2f, 1.0f); // A reddish tint

    // Act
    SpriteComponent component = new SpriteComponent(expectedHandle, expectedColor);

    // Assert
    // Use accessor methods for records
    assertThat(component.textureHandle()).as("Texture handle").isEqualTo(expectedHandle);
    assertThat(component.color()).as("Custom color").isEqualTo(expectedColor);
  }
}
