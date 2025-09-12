package september.game.systems;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.TransformComponent;
import september.game.components.ColliderComponent;
import september.game.components.ColliderComponent.ColliderType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the CollisionSystem running against a live, engine-managed World.
 */
class CollisionSystemTest extends EngineTestHarness {

    private CollisionSystem collisionSystem;

    @BeforeEach
    void setupSystem() {
        // The harness provides the 'world'. We create the system under test with it.
        collisionSystem = new CollisionSystem(world);
    }

    @Test
    @DisplayName("Player should be reverted to previous position after colliding with a Wall")
    void playerVsWall_revertsPlayerPosition() {
        // Arrange
        var playerTransform = new TransformComponent();
        playerTransform.position.set(0, 0, 0);
        playerTransform.updatePreviousPosition(); // Snapshot position before "movement"
        playerTransform.position.set(10, 10, 0); // Simulate movement into a colliding position

        var wallTransform = new TransformComponent();
        wallTransform.position.set(12, 12, 0); // Positioned to overlap with the player

        createEntityWithCollider(playerTransform, new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0));
        createEntityWithCollider(wallTransform, new ColliderComponent(ColliderType.WALL, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position)
                .as("Player position should be reverted to its previous state")
                .isEqualTo(playerTransform.previousPosition);
    }

    @Test
    @DisplayName("Positions should not be reverted when no collision occurs")
    void noCollision_forNonOverlappingEntities() {
        // Arrange
        var playerTransform = new TransformComponent();
        playerTransform.position.set(0, 0, 0);
        playerTransform.updatePreviousPosition();
        playerTransform.position.set(10, 10, 0);
        Vector3f playerPositionBeforeUpdate = new Vector3f(playerTransform.position);

        var wallTransform = new TransformComponent();
        wallTransform.position.set(100, 100, 0); // Far away, no collision

        createEntityWithCollider(playerTransform, new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0));
        createEntityWithCollider(wallTransform, new ColliderComponent(ColliderType.WALL, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position)
                .as("Player position should not change when there is no collision")
                .isEqualTo(playerPositionBeforeUpdate);
    }

    @Test
    @DisplayName("Positions should not be reverted for Player vs. Enemy collision")
    void noRevert_forPlayerVsEnemyCollision() {
        // Arrange
        var playerTransform = new TransformComponent();
        playerTransform.position.set(0, 0, 0);
        playerTransform.updatePreviousPosition();
        playerTransform.position.set(10, 10, 0);
        Vector3f playerPositionBeforeUpdate = new Vector3f(playerTransform.position);

        var enemyTransform = new TransformComponent();
        enemyTransform.position.set(12, 12, 0); // Overlapping

        createEntityWithCollider(playerTransform, new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0));
        createEntityWithCollider(enemyTransform, new ColliderComponent(ColliderType.ENEMY, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position)
                .as("Player position should not be reverted on collision with an enemy")
                .isEqualTo(playerPositionBeforeUpdate);
    }

    private void createEntityWithCollider(TransformComponent transform, ColliderComponent collider) {
        int entity = world.createEntity();
        world.addComponent(entity, transform);
        world.addComponent(entity, collider);
    }
}
