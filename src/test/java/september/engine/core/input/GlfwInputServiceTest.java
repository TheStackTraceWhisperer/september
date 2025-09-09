package september.engine.core.input;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    // Argument captors to grab the callback instances
    private ArgumentCaptor<GLFWKeyCallback> keyCallbackCaptor = ArgumentCaptor.forClass(GLFWKeyCallback.class);
    private ArgumentCaptor<GLFWMouseButtonCallback> mouseButtonCallbackCaptor = ArgumentCaptor.forClass(GLFWMouseButtonCallback.class);
    private ArgumentCaptor<GLFWCursorPosCallback> cursorPosCallbackCaptor = ArgumentCaptor.forClass(GLFWCursorPosCallback.class);

    @BeforeEach
    void setUp() {
        inputService = new GlfwInputService();
        glfw = mockStatic(GLFW.class);

        // Arrange: Mock the WindowContext to return a handle
        when(windowContext.handle()).thenReturn(windowHandle);

        // Act: Initialize the service, which should register the callbacks
        inputService.installCallbacks(windowContext);

        // Assert: Verify that the callbacks were set on the correct window handle
        glfw.verify(() -> glfwSetKeyCallback(eq(windowHandle), keyCallbackCaptor.capture()));
        glfw.verify(() -> glfwSetMouseButtonCallback(eq(windowHandle), mouseButtonCallbackCaptor.capture()));
        glfw.verify(() -> glfwSetCursorPosCallback(eq(windowHandle), cursorPosCallbackCaptor.capture()));
    }

    @AfterEach
    void tearDown() {
        glfw.close();
    }

    @Test
    void keyCallback_updatesKeyState() {
        GLFWKeyCallback keyCallback = keyCallbackCaptor.getValue();

        // Simulate pressing the 'W' key
        keyCallback.invoke(windowHandle, GLFW_KEY_W, 0, GLFW_PRESS, 0);
        assertTrue(inputService.isKeyPressed(GLFW_KEY_W));

        // Simulate releasing the 'W' key
        keyCallback.invoke(windowHandle, GLFW_KEY_W, 0, GLFW_RELEASE, 0);
        assertFalse(inputService.isKeyPressed(GLFW_KEY_W));
    }

    @Test
    void mouseButtonCallback_updatesMouseButtonState() {
        GLFWMouseButtonCallback mouseButtonCallback = mouseButtonCallbackCaptor.getValue();

        // Simulate pressing the left mouse button
        mouseButtonCallback.invoke(windowHandle, GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS, 0);
        assertTrue(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));

        // Simulate releasing the left mouse button
        mouseButtonCallback.invoke(windowHandle, GLFW_MOUSE_BUTTON_LEFT, GLFW_RELEASE, 0);
        assertFalse(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Test
    void cursorPosCallback_updatesMousePosition() {
        GLFWCursorPosCallback cursorPosCallback = cursorPosCallbackCaptor.getValue();

        // Simulate moving the mouse
        cursorPosCallback.invoke(windowHandle, 100.0, 200.0);

        assertEquals(100.0, inputService.getMouseX());
        assertEquals(200.0, inputService.getMouseY());
    }

    @Test
    void clear_resetsAllInputStates() {
        // Arrange: Set some inputs to a pressed state
        keyCallbackCaptor.getValue().invoke(windowHandle, GLFW_KEY_SPACE, 0, GLFW_PRESS, 0);
        mouseButtonCallbackCaptor.getValue().invoke(windowHandle, GLFW_MOUSE_BUTTON_RIGHT, GLFW_PRESS, 0);
        cursorPosCallbackCaptor.getValue().invoke(windowHandle, 123.0, 456.0);
        assertTrue(inputService.isKeyPressed(GLFW_KEY_SPACE));
        assertTrue(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT));
        assertEquals(123.0, inputService.getMouseX());

        // Act
        inputService.clear();

        // Assert: Verify all states are reset
        assertFalse(inputService.isKeyPressed(GLFW_KEY_SPACE));
        assertFalse(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT));
        assertEquals(0.0, inputService.getMouseX());
        assertEquals(0.0, inputService.getMouseY());
    }

    @Test
    void isKeyPressed_returnsFalseForUnmanagedKey() {
        assertFalse(inputService.isKeyPressed(GLFW_KEY_LAST + 1));
    }

    @Test
    void isMouseButtonPressed_returnsFalseForUnmanagedButton() {
        assertFalse(inputService.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LAST + 1));
    }
}
