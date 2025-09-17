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
import static org.assertj.core.api.Assertions.assertThatCode;

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

    @Test
    @DisplayName("System should handle all four directional movements")
    void entityMovesCorrectly_withAllDirections() {
        // Test Up movement
        var controllableUp = new ControllableComponent();
        controllableUp.wantsToMoveUp = true;
        var transformUp = new TransformComponent();
        Vector3f initialPosUp = new Vector3f(transformUp.position);
        createEntity(controllableUp, transformUp, new MovementStatsComponent(100.0f));

        // Test Down movement
        var controllableDown = new ControllableComponent();
        controllableDown.wantsToMoveDown = true;
        var transformDown = new TransformComponent();
        Vector3f initialPosDown = new Vector3f(transformDown.position);
        createEntity(controllableDown, transformDown, new MovementStatsComponent(100.0f));

        // Test Left movement
        var controllableLeft = new ControllableComponent();
        controllableLeft.wantsToMoveLeft = true;
        var transformLeft = new TransformComponent();
        Vector3f initialPosLeft = new Vector3f(transformLeft.position);
        createEntity(controllableLeft, transformLeft, new MovementStatsComponent(100.0f));

        // Test Right movement (already tested above, but included for completeness)
        var controllableRight = new ControllableComponent();
        controllableRight.wantsToMoveRight = true;
        var transformRight = new TransformComponent();
        Vector3f initialPosRight = new Vector3f(transformRight.position);
        createEntity(controllableRight, transformRight, new MovementStatsComponent(100.0f));

        // ACT
        float deltaTime = 0.1f;
        movementSystem.update(deltaTime);

        // ASSERT
        float expectedDistance = 100.0f * deltaTime; // 10.0

        // Up movement (positive Y)
        assertThat(transformUp.position.y()).as("Up movement Y").isCloseTo(initialPosUp.y + expectedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transformUp.position.x()).as("Up movement X").isEqualTo(initialPosUp.x);

        // Down movement (negative Y)
        assertThat(transformDown.position.y()).as("Down movement Y").isCloseTo(initialPosDown.y - expectedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transformDown.position.x()).as("Down movement X").isEqualTo(initialPosDown.x);

        // Left movement (negative X)
        assertThat(transformLeft.position.x()).as("Left movement X").isCloseTo(initialPosLeft.x - expectedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transformLeft.position.y()).as("Left movement Y").isEqualTo(initialPosLeft.y);

        // Right movement (positive X)
        assertThat(transformRight.position.x()).as("Right movement X").isCloseTo(initialPosRight.x + expectedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transformRight.position.y()).as("Right movement Y").isEqualTo(initialPosRight.y);
    }

    @Test
    @DisplayName("System should normalize diagonal movement correctly")
    void entityMovesCorrectly_diagonalNormalization() {
        // Test all possible diagonal combinations
        
        // Up-Right diagonal
        var controllableUR = new ControllableComponent();
        controllableUR.wantsToMoveUp = true;
        controllableUR.wantsToMoveRight = true;
        var transformUR = new TransformComponent();
        Vector3f initialPosUR = new Vector3f(transformUR.position);
        createEntity(controllableUR, transformUR, new MovementStatsComponent(100.0f));

        // Down-Left diagonal
        var controllableDL = new ControllableComponent();
        controllableDL.wantsToMoveDown = true;
        controllableDL.wantsToMoveLeft = true;
        var transformDL = new TransformComponent();
        Vector3f initialPosDL = new Vector3f(transformDL.position);
        createEntity(controllableDL, transformDL, new MovementStatsComponent(100.0f));

        // ACT
        float deltaTime = 0.1f;
        movementSystem.update(deltaTime);

        // ASSERT
        float expectedDistance = 100.0f * deltaTime; // 10.0
        float normalizedDistance = expectedDistance / (float) Math.sqrt(2); // diagonal normalization

        // Up-Right diagonal
        Vector3f movedUR = new Vector3f(transformUR.position).sub(initialPosUR);
        assertThat(movedUR.length()).as("Up-Right diagonal distance").isCloseTo(expectedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(movedUR.x).as("Up-Right X component").isCloseTo(normalizedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(movedUR.y).as("Up-Right Y component").isCloseTo(normalizedDistance, org.assertj.core.data.Offset.offset(0.001f));

        // Down-Left diagonal
        Vector3f movedDL = new Vector3f(transformDL.position).sub(initialPosDL);
        assertThat(movedDL.length()).as("Down-Left diagonal distance").isCloseTo(expectedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(movedDL.x).as("Down-Left X component").isCloseTo(-normalizedDistance, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(movedDL.y).as("Down-Left Y component").isCloseTo(-normalizedDistance, org.assertj.core.data.Offset.offset(0.001f));
    }

    @Test
    @DisplayName("System should handle different movement speeds")
    void entityMovesCorrectly_withDifferentSpeeds() {
        // ARRANGE
        var controllable1 = new ControllableComponent();
        controllable1.wantsToMoveRight = true;
        var transform1 = new TransformComponent();
        Vector3f initialPos1 = new Vector3f(transform1.position);
        createEntity(controllable1, transform1, new MovementStatsComponent(50.0f));

        var controllable2 = new ControllableComponent();
        controllable2.wantsToMoveRight = true;
        var transform2 = new TransformComponent();
        Vector3f initialPos2 = new Vector3f(transform2.position);
        createEntity(controllable2, transform2, new MovementStatsComponent(200.0f));

        // ACT
        float deltaTime = 0.1f;
        movementSystem.update(deltaTime);

        // ASSERT
        float distance1 = 50.0f * deltaTime; // 5.0
        float distance2 = 200.0f * deltaTime; // 20.0

        assertThat(transform1.position.x).as("Slow entity position").isCloseTo(initialPos1.x + distance1, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform2.position.x).as("Fast entity position").isCloseTo(initialPos2.x + distance2, org.assertj.core.data.Offset.offset(0.001f));
    }

    @Test
    @DisplayName("System should handle zero delta time")
    void entityDoesNotMove_withZeroDeltaTime() {
        // ARRANGE
        var controllable = new ControllableComponent();
        controllable.wantsToMoveRight = true;
        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);
        createEntity(controllable, transform, new MovementStatsComponent(100.0f));

        // ACT
        movementSystem.update(0.0f);

        // ASSERT
        assertThat(transform.position).as("Position should not change with zero delta time").isEqualTo(initialPosition);
        assertThat(transform.previousPosition).as("Previous position should still be updated").isEqualTo(initialPosition);
    }

    @Test
    @DisplayName("System should handle very small delta time")
    void entityMovesCorrectly_withSmallDeltaTime() {
        // ARRANGE
        var controllable = new ControllableComponent();
        controllable.wantsToMoveRight = true;
        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);
        createEntity(controllable, transform, new MovementStatsComponent(100.0f));

        // ACT
        float verySmallDeltaTime = 0.001f;
        movementSystem.update(verySmallDeltaTime);

        // ASSERT
        float expectedDistance = 100.0f * verySmallDeltaTime; // 0.1
        Vector3f expectedPosition = new Vector3f(initialPosition).add(expectedDistance, 0, 0);
        
        assertThat(transform.position).as("Position should move correctly with small delta time").usingRecursiveComparison().isEqualTo(expectedPosition);
    }

    @Test
    @DisplayName("System should handle multiple entities independently")
    void multipleEntities_moveIndependently() {
        // ARRANGE
        // Entity 1: Moving up
        var controllable1 = new ControllableComponent();
        controllable1.wantsToMoveUp = true;
        var transform1 = new TransformComponent();
        Vector3f initialPos1 = new Vector3f(transform1.position);
        createEntity(controllable1, transform1, new MovementStatsComponent(100.0f));

        // Entity 2: Moving right
        var controllable2 = new ControllableComponent();
        controllable2.wantsToMoveRight = true;
        var transform2 = new TransformComponent();
        Vector3f initialPos2 = new Vector3f(transform2.position);
        createEntity(controllable2, transform2, new MovementStatsComponent(75.0f));

        // Entity 3: Not moving
        var controllable3 = new ControllableComponent();
        var transform3 = new TransformComponent();
        Vector3f initialPos3 = new Vector3f(transform3.position);
        createEntity(controllable3, transform3, new MovementStatsComponent(50.0f));

        // ACT
        float deltaTime = 0.1f;
        movementSystem.update(deltaTime);

        // ASSERT
        // Entity 1 moves up
        assertThat(transform1.position.y).as("Entity 1 Y position").isCloseTo(initialPos1.y + 10.0f, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform1.position.x).as("Entity 1 X position").isEqualTo(initialPos1.x);

        // Entity 2 moves right
        assertThat(transform2.position.x).as("Entity 2 X position").isCloseTo(initialPos2.x + 7.5f, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform2.position.y).as("Entity 2 Y position").isEqualTo(initialPos2.y);

        // Entity 3 doesn't move
        assertThat(transform3.position).as("Entity 3 position").isEqualTo(initialPos3);
    }

    @Test
    @DisplayName("System should handle entities without required components gracefully")
    void update_handlesIncompleteEntities_gracefully() {
        // ARRANGE
        // Entity with only ControllableComponent
        int entity1 = world.createEntity();
        world.addComponent(entity1, new ControllableComponent());

        // Entity with only TransformComponent
        int entity2 = world.createEntity();
        world.addComponent(entity2, new TransformComponent());

        // Entity with only MovementStatsComponent
        int entity3 = world.createEntity();
        world.addComponent(entity3, new MovementStatsComponent(100.0f));

        // Valid entity for comparison
        var controllable = new ControllableComponent();
        controllable.wantsToMoveRight = true;
        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);
        createEntity(controllable, transform, new MovementStatsComponent(100.0f));

        // ACT & ASSERT
        assertThatCode(() -> movementSystem.update(0.1f))
                .as("MovementSystem should handle incomplete entities gracefully")
                .doesNotThrowAnyException();

        // Verify the valid entity still moves
        assertThat(transform.position.x).as("Valid entity should still move").isGreaterThan(initialPosition.x);
    }

    @Test
    @DisplayName("System should handle empty world gracefully")
    void update_handlesEmptyWorld_gracefully() {
        // ARRANGE - Empty world

        // ACT & ASSERT
        assertThatCode(() -> movementSystem.update(0.1f))
                .as("MovementSystem should handle empty world gracefully")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("System should always update previous position")
    void previousPosition_alwaysUpdated() {
        // ARRANGE
        var controllable = new ControllableComponent();
        // No movement input - entity should not move but previous position should still be updated
        var transform = new TransformComponent();
        Vector3f initialPosition = new Vector3f(transform.position);
        createEntity(controllable, transform, new MovementStatsComponent(100.0f));

        // ACT
        movementSystem.update(0.1f);

        // ASSERT
        assertThat(transform.previousPosition).as("Previous position should be updated even without movement").isEqualTo(initialPosition);
        assertThat(transform.position).as("Current position should remain unchanged").isEqualTo(initialPosition);
    }

    private void createEntity(ControllableComponent controllable, TransformComponent transform, MovementStatsComponent stats) {
        int entityId = world.createEntity();
        world.addComponent(entityId, controllable);
        world.addComponent(entityId, transform);
        world.addComponent(entityId, stats);
    }
}
