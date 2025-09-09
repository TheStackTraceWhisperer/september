package september.engine.rendering.gl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL11;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.lwjgl.opengl.GL11.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenGLDebuggerTest {

    private MockedStatic<GL11> gl;

    @BeforeEach
    void setUp() {
        gl = mockStatic(GL11.class);
    }

    @AfterEach
    void tearDown() {
        gl.close();
    }

    @Test
    void checkErrors_throwsRuntimeException_whenErrorExists() {
        // Arrange
        when(glGetError()).thenReturn(GL_INVALID_ENUM).thenReturn(GL_NO_ERROR);

        // Act & Assert
        RuntimeException e = assertThrows(RuntimeException.class, OpenGLDebugger::checkErrors);
        assertTrue(e.getMessage().contains("OpenGL error"));
        assertTrue(e.getMessage().contains("GL_INVALID_ENUM"));
    }

    @Test
    void checkErrors_doesNothing_whenNoErrorExists() {
        // Arrange
        when(glGetError()).thenReturn(GL_NO_ERROR);

        // Act & Assert
        assertDoesNotThrow(OpenGLDebugger::checkErrors);
    }
}
