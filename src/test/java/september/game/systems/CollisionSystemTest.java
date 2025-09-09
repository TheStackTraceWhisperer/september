package september.game.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.TransformComponent;
import september.game.components.ColliderComponent;
import september.game.components.ColliderComponent.ColliderType;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollisionSystemTest {

    @Mock
    private IWorld world;

    private CollisionSystem collisionSystem;

    @BeforeEach
    void setUp() {
        // Correctly instantiate the system by passing the mocked IWorld dependency
        collisionSystem = new CollisionSystem(world);
    }

    @Test
    void playerVsWall_revertsPlayerPosition() {
        // Arrange
        var playerTransform = spy(new TransformComponent());
        var wallTransform = new TransformComponent();
        var playerCollider = new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0);
        var wallCollider = new ColliderComponent(ColliderType.WALL, 16, 16, 0, 0);

        playerTransform.position.set(10, 10, 0);
        wallTransform.position.set(12, 12, 0);

        setupEntities(playerTransform, playerCollider, wallTransform, wallCollider);

        // Act: Call the update method with the correct signature
        collisionSystem.update(0.016f);

        // Assert
        verify(playerTransform).revertPosition();
    }

    @Test
    void wallVsPlayer_revertsPlayerPosition() {
        // Arrange
        var playerTransform = spy(new TransformComponent());
        var wallTransform = new TransformComponent();
        var playerCollider = new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0);
        var wallCollider = new ColliderComponent(ColliderType.WALL, 16, 16, 0, 0);

        playerTransform.position.set(10, 10, 0);
        wallTransform.position.set(12, 12, 0);

        setupEntities(wallTransform, wallCollider, playerTransform, playerCollider);

        // Act: Call the update method with the correct signature
        collisionSystem.update(0.016f);

        // Assert
        verify(playerTransform).revertPosition();
    }

    @Test
    void noCollision_forNonOverlappingEntities() {
        // Arrange
        var playerTransform = spy(new TransformComponent());
        var wallTransform = spy(new TransformComponent());
        var playerCollider = new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0);
        var wallCollider = new ColliderComponent(ColliderType.WALL, 16, 16, 0, 0);

        playerTransform.position.set(100, 100, 0);
        wallTransform.position.set(200, 200, 0);

        setupEntities(playerTransform, playerCollider, wallTransform, wallCollider);

        // Act: Call the update method with the correct signature
        collisionSystem.update(0.016f);

        // Assert
        verify(playerTransform, never()).revertPosition();
        verify(wallTransform, never()).revertPosition();
    }

    @Test
    void noRevert_forPlayerVsEnemyCollision() {
        // Arrange
        var playerTransform = spy(new TransformComponent());
        var enemyTransform = spy(new TransformComponent());
        var playerCollider = new ColliderComponent(ColliderType.PLAYER, 16, 16, 0, 0);
        var enemyCollider = new ColliderComponent(ColliderType.ENEMY, 16, 16, 0, 0);

        playerTransform.position.set(10, 10, 0);
        enemyTransform.position.set(12, 12, 0);

        setupEntities(playerTransform, playerCollider, enemyTransform, enemyCollider);

        // Act: Call the update method with the correct signature
        collisionSystem.update(0.016f);

        // Assert
        verify(playerTransform, never()).revertPosition();
        verify(enemyTransform, never()).revertPosition();
    }

    private void setupEntities(TransformComponent transformA, ColliderComponent colliderA, TransformComponent transformB, ColliderComponent colliderB) {
        int entityA = 1;
        int entityB = 2;

        when(world.getEntitiesWith(TransformComponent.class, ColliderComponent.class)).thenReturn(List.of(entityA, entityB));

        when(world.getComponent(entityA, TransformComponent.class)).thenReturn(transformA);
        when(world.getComponent(entityA, ColliderComponent.class)).thenReturn(colliderA);

        when(world.getComponent(entityB, TransformComponent.class)).thenReturn(transformB);
        when(world.getComponent(entityB, ColliderComponent.class)).thenReturn(colliderB);
    }
}
