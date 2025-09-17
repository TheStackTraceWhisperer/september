package september.engine.core.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Integration test for GlfwInputService that requires a live GLFW context.
 * This test uses the EngineTestHarness to provide a real window and OpenGL context.
 */
class GlfwInputServiceIT extends EngineTestHarness {

    private GlfwInputService inputService;

    @BeforeEach
    void setupInputService() {
        inputService = new GlfwInputService();
        // Install callbacks on the real window from the harness
        inputService.installCallbacks(engine.getWindow());
    }

    @Test
    @DisplayName("Input service should initialize with default state")
    void inputService_initializesWithDefaultState() {
        // Assert: All input should be in default (unpressed) state initially
        assertThat(inputService.isKeyPressed(GLFW_KEY_W))
            .as("W key should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_A))
            .as("A key should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("Left mouse button should not be pressed initially")
            .isFalse();
        
        assertThat(inputService.getMouseX())
            .as("Mouse X should be 0.0 initially")
            .isEqualTo(0.0);
        
        assertThat(inputService.getMouseY())
            .as("Mouse Y should be 0.0 initially")
            .isEqualTo(0.0);
    }

    @Test
    @DisplayName("Input service should handle callback installation without error")
    void inputService_handlesCallbackInstallation() {
        // Act: Create a new service and install callbacks
        GlfwInputService newService = new GlfwInputService();
        
        // This should not throw any exceptions
        newService.installCallbacks(engine.getWindow());
        
        // Assert: Service should be in default state
        assertThat(newService.isKeyPressed(GLFW_KEY_SPACE))
            .as("New service should have unpressed keys")
            .isFalse();
        
        assertThat(newService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            .as("New service should have unpressed mouse buttons")
            .isFalse();
    }

    @Test
    @DisplayName("Input service should handle multiple callback installations")
    void inputService_handlesMultipleCallbackInstallations() {
        // Act: Install callbacks multiple times (should overwrite previous ones)
        inputService.installCallbacks(engine.getWindow());
        inputService.installCallbacks(engine.getWindow());
        inputService.installCallbacks(engine.getWindow());
        
        // Assert: Service should still work correctly
        assertThat(inputService.isKeyPressed(GLFW_KEY_ESCAPE))
            .as("Service should work after multiple callback installations")
            .isFalse();
        
        assertThat(inputService.getMouseX())
            .as("Mouse position should remain consistent")
            .isEqualTo(0.0);
    }

    @Test
    @DisplayName("Clear method should reset all states")
    void clear_resetsAllStates() {
        // Act: Clear the input service
        inputService.clear();
        
        // Assert: All states should be reset
        assertThat(inputService.isKeyPressed(GLFW_KEY_W))
            .as("Keys should be unpressed after clear")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_S))
            .as("Keys should be unpressed after clear")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("Mouse buttons should be unpressed after clear")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
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
    @DisplayName("Input service should handle window context properly")
    void inputService_handlesWindowContext() {
        // Assert: Window context should be valid for callback installation
        assertThat(engine.getWindow())
            .as("Engine should provide a valid window context")
            .isNotNull();
        
        assertThat(engine.getWindow().handle())
            .as("Window context should have a valid handle")
            .isNotEqualTo(0L);
        
        // Act & Assert: Multiple services can use the same window
        GlfwInputService service1 = new GlfwInputService();
        GlfwInputService service2 = new GlfwInputService();
        
        // This should not cause issues (last one wins for callbacks)
        service1.installCallbacks(engine.getWindow());
        service2.installCallbacks(engine.getWindow());
        
        assertThat(service1.isKeyPressed(GLFW_KEY_TAB))
            .as("First service should still function")
            .isFalse();
        
        assertThat(service2.isKeyPressed(GLFW_KEY_TAB))
            .as("Second service should function normally")
            .isFalse();
    }

    @Test
    @DisplayName("Input service should handle all supported key ranges")
    void inputService_handlesAllKeyRanges() {
        // Act & Assert: Test various key code ranges
        assertThat(inputService.isKeyPressed(GLFW_KEY_SPACE))
            .as("Should handle SPACE key (printable character)")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_A))
            .as("Should handle letter keys")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_0))
            .as("Should handle number keys")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_F1))
            .as("Should handle function keys")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            .as("Should handle modifier keys")
            .isFalse();
        
        assertThat(inputService.isKeyPressed(GLFW_KEY_UP))
            .as("Should handle arrow keys")
            .isFalse();
    }

    @Test
    @DisplayName("Input service should handle all supported mouse buttons")
    void inputService_handlesAllMouseButtons() {
        // Act & Assert: Test various mouse button ranges
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT))
            .as("Should handle left mouse button")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT))
            .as("Should handle right mouse button")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_MIDDLE))
            .as("Should handle middle mouse button")
            .isFalse();
        
        // Test additional mouse buttons
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_4))
            .as("Should handle additional mouse buttons")
            .isFalse();
        
        assertThat(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_5))
            .as("Should handle additional mouse buttons")
            .isFalse();
    }
}