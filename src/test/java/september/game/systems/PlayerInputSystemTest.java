package september.game.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.ControllableComponent;
import september.game.input.GameAction;
import september.game.input.InputMappingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerInputSystemTest {

    @Mock
    private IWorld world;
    @Mock
    private InputMappingService mappingService;

    private PlayerInputSystem playerInputSystem;
    private ControllableComponent controllable;
    private final int playerId = 0;
    private final int entityId = 1;

    @BeforeEach
    void setUp() {
        playerInputSystem = new PlayerInputSystem(world, mappingService);
        controllable = new ControllableComponent();

        when(world.getEntitiesWith(ControllableComponent.class)).thenReturn(List.of(entityId));
        when(world.getComponent(entityId, ControllableComponent.class)).thenReturn(controllable);
    }

    @Test
    void setsMovementFlags_whenMoveActionsAreActive() {
        // Arrange: Stub ALL actions the system will query to satisfy strict stubbing.
        when(mappingService.isActionActive(playerId, GameAction.MOVE_UP)).thenReturn(true);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_DOWN)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_LEFT)).thenReturn(true);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.ATTACK)).thenReturn(false);

        // Act
        playerInputSystem.update(0.016f);

        // Assert
        assertTrue(controllable.wantsToMoveUp);
        assertFalse(controllable.wantsToMoveDown);
        assertTrue(controllable.wantsToMoveLeft);
        assertFalse(controllable.wantsToMoveRight);
        assertFalse(controllable.wantsToAttack);
    }

    @Test
    void setsAttackFlag_whenAttackActionIsActive() {
        // Arrange: Stub ALL actions the system will query.
        when(mappingService.isActionActive(playerId, GameAction.MOVE_UP)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_DOWN)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_LEFT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.ATTACK)).thenReturn(true);

        // Act
        playerInputSystem.update(0.016f);

        // Assert
        assertTrue(controllable.wantsToAttack);
        assertFalse(controllable.wantsToMoveUp, "Movement flags should not be affected");
    }

    @Test
    void clearsFlags_whenActionsAreInactive() {
        // Arrange: Explicitly stub ALL actions as inactive.
        when(mappingService.isActionActive(playerId, GameAction.MOVE_UP)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_DOWN)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_LEFT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.ATTACK)).thenReturn(false);

        // Act
        playerInputSystem.update(0.016f);

        // Assert
        assertFalse(controllable.wantsToMoveUp);
        assertFalse(controllable.wantsToMoveDown);
        assertFalse(controllable.wantsToMoveLeft);
        assertFalse(controllable.wantsToMoveRight);
        assertFalse(controllable.wantsToAttack);
    }
}
