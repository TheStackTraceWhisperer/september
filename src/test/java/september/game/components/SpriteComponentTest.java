package september.game.components;

import org.joml.Vector4f;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpriteComponentTest {

    @Test
    @DisplayName("Constructor with handle should set handle and default to white color")
    void constructor_withHandle_setsDefaultColor() {
        // Arrange
        String expectedHandle = "player_sprite";

        // Act
        SpriteComponent component = new SpriteComponent(expectedHandle);

        // Assert
        assertAll("Default sprite component state",
                () -> assertEquals(expectedHandle, component.textureHandle, "The texture handle should be set correctly."),
                () -> assertEquals(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), component.color, "The color should default to white.")
        );
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
        assertAll("Custom sprite component state",
                () -> assertEquals(expectedHandle, component.textureHandle, "The texture handle should be set correctly."),
                () -> assertEquals(expectedColor, component.color, "The custom color should be set correctly.")
        );
    }
}
