package september.engine.systems;

import org.joml.Vector3f;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.MovementStatsComponent;
import september.engine.ecs.components.TransformComponent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the MovementSystem that runs against a live, engine-managed World.
 */
class MovementSystemTest extends EngineTestHarness {

    @Test
    @DisplayName("System should move an entity based on its Controllable and MovementStats components")
    void entityMovesCorrectly_whenControlled() {
        // ARRANGE
        // The harness provides a live world. We create an entity and its components.
        int entityId = world.createEntity();

        // 1. Configure the input component to signal movement.
        var controllable = new ControllableComponent();
        controllable.wantsToMoveUp = true;
        controllable.wantsToMoveLeft = true;

        // 2. Configure the transform and capture its initial state.
        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);

        // 3. Configure movement stats.
        var stats = new MovementStatsComponent(100.0f);

        // 4. Add all components to the entity in the world.
        world.addComponent(entityId, controllable);
        world.addComponent(entityId, transform);
        world.addComponent(entityId, stats);

        // ACT
        // We create a new instance of the system to run it in isolation against the current world state.
        MovementSystem movementSystem = new MovementSystem(world);
        float deltaTime = 0.1f; // Use a fixed delta time for a predictable outcome.
        movementSystem.update(deltaTime);

        // ASSERT
        // The transform's position should have changed from its initial state.
        assertThat(transform.position).isNotEqualTo(initialPosition);

        // The 'previousPosition' should now hold the value of the 'initialPosition'.
        assertThat(transform.previousPosition).isEqualTo(initialPosition);

        // Calculate the expected new position based on the system's logic.
        // Movement is diagonal (up and left), so we normalize the direction vector.
        Vector3f direction = new Vector3f(-1.0f, 1.0f, 0.0f).normalize();
        float distance = stats.speed * deltaTime;
        Vector3f expectedPosition = new Vector3f(initialPosition).add(direction.mul(distance));

        // Verify the new position matches the expected outcome.
        assertThat(transform.position.x()).as("X position").isCloseTo(expectedPosition.x, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform.position.y()).as("Y position").isCloseTo(expectedPosition.y, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform.position.z()).as("Z position").isEqualTo(initialPosition.z);
    }
}
