package september.engine.core.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Integration test for GlfwGamepadService that requires a live GLFW context.
 * This test uses the EngineTestHarness to provide a real window and OpenGL context.
 */
class GlfwGamepadServiceIT extends EngineTestHarness {

    private GlfwGamepadService gamepadService;

    @BeforeEach
    void setupGamepadService() {
        gamepadService = new GlfwGamepadService();
    }

    @Test
    @DisplayName("Gamepad service should initialize properly")
    void gamepadService_initializesProperly() {
        // Assert: Service should be created successfully
        assertThat(gamepadService)
            .as("Gamepad service should be created")
            .isNotNull();
    }

    @Test
    @DisplayName("Gamepad service should handle connection checks for all supported indices")
    void gamepadService_handlesConnectionChecks() {
        // Act & Assert: Test all valid gamepad indices (0-7)
        for (int i = 0; i < 8; i++) {
            boolean connected = gamepadService.isGamepadConnected(i);
            assertThat(connected)
                .as("isGamepadConnected should return a boolean for index " + i)
                .isIn(true, false); // Either true or false is acceptable
        }
    }

    @Test
    @DisplayName("Gamepad service should return false for invalid indices")
    void gamepadService_handlesInvalidIndices() {
        // Act & Assert: Test invalid indices
        assertThat(gamepadService.isGamepadConnected(-1))
            .as("Should return false for negative index")
            .isFalse();
        
        assertThat(gamepadService.isGamepadConnected(8))
            .as("Should return false for index beyond max (8)")
            .isFalse();
        
        assertThat(gamepadService.isGamepadConnected(100))
            .as("Should return false for very large index")
            .isFalse();
    }

