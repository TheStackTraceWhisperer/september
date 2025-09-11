package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovementStatsComponentTest {

    @Test
    @DisplayName("Constructor should correctly initialize the speed property")
    void constructor_initializesSpeed() {
        // Arrange: Define a test speed value.
        float expectedSpeed = 5.5f;

        // Act: Create the component with the speed.
        MovementStatsComponent component = new MovementStatsComponent(expectedSpeed);

        // Assert: Verify that the public speed field is set to the value provided at construction.
        assertEquals(expectedSpeed, component.speed, 0.001f, "The speed should be correctly set.");
    }
}
