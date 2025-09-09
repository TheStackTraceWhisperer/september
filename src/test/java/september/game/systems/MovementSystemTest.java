package september.game.systems;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.ControllableComponent;
import september.engine.ecs.components.TransformComponent;
import september.game.components.MovementStatsComponent;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementSystemTest {

    @Mock
    private IWorld world;
    @Mock
    private ControllableComponent controllable;
    @Mock
    private MovementStatsComponent stats;

    // The transform will be a spy so we can verify calls on a real object
    private TransformComponent transform;

    private MovementSystem movementSystem;

    @BeforeEach
    void setUp() {
        transform = spy(new TransformComponent());
        movementSystem = new MovementSystem(world);
    }

    @Test
    void update_callsUpdatePreviousPosition() {
        // Arrange
        int entityId = 1;
        when(world.getEntitiesWith(ControllableComponent.class, TransformComponent.class, MovementStatsComponent.class))
                .thenReturn(List.of(entityId));
        when(world.getComponent(entityId, ControllableComponent.class)).thenReturn(controllable);
        when(world.getComponent(entityId, TransformComponent.class)).thenReturn(transform);
        when(world.getComponent(entityId, MovementStatsComponent.class)).thenReturn(stats);

        // Act
        movementSystem.update(0.016f);

        // Assert
        // Verify that the crucial snapshot method was called.
        verify(transform).updatePreviousPosition();
    }
}
