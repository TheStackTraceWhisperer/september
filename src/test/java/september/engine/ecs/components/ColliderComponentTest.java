package september.engine.ecs.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(component.getType()).as("Collider type").isEqualTo(expectedType);
        assertThat(component.getWidth()).as("Width").isEqualTo(expectedWidth);
        assertThat(component.getHeight()).as("Height").isEqualTo(expectedHeight);
        assertThat(component.getOffsetX()).as("X offset").isEqualTo(expectedOffsetX);
        assertThat(component.getOffsetY()).as("Y offset").isEqualTo(expectedOffsetY);
    }
}
