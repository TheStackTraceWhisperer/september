package september.game.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.ControllableComponent;
import september.game.components.PlayerComponent;
import september.game.input.GameAction;
import september.game.input.InputMappingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Integration test for the PlayerInputSystem.
 * This test uses a real World from the EngineTestHarness but mocks the InputMappingService
 * to provide controlled, simulated player input.
 */
@ExtendWith(MockitoExtension.class)
class PlayerInputSystemIT extends EngineTestHarness {

    @Mock
    private InputMappingService mappingService;

    private PlayerInputSystem playerInputSystem;
    private ControllableComponent controllable;
    private final int playerId = 0;

    @BeforeEach
    void setupSystemAndPlayer() {
        // The harness has already run its setup, providing the 'world'.
        // We now perform additional setup specific to this test class.
        playerInputSystem = new PlayerInputSystem(world, mappingService);

        // Create a real entity in the world that the system can operate on.
        int playerEntity = world.createEntity();
        controllable = new ControllableComponent();
        world.addComponent(playerEntity, controllable);
        // The system specifically looks for entities with PlayerComponent, so we must add it.
        world.addComponent(playerEntity, new PlayerComponent());
    }

    @Test
    @DisplayName("System should set movement flags when move actions are active")
    void setsMovementFlags_whenMoveActionsAreActive() {
        // Arrange: Stub the input service to simulate active actions.
        when(mappingService.isActionActive(playerId, GameAction.MOVE_UP)).thenReturn(true);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_DOWN)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_LEFT)).thenReturn(true);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.ATTACK)).thenReturn(false);

        // Act: The system will now query the real world, find our real entity,
        // and update the real ControllableComponent.
        playerInputSystem.update(0.016f);

        // Assert: Check the state of the real component.
        assertThat(controllable.wantsToMoveUp).isTrue();
        assertThat(controllable.wantsToMoveDown).isFalse();
        assertThat(controllable.wantsToMoveLeft).isTrue();
        assertThat(controllable.wantsToMoveRight).isFalse();
        assertThat(controllable.wantsToAttack).isFalse();
    }

    @Test
    @DisplayName("System should set attack flag when attack action is active")
    void setsAttackFlag_whenAttackActionIsActive() {
        // Arrange: Stub the input service.
        when(mappingService.isActionActive(playerId, GameAction.MOVE_UP)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_DOWN)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_LEFT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.ATTACK)).thenReturn(true);

        // Act
        playerInputSystem.update(0.016f);

        // Assert
        assertThat(controllable.wantsToAttack).isTrue();
        assertThat(controllable.wantsToMoveUp).as("Movement flags should not be affected").isFalse();
    }

    @Test
    @DisplayName("System should clear all flags when no actions are active")
    void clearsFlags_whenActionsAreInactive() {
        // Arrange: Set some flags to true initially to ensure the system clears them.
        controllable.wantsToMoveUp = true;
        controllable.wantsToAttack = true;

        // Stub the input service to report all actions as inactive.
        when(mappingService.isActionActive(playerId, GameAction.MOVE_UP)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_DOWN)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_LEFT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.MOVE_RIGHT)).thenReturn(false);
        when(mappingService.isActionActive(playerId, GameAction.ATTACK)).thenReturn(false);

        // Act
        playerInputSystem.update(0.016f);

        // Assert
        assertThat(controllable.wantsToMoveUp).isFalse();
        assertThat(controllable.wantsToMoveDown).isFalse();
        assertThat(controllable.wantsToMoveLeft).isFalse();
        assertThat(controllable.wantsToMoveRight).isFalse();
        assertThat(controllable.wantsToAttack).isFalse();
    }
}
