package september.engine.core.input;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.core.WindowContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlfwInputServiceTest {

    private GlfwInputService inputService;
    private MockedStatic<GLFW> glfw;
    private long windowHandle = 12345L;

    @Mock
    private WindowContext windowContext;

    // Argument captors to grab the callback instances for direct invocation in tests.
    private ArgumentCaptor<GLFWKeyCallback> keyCallbackCaptor = ArgumentCaptor.forClass(GLFWKeyCallback.class);
    private ArgumentCaptor<GLFWMouseButtonCallback> mouseButtonCallbackCaptor = ArgumentCaptor.forClass(GLFWMouseButtonCallback.class);
    private ArgumentCaptor<GLFWCursorPosCallback> cursorPosCallbackCaptor = ArgumentCaptor.forClass(GLFWCursorPosCallback.class);

    @BeforeEach
    void setUp() {
        inputService = new GlfwInputService();
        glfw = mockStatic(GLFW.class);

        when(windowContext.handle()).thenReturn(windowHandle);

        // This installs the callbacks, which we capture to simulate events.
        inputService.installCallbacks(windowContext);

        glfw.verify(() -> glfwSetKeyCallback(eq(windowHandle), keyCallbackCaptor.capture()));
        glfw.verify(() -> glfwSetMouseButtonCallback(eq(windowHandle), mouseButtonCallbackCaptor.capture()));
        glfw.verify(() -> glfwSetCursorPosCallback(eq(windowHandle), cursorPosCallbackCaptor.capture()));
    }

    @AfterEach
    void tearDown() {
        glfw.close();
    }

    @Test
    @DisplayName("Key callback should update key state for press and release")
    void keyCallback_updatesKeyState() {
        GLFWKeyCallback keyCallback = keyCallbackCaptor.getValue();

        keyCallback.invoke(windowHandle, GLFW_KEY_W, 0, GLFW_PRESS, 0);
        assertTrue(inputService.isKeyPressed(GLFW_KEY_W));

        keyCallback.invoke(windowHandle, GLFW_KEY_W, 0, GLFW_RELEASE, 0);
        assertFalse(inputService.isKeyPressed(GLFW_KEY_W));
    }

    @Test
    @DisplayName("Mouse button callback should update button state for press and release")
    void mouseButtonCallback_updatesMouseButtonState() {
        GLFWMouseButtonCallback mouseButtonCallback = mouseButtonCallbackCaptor.getValue();

        mouseButtonCallback.invoke(windowHandle, GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS, 0);
        assertTrue(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));

        mouseButtonCallback.invoke(windowHandle, GLFW_MOUSE_BUTTON_LEFT, GLFW_RELEASE, 0);
        assertFalse(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Test
    @DisplayName("Cursor position callback should update mouse coordinates")
    void cursorPosCallback_updatesMousePosition() {
        GLFWCursorPosCallback cursorPosCallback = cursorPosCallbackCaptor.getValue();

        cursorPosCallback.invoke(windowHandle, 100.0, 200.0);

        assertEquals(100.0, inputService.getMouseX());
        assertEquals(200.0, inputService.getMouseY());
    }

    @Test
    @DisplayName("clear() should reset all key, button, and mouse states")
    void clear_resetsAllInputStates() {
        // Arrange: Set some inputs to a pressed/moved state.
        keyCallbackCaptor.getValue().invoke(windowHandle, GLFW_KEY_SPACE, 0, GLFW_PRESS, 0);
        mouseButtonCallbackCaptor.getValue().invoke(windowHandle, GLFW_MOUSE_BUTTON_RIGHT, GLFW_PRESS, 0);
        cursorPosCallbackCaptor.getValue().invoke(windowHandle, 123.0, 456.0);

        // Act: Clear the service state.
        inputService.clear();

        // Assert: Verify all states are reset to their defaults.
        assertFalse(inputService.isKeyPressed(GLFW_KEY_SPACE));
        assertFalse(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT));
        assertEquals(0.0, inputService.getMouseX());
        assertEquals(0.0, inputService.getMouseY());
    }

    @Test
    @DisplayName("isKeyPressed should return false for out-of-bounds key codes")
    void isKeyPressed_returnsFalseForOutOfBoundsKeys() {
        assertFalse(inputService.isKeyPressed(-1), "Negative key codes should be handled.");
        assertFalse(inputService.isKeyPressed(GLFW_KEY_LAST + 1), "Key codes above the max should be handled.");
    }

    @Test
    @DisplayName("isMouseButtonPressed should return false for out-of-bounds button codes")
    void isMouseButtonPressed_returnsFalseForOutOfBoundsButtons() {
        assertFalse(inputService.isMouseButtonPressed(-1), "Negative button codes should be handled.");
        assertFalse(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LAST + 1), "Button codes above the max should be handled.");
    }

    @Test
    @DisplayName("Key callback should not throw for out-of-bounds key codes")
    void keyCallback_doesNotThrowForOutOfBoundsKey() {
        GLFWKeyCallback keyCallback = keyCallbackCaptor.getValue();
        assertDoesNotThrow(() -> keyCallback.invoke(windowHandle, -1, 0, GLFW_PRESS, 0));
        assertDoesNotThrow(() -> keyCallback.invoke(windowHandle, GLFW_KEY_LAST + 1, 0, GLFW_PRESS, 0));
    }

    @Test
    @DisplayName("Mouse button callback should not throw for out-of-bounds button codes")
    void mouseButtonCallback_doesNotThrowForOutOfBoundsButton() {
        GLFWMouseButtonCallback mouseButtonCallback = mouseButtonCallbackCaptor.getValue();
        assertDoesNotThrow(() -> mouseButtonCallback.invoke(windowHandle, -1, GLFW_PRESS, 0));
        assertDoesNotThrow(() -> mouseButtonCallback.invoke(windowHandle, GLFW_MOUSE_BUTTON_LAST + 1, GLFW_PRESS, 0));
    }
}
