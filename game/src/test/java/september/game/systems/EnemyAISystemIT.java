package september.game.systems;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.EngineTestHarness;
import september.engine.core.TimeService;
import september.engine.ecs.components.TransformComponent;
import september.game.components.EnemyComponent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Integration test for the EnemyAISystem.
 * This test uses a real World from the EngineTestHarness but mocks the TimeService
 * to provide predictable time values for verifying movement logic.
 */
@ExtendWith(MockitoExtension.class)
class EnemyAISystemIT extends EngineTestHarness {

    @Mock
    private TimeService timeService;

    private EnemyAISystem enemyAISystem;

    @BeforeEach
    void setupSystem() {
        // The harness provides the 'world'. We create the system under test with it.
        enemyAISystem = new EnemyAISystem(world, timeService);
    }

    @Test
    @DisplayName("update() should move a single enemy based on a sine wave")
    void update_movesSingleEnemy() {
        // --- Arrange ---
        // Create a real enemy entity in the world.
        int enemyId = world.createEntity();
        var transform = new TransformComponent(); // Initial position is (0, 0, 0)
        world.addComponent(enemyId, transform);
        world.addComponent(enemyId, new EnemyComponent());

        // Mock the time service to return a predictable time.
        // Math.sin(PI / 2) = 1, so the enemy should move to its maximum distance.
        double totalTime = Math.PI / 2.0;
        when(timeService.getTotalTime()).thenReturn(totalTime);

        // --- Act ---
        enemyAISystem.update(0.016f);

        // --- Assert ---
        // The travel distance is 3.0f, so sin(PI/2) * 3.0f = 3.0f.
        float expectedX = 3.0f;
        assertThat(transform.position.x).as("Enemy X position should be at the sine wave peak.").isCloseTo(expectedX, org.assertj.core.data.Offset.offset(0.001f));
    }

    @Test
    @DisplayName("update() should move multiple enemies independently")
    void update_movesMultipleEnemies() {
        // --- Arrange ---
        var transform1 = new TransformComponent();
        int enemy1 = world.createEntity();
        world.addComponent(enemy1, transform1);
        world.addComponent(enemy1, new EnemyComponent());

        var transform2 = new TransformComponent();
        int enemy2 = world.createEntity();
        world.addComponent(enemy2, transform2);
        world.addComponent(enemy2, new EnemyComponent());

        // Mock time to move enemies to the trough of the sine wave.
        // Math.sin(3 * PI / 2) = -1.
        double totalTime = (3.0 * Math.PI) / 2.0;
        when(timeService.getTotalTime()).thenReturn(totalTime);

        // --- Act ---
        enemyAISystem.update(0.016f);

        // --- Assert ---
        // Both enemies should be updated to the same X position based on the global time.
        float expectedX = -3.0f;
        assertThat(transform1.position.x).as("Enemy 1 should be at the sine wave trough.").isCloseTo(expectedX, org.assertj.core.data.Offset.offset(0.001f));
        assertThat(transform2.position.x).as("Enemy 2 should be at the sine wave trough.").isCloseTo(expectedX, org.assertj.core.data.Offset.offset(0.001f));
    }

    @Test
    @DisplayName("update() should not affect non-enemy entities")
    void update_doesNothing_whenNoEnemiesExist() {
        // --- Arrange ---
        // Create a non-enemy entity with a transform that could be modified.
        var nonEnemyTransform = new TransformComponent();
        nonEnemyTransform.position.set(100, 100, 100);
        int nonEnemyEntity = world.createEntity();
        world.addComponent(nonEnemyEntity, nonEnemyTransform);

        // Capture its state before the system runs.
        Vector3f positionBeforeUpdate = new Vector3f(nonEnemyTransform.position);

        // Verify our precondition: there are no actual enemies in the world.
        assertThat(world.getEntitiesWith(EnemyComponent.class)).isEmpty();

        // --- Act ---
        // Run the system. It should find no enemies and do nothing.
        enemyAISystem.update(0.016f);

        // --- Assert ---
        // Verify that the non-enemy entity was not touched. This is a much more precise
        // assertion than checking the world's total entity count.
        assertThat(nonEnemyTransform.position).isEqualTo(positionBeforeUpdate);
    }
}
