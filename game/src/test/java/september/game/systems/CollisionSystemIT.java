package september.game.systems;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.ColliderComponent;
import september.engine.ecs.components.TransformComponent;
import september.game.components.GameColliderType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the CollisionSystem running against a live, engine-managed World.
 */
class CollisionSystemIT extends EngineTestHarness {

    private CollisionSystem collisionSystem;

    @BeforeEach
    void setupSystem() {
        collisionSystem = new CollisionSystem(world);
    }

    @Test
    @DisplayName("Player vs. Wall: Player position should revert, Wall position should not")
    void playerVsWall_revertsPlayerPosition() {
        // Arrange
        var playerTransform = createTestTransform(new Vector3f(0, 0, 0), new Vector3f(10, 10, 0));
        var wallTransform = createTestTransform(new Vector3f(12, 12, 0));
        Vector3f wallPositionBeforeUpdate = new Vector3f(wallTransform.position);

        createEntityWithCollider(playerTransform, new ColliderComponent(GameColliderType.PLAYER, 16, 16, 0, 0));
        createEntityWithCollider(wallTransform, new ColliderComponent(GameColliderType.WALL, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position).as("Player position should revert").isEqualTo(playerTransform.previousPosition);
        assertThat(wallTransform.position).as("Wall position should not change").isEqualTo(wallPositionBeforeUpdate);
    }

    @Test
    @DisplayName("Wall vs. Player: Player position should revert, Wall position should not")
    void wallVsPlayer_revertsPlayerPosition() {
        // Arrange
        var playerTransform = createTestTransform(new Vector3f(0, 0, 0), new Vector3f(10, 10, 0));
        var wallTransform = createTestTransform(new Vector3f(12, 12, 0));
        Vector3f wallPositionBeforeUpdate = new Vector3f(wallTransform.position);

        // Create wall first to test the other branch of the collision logic
        createEntityWithCollider(wallTransform, new ColliderComponent(GameColliderType.WALL, 16, 16, 0, 0));
        createEntityWithCollider(playerTransform, new ColliderComponent(GameColliderType.PLAYER, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position).as("Player position should revert").isEqualTo(playerTransform.previousPosition);
        assertThat(wallTransform.position).as("Wall position should not change").isEqualTo(wallPositionBeforeUpdate);
    }

    @Test
    @DisplayName("Enemy vs. Wall: Enemy position should revert, Wall position should not")
    void enemyVsWall_revertsEnemyPosition() {
        // Arrange
        var enemyTransform = createTestTransform(new Vector3f(0, 0, 0), new Vector3f(10, 10, 0));
        var wallTransform = createTestTransform(new Vector3f(12, 12, 0));
        Vector3f wallPositionBeforeUpdate = new Vector3f(wallTransform.position);

        createEntityWithCollider(enemyTransform, new ColliderComponent(GameColliderType.ENEMY, 16, 16, 0, 0));
        createEntityWithCollider(wallTransform, new ColliderComponent(GameColliderType.WALL, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(enemyTransform.position).as("Enemy position should revert").isEqualTo(enemyTransform.previousPosition);
        assertThat(wallTransform.position).as("Wall position should not change").isEqualTo(wallPositionBeforeUpdate);
    }

    @Test
    @DisplayName("Enemy vs. Enemy: Neither entity's position should revert")
    void enemyVsEnemy_doesNotRevertPositions() {
        // Arrange
        var enemy1Transform = createTestTransform(new Vector3f(0, 0, 0), new Vector3f(10, 10, 0));
        var enemy2Transform = createTestTransform(new Vector3f(12, 12, 0));
        Vector3f enemy1PositionBeforeUpdate = new Vector3f(enemy1Transform.position);
        Vector3f enemy2PositionBeforeUpdate = new Vector3f(enemy2Transform.position);

        createEntityWithCollider(enemy1Transform, new ColliderComponent(GameColliderType.ENEMY, 16, 16, 0, 0));
        createEntityWithCollider(enemy2Transform, new ColliderComponent(GameColliderType.ENEMY, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(enemy1Transform.position).as("Enemy 1 position should not revert").isEqualTo(enemy1PositionBeforeUpdate);
        assertThat(enemy2Transform.position).as("Enemy 2 position should not revert").isEqualTo(enemy2PositionBeforeUpdate);
    }

    @Test
    @DisplayName("No Collision: Neither entity's position should change")
    void noCollision_forNonOverlappingEntities() {
        // Arrange
        var playerTransform = createTestTransform(new Vector3f(0, 0, 0), new Vector3f(10, 10, 0));
        var wallTransform = createTestTransform(new Vector3f(100, 100, 0)); // Far away
        Vector3f playerPositionBeforeUpdate = new Vector3f(playerTransform.position);
        Vector3f wallPositionBeforeUpdate = new Vector3f(wallTransform.position);

        createEntityWithCollider(playerTransform, new ColliderComponent(GameColliderType.PLAYER, 16, 16, 0, 0));
        createEntityWithCollider(wallTransform, new ColliderComponent(GameColliderType.WALL, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position).as("Player position should not change").isEqualTo(playerPositionBeforeUpdate);
        assertThat(wallTransform.position).as("Wall position should not change").isEqualTo(wallPositionBeforeUpdate);
    }

    @Test
    @DisplayName("Player vs. Enemy: Neither entity's position should revert")
    void noRevert_forPlayerVsEnemyCollision() {
        // Arrange
        var playerTransform = createTestTransform(new Vector3f(0, 0, 0), new Vector3f(10, 10, 0));
        var enemyTransform = createTestTransform(new Vector3f(12, 12, 0)); // Overlapping
        Vector3f playerPositionBeforeUpdate = new Vector3f(playerTransform.position);
        Vector3f enemyPositionBeforeUpdate = new Vector3f(enemyTransform.position);

        createEntityWithCollider(playerTransform, new ColliderComponent(GameColliderType.PLAYER, 16, 16, 0, 0));
        createEntityWithCollider(enemyTransform, new ColliderComponent(GameColliderType.ENEMY, 16, 16, 0, 0));

        // Act
        collisionSystem.update(0.016f);

        // Assert
        assertThat(playerTransform.position).as("Player position should not revert").isEqualTo(playerPositionBeforeUpdate);
        assertThat(enemyTransform.position).as("Enemy position should not revert").isEqualTo(enemyPositionBeforeUpdate);
    }

    private void createEntityWithCollider(TransformComponent transform, ColliderComponent collider) {
        int entity = world.createEntity();
        world.addComponent(entity, transform);
        world.addComponent(entity, collider);
    }

    private TransformComponent createTestTransform(Vector3f position) {
        var transform = new TransformComponent();
        transform.position.set(position);
        transform.updatePreviousPosition();
        return transform;
    }

    private TransformComponent createTestTransform(Vector3f previousPosition, Vector3f currentPosition) {
        var transform = new TransformComponent();
        transform.previousPosition.set(previousPosition);
        transform.position.set(currentPosition);
        return transform;
    }
}
