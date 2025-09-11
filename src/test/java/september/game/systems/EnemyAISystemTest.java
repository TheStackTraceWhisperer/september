package september.game.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.TimeService;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.TransformComponent;
import september.game.components.EnemyComponent;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnemyAISystemTest {

    @Mock
    private IWorld world;
    @Mock
    private TimeService timeService;

    private EnemyAISystem enemyAISystem;

    @BeforeEach
    void setUp() {
        enemyAISystem = new EnemyAISystem(world, timeService);
    }

    @Test
    @DisplayName("update() should move a single enemy based on a sine wave")
    void update_movesSingleEnemy() {
        // --- Arrange ---
        int enemyId = 1;
        TransformComponent transform = new TransformComponent(); // Initial position is (0, 0, 0)

        // Mock the world to return our single enemy entity.
        when(world.getEntitiesWith(EnemyComponent.class, TransformComponent.class)).thenReturn(List.of(enemyId));
        when(world.getComponent(enemyId, TransformComponent.class)).thenReturn(transform);

        // Mock the time service to return a predictable time.
        // Math.sin(PI / 2) = 1, so the enemy should move to its maximum distance.
        double totalTime = Math.PI / 2.0;
        when(timeService.getTotalTime()).thenReturn(totalTime);

        // --- Act ---
        enemyAISystem.update(0.016f);

        // --- Assert ---
        // The travel distance is 3.0f, so sin(PI/2) * 3.0f = 3.0f.
        float expectedX = 3.0f;
        assertEquals(expectedX, transform.position.x, 0.001f, "Enemy X position should be at the sine wave peak.");
    }

    @Test
    @DisplayName("update() should move multiple enemies independently")
    void update_movesMultipleEnemies() {
        // --- Arrange ---
        int enemy1 = 1;
        int enemy2 = 2;
        TransformComponent transform1 = new TransformComponent();
        TransformComponent transform2 = new TransformComponent();

        when(world.getEntitiesWith(EnemyComponent.class, TransformComponent.class)).thenReturn(List.of(enemy1, enemy2));
        when(world.getComponent(enemy1, TransformComponent.class)).thenReturn(transform1);
        when(world.getComponent(enemy2, TransformComponent.class)).thenReturn(transform2);

        // Mock time to move enemies to the trough of the sine wave.
        // Math.sin(3 * PI / 2) = -1.
        double totalTime = (3.0 * Math.PI) / 2.0;
        when(timeService.getTotalTime()).thenReturn(totalTime);

        // --- Act ---
        enemyAISystem.update(0.016f);

        // --- Assert ---
        // Both enemies should be updated to the same X position based on the global time.
        float expectedX = -3.0f;
        assertEquals(expectedX, transform1.position.x, 0.001f, "Enemy 1 should be at the sine wave trough.");
        assertEquals(expectedX, transform2.position.x, 0.001f, "Enemy 2 should be at the sine wave trough.");
    }

    @Test
    @DisplayName("update() should do nothing if no enemies exist")
    void update_doesNothing_whenNoEnemiesExist() {
        // --- Arrange ---
        // Mock the world to return an empty list of entities.
        when(world.getEntitiesWith(EnemyComponent.class, TransformComponent.class)).thenReturn(Collections.emptyList());

        // --- Act ---
        enemyAISystem.update(0.016f);

        // --- Assert ---
        // Verify that no components were ever requested from the world, as there were no entities to process.
        verify(world, never()).getComponent(anyInt(), any());
    }
}
