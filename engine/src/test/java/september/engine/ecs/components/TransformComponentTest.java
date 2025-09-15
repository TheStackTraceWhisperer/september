package september.engine.ecs.components;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransformComponentTest {

    private TransformComponent transform;

    @BeforeEach
    void setUp() {
        transform = new TransformComponent();
    }

    @Test
    void updatePreviousPosition_copiesCurrentPosition() {
        // Arrange
        Vector3f newPosition = new Vector3f(10f, 20f, 30f);
        transform.position.set(newPosition);

        // Act
        transform.updatePreviousPosition();

        // Assert
        assertThat(transform.previousPosition)
                .as("Previous position should match the new position after update.")
                .isEqualTo(newPosition);
        assertThat(transform.previousPosition)
                .as("Previous position should be equal to the current position.")
                .isEqualTo(transform.position);
    }

    @Test
    void revertPosition_restoresPositionFromPrevious() {
        // Arrange
        Vector3f initialPosition = new Vector3f(10f, 20f, 30f);
        transform.position.set(initialPosition);
        transform.updatePreviousPosition(); // Snapshot the initial position

        Vector3f newPosition = new Vector3f(40f, 50f, 60f);
        transform.position.set(newPosition);
        assertThat(transform.position)
                .as("Position should have changed before reverting.")
                .isNotEqualTo(initialPosition);

        // Act
        transform.revertPosition();

        // Assert
        assertThat(transform.position)
                .as("Position should be reverted to the initial state.")
                .isEqualTo(initialPosition);
    }
}
