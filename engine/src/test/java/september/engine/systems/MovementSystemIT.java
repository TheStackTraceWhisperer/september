package september.engine.systems;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
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
class MovementSystemIT extends EngineTestHarness {

    private MovementSystem movementSystem;

    @BeforeEach
    void setupSystem() {
        movementSystem = new MovementSystem(world);
    }

    @Test
    @DisplayName("System should move an entity correctly with diagonal input")
    void entityMovesCorrectly_withDiagonalInput() {
        // ARRANGE
        var controllable = new ControllableComponent();
        controllable.wantsToMoveUp = true;
        controllable.wantsToMoveLeft = true;

        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);

        var stats = new MovementStatsComponent(100.0f);
        createEntity(controllable, transform, stats);

        // ACT
        float deltaTime = 0.1f;
        movementSystem.update(deltaTime);

        // ASSERT
        assertThat(transform.position).isNotEqualTo(initialPosition);
        assertThat(transform.previousPosition).isEqualTo(initialPosition);

        Vector3f direction = new Vector3f(-1.0f, 1.0f, 0.0f).normalize();
        float distance = stats.speed * deltaTime;
        Vector3f expectedPosition = new Vector3f(initialPosition).add(direction.mul(distance));

        assertThat(transform.position.x()).as("X position").isCloseTo(expectedPosition.x, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform.position.y()).as("Y position").isCloseTo(expectedPosition.y, org.assertj.core.data.Offset.offset(0.001f));
    }

    @Test
    @DisplayName("System should move an entity correctly with straight-line input")
    void entityMovesCorrectly_withStraightLineInput() {
        // ARRANGE
        var controllable = new ControllableComponent();
        controllable.wantsToMoveRight = true;

        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);

        var stats = new MovementStatsComponent(50.0f);
        createEntity(controllable, transform, stats);

        // ACT
        float deltaTime = 0.1f;
        movementSystem.update(deltaTime);

        // ASSERT
        assertThat(transform.position).isNotEqualTo(initialPosition);
        assertThat(transform.previousPosition).isEqualTo(initialPosition);

        float distance = stats.speed * deltaTime; // 50.0 * 0.1 = 5.0
        Vector3f expectedPosition = new Vector3f(initialPosition).add(distance, 0, 0);

        assertThat(transform.position).usingRecursiveComparison().isEqualTo(expectedPosition);
    }

    @Test
    @DisplayName("System should not move an entity when there is no input")
    void entityDoesNotMove_whenNoInput() {
        // ARRANGE
        var controllable = new ControllableComponent(); // All flags are false by default
        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);

        var stats = new MovementStatsComponent(100.0f);
        createEntity(controllable, transform, stats);

        // ACT
        movementSystem.update(0.1f);

        // ASSERT
        // Previous position is still updated, but current position should not change.
        assertThat(transform.previousPosition).isEqualTo(initialPosition);
        assertThat(transform.position).isEqualTo(initialPosition);
    }

    private void createEntity(ControllableComponent controllable, TransformComponent transform, MovementStatsComponent stats) {
        int entityId = world.createEntity();
        world.addComponent(entityId, controllable);
        world.addComponent(entityId, transform);
        world.addComponent(entityId, stats);
    }
}
