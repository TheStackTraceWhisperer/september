package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColliderComponentTest {

    @Test
    @DisplayName("Constructor should correctly initialize all collider properties")
    void constructor_initializesAllProperties() {
        // Arrange: Define the properties for a test collider.
        ColliderComponent.ColliderType expectedType = ColliderComponent.ColliderType.PLAYER;
        int expectedWidth = 32;
        int expectedHeight = 48;
        int expectedOffsetX = -4;
        int expectedOffsetY = 0;

        // Act: Create the component using the @AllArgsConstructor.
        ColliderComponent component = new ColliderComponent(expectedType, expectedWidth, expectedHeight, expectedOffsetX, expectedOffsetY);

        // Assert: Verify that each getter returns the value provided at construction.
        assertAll("Collider properties verification",
                () -> assertEquals(expectedType, component.getType(), "The collider type should be correctly set."),
                () -> assertEquals(expectedWidth, component.getWidth(), "The width should be correctly set."),
                () -> assertEquals(expectedHeight, component.getHeight(), "The height should be correctly set."),
                () -> assertEquals(expectedOffsetX, component.getOffsetX(), "The X offset should be correctly set."),
                () -> assertEquals(expectedOffsetY, component.getOffsetY(), "The Y offset should be correctly set.")
        );
    }
}