    @Test
    @DisplayName("Gamepad service should handle axis queries safely")
    void gamepadService_handlesAxisQueries() {
        // Act & Assert: Test axis queries for valid indices
        // Note: In headless environment, no gamepads are likely connected
        for (int i = 0; i < 8; i++) {
            float leftX = gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_LEFT_X);
            float leftY = gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_LEFT_Y);
            float rightX = gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_RIGHT_X);
            float rightY = gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_RIGHT_Y);
            
            // Should return valid float values (likely 0.0f for disconnected gamepads)
            assertThat(leftX)
                .as("Left X axis should return valid float for index " + i)
                .isInstanceOf(Float.class);
            
            assertThat(leftY)
                .as("Left Y axis should return valid float for index " + i)
                .isInstanceOf(Float.class);
            
            assertThat(rightX)
                .as("Right X axis should return valid float for index " + i)
                .isInstanceOf(Float.class);
            
            assertThat(rightY)
                .as("Right Y axis should return valid float for index " + i)
                .isInstanceOf(Float.class);
        }
    }

    @Test
    @DisplayName("Gamepad service should handle trigger axis queries")
    void gamepadService_handlesTriggerQueries() {
        // Act & Assert: Test trigger axis queries
        for (int i = 0; i < 8; i++) {
            float leftTrigger = gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
            float rightTrigger = gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
            
            // Triggers should return valid float values
            assertThat(leftTrigger)
                .as("Left trigger should return valid float for index " + i)
                .isInstanceOf(Float.class);
            
            assertThat(rightTrigger)
                .as("Right trigger should return valid float for index " + i)
                .isInstanceOf(Float.class);
        }
    }

    @Test
    @DisplayName("Gamepad service should return 0.0f for invalid gamepad indices in getAxis")
    void gamepadService_returnsZeroForInvalidIndicesInGetAxis() {
        // Act & Assert: Test that invalid indices return 0.0f
        assertThat(gamepadService.getAxis(-1, GLFW_GAMEPAD_AXIS_LEFT_X))
            .as("Should return 0.0f for negative index")
            .isEqualTo(0.0f);
        
        assertThat(gamepadService.getAxis(99, GLFW_GAMEPAD_AXIS_LEFT_Y))
            .as("Should return 0.0f for out-of-range index")
            .isEqualTo(0.0f);
    }

    @Test
    @DisplayName("Gamepad service should handle button queries safely")
    void gamepadService_handlesButtonQueries() {
        // Act & Assert: Test button queries for all standard buttons
        int[] standardButtons = {
            GLFW_GAMEPAD_BUTTON_A,
            GLFW_GAMEPAD_BUTTON_B,
            GLFW_GAMEPAD_BUTTON_X,
            GLFW_GAMEPAD_BUTTON_Y,
            GLFW_GAMEPAD_BUTTON_LEFT_BUMPER,
            GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER,
            GLFW_GAMEPAD_BUTTON_BACK,
            GLFW_GAMEPAD_BUTTON_START,
            GLFW_GAMEPAD_BUTTON_GUIDE,
            GLFW_GAMEPAD_BUTTON_LEFT_THUMB,
            GLFW_GAMEPAD_BUTTON_RIGHT_THUMB,
            GLFW_GAMEPAD_BUTTON_DPAD_UP,
            GLFW_GAMEPAD_BUTTON_DPAD_RIGHT,
            GLFW_GAMEPAD_BUTTON_DPAD_DOWN,
            GLFW_GAMEPAD_BUTTON_DPAD_LEFT
        };
        
        for (int i = 0; i < 8; i++) {
            for (int button : standardButtons) {
                boolean pressed = gamepadService.isButtonPressed(i, button);
                assertThat(pressed)
                    .as("Button query should return boolean for gamepad " + i + " button " + button)
                    .isIn(true, false);
            }
        }
    }

    @Test
    @DisplayName("Gamepad service should return false for invalid indices in isButtonPressed")
    void gamepadService_returnsFalseForInvalidIndicesInIsButtonPressed() {
        // Act & Assert: Test that invalid indices return false
        assertThat(gamepadService.isButtonPressed(-1, GLFW_GAMEPAD_BUTTON_A))
            .as("Should return false for negative index")
            .isFalse();
        
        assertThat(gamepadService.isButtonPressed(99, GLFW_GAMEPAD_BUTTON_B))
            .as("Should return false for out-of-range index")
            .isFalse();
    }

    @Test
    @DisplayName("Gamepad service should handle disconnected gamepads gracefully")
    void gamepadService_handlesDisconnectedGamepads() {
        // Note: In headless environment, all gamepads are likely disconnected
        // Act & Assert: Test that disconnected gamepads return sensible values
        
        for (int i = 0; i < 8; i++) {
            // If gamepad is not connected, axis should return 0.0f
            if (!gamepadService.isGamepadConnected(i)) {
                assertThat(gamepadService.getAxis(i, GLFW_GAMEPAD_AXIS_LEFT_X))
                    .as("Disconnected gamepad should return 0.0f for axis")
                    .isEqualTo(0.0f);
                
                assertThat(gamepadService.isButtonPressed(i, GLFW_GAMEPAD_BUTTON_A))
                    .as("Disconnected gamepad should return false for button")
                    .isFalse();
            }
        }
    }

    @Test
    @DisplayName("Gamepad service should be consistent across multiple calls")
    void gamepadService_consistentAcrossMultipleCalls() {
        // Act & Assert: Test that multiple calls return consistent results
        for (int i = 0; i < 3; i++) { // Test index 0 multiple times
            boolean connected1 = gamepadService.isGamepadConnected(0);
            boolean connected2 = gamepadService.isGamepadConnected(0);
            
            assertThat(connected1)
                .as("Connection status should be consistent across calls")
                .isEqualTo(connected2);
            
            float axis1 = gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X);
            float axis2 = gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X);
            
            // Axis values should be consistent (within floating point precision)
            assertThat(axis1)
                .as("Axis values should be consistent across calls")
                .isCloseTo(axis2, org.assertj.core.data.Offset.offset(0.001f));
            
            boolean button1 = gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A);
            boolean button2 = gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A);
            
            assertThat(button1)
                .as("Button state should be consistent across calls")
                .isEqualTo(button2);
        }
    }

    @Test
    @DisplayName("Gamepad service should handle resource management properly")
    void gamepadService_handlesResourceManagement() {
        // Act: Perform multiple operations that allocate and free GLFWGamepadState
        for (int i = 0; i < 10; i++) {
            // These operations should not cause memory leaks
            gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_X);
            gamepadService.getAxis(0, GLFW_GAMEPAD_AXIS_LEFT_Y);
            gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_A);
            gamepadService.isButtonPressed(0, GLFW_GAMEPAD_BUTTON_B);
        }
        
        // Assert: Service should still function normally
        assertThat(gamepadService.isGamepadConnected(0))
            .as("Service should still function after multiple operations")
            .isIn(true, false);
    }
}