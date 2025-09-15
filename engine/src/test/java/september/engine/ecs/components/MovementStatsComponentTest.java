package september.engine.ecs.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovementStatsComponentTest {

    @Test
    @DisplayName("Constructor should correctly initialize the speed property")
    void constructor_initializesSpeed() {
        // Arrange: Define a test speed value.
        float expectedSpeed = 5.5f;

        // Act: Create the component with the speed.
        MovementStatsComponent component = new MovementStatsComponent(expectedSpeed);

        // Assert: Verify that the public speed field is set to the value provided at construction.
        assertThat(component.speed).isEqualTo(expectedSpeed);
    }
}
