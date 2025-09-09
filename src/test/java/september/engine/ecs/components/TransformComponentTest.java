package september.engine.ecs.components;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
        assertEquals(newPosition, transform.previousPosition, "Previous position should match the new position after update.");
        assertEquals(transform.position, transform.previousPosition, "Previous position should be equal to the current position.");
    }

    @Test
    void revertPosition_restoresPositionFromPrevious() {
        // Arrange
        Vector3f initialPosition = new Vector3f(10f, 20f, 30f);
        transform.position.set(initialPosition);
        transform.updatePreviousPosition(); // Snapshot the initial position

        Vector3f newPosition = new Vector3f(40f, 50f, 60f);
        transform.position.set(newPosition);
        assertNotEquals(initialPosition, transform.position, "Position should have changed before reverting.");

        // Act
        transform.revertPosition();

        // Assert
        assertEquals(initialPosition, transform.position, "Position should be reverted to the initial state.");
    }
}
