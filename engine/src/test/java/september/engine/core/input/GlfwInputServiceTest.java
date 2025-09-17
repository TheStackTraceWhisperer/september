package september.engine.core.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Unit test for GlfwInputService focusing on pure logic aspects that don't require GLFW context.
 * Tests state management, boundary checking, and internal logic without callback installation.
 */
class GlfwInputServiceTest {

    private GlfwInputService inputService;

    @BeforeEach
    void setUp() {
        inputService = new GlfwInputService();
    }

    @Test
    @DisplayName("Initial state should have no keys pressed")
    void initialState_noKeysPressed() {
        // Assert: All keys should be unpressed initially
        assertThat(inputService.isKeyPressed(GLFW_KEY_W))
            .as("W key should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_S))
            .as("S key should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_A))
            .as("A key should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_D))
            .as("D key should not be pressed initially")
            .isFalse();
    }

    @Test
    @DisplayName("Initial state should have no mouse buttons pressed")
    void initialState_noMouseButtonsPressed() {
        // Assert: All mouse buttons should be unpressed initially
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("Left mouse button should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            .as("Right mouse button should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_MIDDLE))
            .as("Middle mouse button should not be pressed initially")
            .isFalse();
    }

    @Test
    @DisplayName("Initial mouse position should be zero")
    void initialState_mousePositionZero() {
        // Assert: Mouse position should be at origin initially
        assertThat(inputService.getMouseX())
            .as("Mouse X should be 0.0 initially")
            .isEqualTo(0.0);
        
        assertThat(inputService.getMouseY())
            .as("Mouse Y should be 0.0 initially")
            .isEqualTo(0.0);
    }

    @Test
    @DisplayName("isKeyPressed should handle invalid key codes gracefully")
    void isKeyPressed_handlesInvalidKeyCodes() {
        // Act & Assert: Test boundary conditions
        assertThat(inputService.isKeyPressed(-1))
            .as("isKeyPressed should return false for negative key codes")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_LAST + 1))
            .as("isKeyPressed should return false for key codes beyond GLFW_KEY_LAST")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(999999))
            .as("isKeyPressed should return false for very large key codes")
            .isFalse();
    }

    @Test
    @DisplayName("isMouseButtonPressed should handle invalid button codes gracefully")
    void isMouseButtonPressed_handlesInvalidButtonCodes() {
        // Act & Assert: Test boundary conditions
        assertThat(inputService.isMouseButtonPressed(-1))
            .as("isMouseButtonPressed should return false for negative button codes")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LAST + 1))
            .as("isMouseButtonPressed should return false for button codes beyond GLFW_MOUSE_BUTTON_LAST")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(999999))
            .as("isMouseButtonPressed should return false for very large button codes")
            .isFalse();
    }

    @Test
    @DisplayName("clear method should reset all input states")
    void clear_resetsAllInputStates() {
        // Note: We can't directly set states without callback installation,
        // but we can test that clear() doesn't break the service
        
        // Act: Clear the input service
        inputService.clear();
        
        // Assert: All states should remain false (or be reset to false)
        assertThat(inputService.isKeyPressed(GLFW_KEY_W))
            .as("Keys should be unpressed after clear")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("Mouse buttons should be unpressed after clear")
            .isFalse();
        
        assertThat(inputService.getMouseX())
            .as("Mouse X should be 0.0 after clear")
            .isEqualTo(0.0);
        
        assertThat(inputService.getMouseY())
            .as("Mouse Y should be 0.0 after clear")
            .isEqualTo(0.0);
    }

    @Test
    @DisplayName("Service should handle valid key codes within range")
    void isKeyPressed_handlesValidKeyCodes() {
        // Act & Assert: Test valid key code ranges
        assertThat(inputService.isKeyPressed(0))
            .as("isKeyPressed should handle minimum valid key code (0)")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_SPACE))
            .as("isKeyPressed should handle SPACE key code")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_A))
            .as("isKeyPressed should handle A key code")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_LAST))
            .as("isKeyPressed should handle maximum valid key code (GLFW_KEY_LAST)")
            .isFalse();
    }

    @Test
    @DisplayName("Service should handle valid mouse button codes within range")
    void isMouseButtonPressed_handlesValidButtonCodes() {
        // Act & Assert: Test valid mouse button code ranges
        assertThat(inputService.isMouseButtonPressed(0))
            .as("isMouseButtonPressed should handle minimum valid button code (0)")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("isMouseButtonPressed should handle LEFT button code")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            .as("isMouseButtonPressed should handle RIGHT button code")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LAST))
            .as("isMouseButtonPressed should handle maximum valid button code")
            .isFalse();
    }
}