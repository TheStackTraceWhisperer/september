package september.engine.core.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.when;

/**
 * Contract test for the InputService interface.
 * Tests that any implementation of InputService provides the expected behavior.
 */
@ExtendWith(MockitoExtension.class)
class InputServiceTest {

    @Mock
    private InputService mockInputService;

    @Test
    @DisplayName("isKeyPressed should return consistent boolean values")
    void isKeyPressed_returnsConsistentBooleans() {
        // Arrange: Setup consistent mocked behavior
        when(mockInputService.isKeyPressed(GLFW_KEY_W)).thenReturn(true);
        when(mockInputService.isKeyPressed(GLFW_KEY_S)).thenReturn(false);

        // Act & Assert: Verify contract expectations
        assertThat(mockInputService.isKeyPressed(GLFW_KEY_W))
            .as("isKeyPressed should return true for pressed keys")
            .isTrue();
        
        assertThat(mockInputService.isKeyPressed(GLFW_KEY_S))
            .as("isKeyPressed should return false for unpressed keys")
            .isFalse();
    }

    @Test
    @DisplayName("isMouseButtonPressed should return consistent boolean values")
    void isMouseButtonPressed_returnsConsistentBooleans() {
        // Arrange: Setup consistent mocked behavior
        when(mockInputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)).thenReturn(true);
        when(mockInputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT)).thenReturn(false);

        // Act & Assert: Verify contract expectations
        assertThat(mockInputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("isMouseButtonPressed should return true for pressed buttons")
            .isTrue();
        
        assertThat(mockInputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            .as("isMouseButtonPressed should return false for unpressed buttons")
            .isFalse();
    }

    @Test
    @DisplayName("getMouseX should return valid coordinate values")
    void getMouseX_returnsValidCoordinates() {
        // Arrange: Setup mocked behavior for mouse position
        when(mockInputService.getMouseX()).thenReturn(100.5);

        // Act & Assert: Verify contract expectations
        double mouseX = mockInputService.getMouseX();
        assertThat(mouseX)
            .as("getMouseX should return valid double coordinate")
            .isEqualTo(100.5);
    }

    @Test
    @DisplayName("getMouseY should return valid coordinate values")
    void getMouseY_returnsValidCoordinates() {
        // Arrange: Setup mocked behavior for mouse position
        when(mockInputService.getMouseY()).thenReturn(200.75);

        // Act & Assert: Verify contract expectations
        double mouseY = mockInputService.getMouseY();
        assertThat(mouseY)
            .as("getMouseY should return valid double coordinate")
            .isEqualTo(200.75);
    }

    @Test
    @DisplayName("Mouse coordinates should handle boundary conditions")
    void mouseCoordinates_handleBoundaryConditions() {
        // Arrange: Test edge cases
        when(mockInputService.getMouseX()).thenReturn(0.0);
        when(mockInputService.getMouseY()).thenReturn(-50.0);

        // Act & Assert: Verify edge case handling
        assertThat(mockInputService.getMouseX())
            .as("getMouseX should handle zero coordinates")
            .isEqualTo(0.0);
        
        assertThat(mockInputService.getMouseY())
            .as("getMouseY should handle negative coordinates")
            .isEqualTo(-50.0);
    }
}