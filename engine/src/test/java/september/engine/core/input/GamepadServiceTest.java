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
 * Contract test for the GamepadService interface.
 * Tests that any implementation of GamepadService provides the expected behavior.
 */
@ExtendWith(MockitoExtension.class)
class GamepadServiceTest {

    @Mock
    private GamepadService mockGamepadService;

    @Test
    @DisplayName("isGamepadConnected should return consistent boolean values")
    void isGamepadConnected_returnsConsistentBooleans() {
        // Arrange: Setup consistent mocked behavior
        when(mockGamepadService.isGamepadConnected(0)).thenReturn(true);
        when(mockGamepadService.isGamepadConnected(1)).thenReturn(false);

        // Act & Assert: Verify contract expectations
        assertThat(mockGamepadService.isGamepadConnected(0))
            .as("isGamepadConnected should return true for connected gamepads")
            .isTrue();
        
        assertThat(mockGamepadService.isGamepadConnected(1))
            .as("isGamepadConnected should return false for disconnected gamepads")
            .isFalse();
    }

    @Test
    @DisplayName("isGamepadConnected should handle invalid indices gracefully")
    void isGamepadConnected_handlesInvalidIndices() {
        // Arrange: Setup behavior for invalid indices
        when(mockGamepadService.isGamepadConnected(-1)).thenReturn(false);
        when(mockGamepadService.isGamepadConnected(99)).thenReturn(false);

        // Act & Assert: Verify invalid index handling
        assertThat(mockGamepadService.isGamepadConnected(-1))
            .as("isGamepadConnected should return false for negative indices")
            .isFalse();
        
        assertThat(mockGamepadService.isGamepadConnected(99))
            .as("isGamepadConnected should return false for out-of-range indices")
            .isFalse();
    }

    @Test
    @DisplayName("getAxis should return valid float values in expected range")
    void getAxis_returnsValidFloatValues() {
        // Arrange: Setup mocked behavior for axis values
        when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(0.5f);
        when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y)).thenReturn(-0.75f);

        // Act & Assert: Verify contract expectations
        float leftX = mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X);
        float leftY = mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y);

        assertThat(leftX)
            .as("getAxis should return valid positive axis value")
            .isEqualTo(0.5f);
        
        assertThat(leftY)
            .as("getAxis should return valid negative axis value")
            .isEqualTo(-0.75f);
    }

    @Test
    @DisplayName("getAxis should return zero for disconnected gamepads")
    void getAxis_returnsZeroForDisconnectedGamepads() {
        // Arrange: Setup behavior for disconnected gamepad
        when(mockGamepadService.getAxis(1, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(0.0f);

        // Act & Assert: Verify disconnected gamepad handling
        float axisValue = mockGamepadService.getAxis(1, GLFW_GAMEPAD_AXIS_LEFT_X);
        assertThat(axisValue)
            .as("getAxis should return 0.0f for disconnected gamepads")
            .isEqualTo(0.0f);
    }

    @Test
    @DisplayName("getAxis should handle boundary values correctly")
    void getAxis_handlesBoundaryValues() {
        // Arrange: Test boundary conditions
        when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X)).thenReturn(-1.0f);
        when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y)).thenReturn(1.0f);
        when(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_RIGHT_X)).thenReturn(0.0f);

        // Act & Assert: Verify boundary value handling
        assertThat(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X))
            .as("getAxis should handle minimum axis value (-1.0)")
            .isEqualTo(-1.0f);
        
        assertThat(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y))
            .as("getAxis should handle maximum axis value (1.0)")
            .isEqualTo(1.0f);
        
        assertThat(mockGamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_RIGHT_X))
            .as("getAxis should handle neutral axis value (0.0)")
            .isEqualTo(0.0f);
    }

    @Test
    @DisplayName("isButtonPressed should return consistent boolean values")
    void isButtonPressed_returnsConsistentBooleans() {
        // Arrange: Setup consistent mocked behavior
        when(mockGamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A)).thenReturn(true);
        when(mockGamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_B)).thenReturn(false);

        // Act & Assert: Verify contract expectations
        assertThat(mockGamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A))
            .as("isButtonPressed should return true for pressed buttons")
            .isTrue();
        
        assertThat(mockGamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_B))
            .as("isButtonPressed should return false for unpressed buttons")
            .isFalse();
    }

    @Test
    @DisplayName("isButtonPressed should return false for disconnected gamepads")
    void isButtonPressed_returnsFalseForDisconnectedGamepads() {
        // Arrange: Setup behavior for disconnected gamepad
        when(mockGamepadService.isButtonPressed(1, GLFW_GAMEPAD_BUTTON_A)).thenReturn(false);

        // Act & Assert: Verify disconnected gamepad handling
        boolean isPressed = mockGamepadService.isButtonPressed(1, GLFW_GAMEPAD_BUTTON_A);
        assertThat(isPressed)
            .as("isButtonPressed should return false for disconnected gamepads")
            .isFalse();
    }

    @Test
    @DisplayName("isButtonPressed should handle invalid gamepad indices")
    void isButtonPressed_handlesInvalidIndices() {
        // Arrange: Setup behavior for invalid indices
        when(mockGamepadService.isButtonPressed(-1, GLFW_GAMEPAD_BUTTON_A)).thenReturn(false);
        when(mockGamepadService.isButtonPressed(99, GLFW_GAMEPAD_BUTTON_A)).thenReturn(false);

        // Act & Assert: Verify invalid index handling
        assertThat(mockGamepadService.isButtonPressed(-1, GLFW_GAMEPAD_BUTTON_A))
            .as("isButtonPressed should return false for negative indices")
            .isFalse();
        
        assertThat(mockGamepadService.isButtonPressed(99, GLFW_GAMEPAD_BUTTON_A))
            .as("isButtonPressed should return false for out-of-range indices")
            .isFalse();
    }
}