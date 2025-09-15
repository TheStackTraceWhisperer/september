package september.game.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.input.InputService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.when;

/**
 * Unit test for the KeyboardMappingService.
 * This test verifies that the service correctly maps raw keyboard inputs to game actions.
 */
@ExtendWith(MockitoExtension.class)
class KeyboardMappingServiceTest {

    @Mock
    private InputService mockInputService;

    private KeyboardMappingService mappingService;

    @BeforeEach
    void setUp() {
        mappingService = new KeyboardMappingService(mockInputService);
    }

    @Test
    @DisplayName("isActionActive should return true when a mapped key is pressed")
    void isActionActive_returnsTrue_whenKeyIsPressed() {
        // Arrange: Simulate the 'W' key being pressed.
        when(mockInputService.isKeyPressed(GLFW_KEY_W)).thenReturn(true);

        // Act
        boolean isMoveUpActive = mappingService.isActionActive(0, GameAction.MOVE_UP);

        // Assert
        assertThat(isMoveUpActive).isTrue();
    }

    @Test
    @DisplayName("isActionActive should return false when a mapped key is not pressed")
    void isActionActive_returnsFalse_whenKeyIsNotPressed() {
        // Arrange: Simulate the 'S' key NOT being pressed.
        when(mockInputService.isKeyPressed(GLFW_KEY_S)).thenReturn(false);

        // Act
        boolean isMoveDownActive = mappingService.isActionActive(0, GameAction.MOVE_DOWN);

        // Assert
        assertThat(isMoveDownActive).isFalse();
    }

    @Test
    @DisplayName("isActionActive should return false for an action that is not mapped")
    void isActionActive_returnsFalse_forUnmappedAction() {
        // Arrange: The ATTACK action exists but is not mapped to any key in the default service.
        // No key press simulation is needed because the mapping doesn't exist.

        // Act
        boolean isAttackActive = mappingService.isActionActive(0, GameAction.ATTACK);

        // Assert
        assertThat(isAttackActive).isFalse();
    }

    @Test
    @DisplayName("isActionActive should return false for an invalid player ID")
    void isActionActive_returnsFalse_forInvalidPlayerId() {
        // Arrange: No key press simulation is needed because the player ID check happens first.

        // Act: Query for a player ID that has no mappings.
        boolean isActive = mappingService.isActionActive(99, GameAction.MOVE_UP);

        // Assert
        assertThat(isActive).isFalse();
    }
}
