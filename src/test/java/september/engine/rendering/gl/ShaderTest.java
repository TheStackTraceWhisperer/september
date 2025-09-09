package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL20;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaderTest {

    private MockedStatic<GL20> gl;
    private final String vertexSource = "vertex code";
    private final String fragmentSource = "fragment code";

    @BeforeEach
    void setUp() {
        gl = mockStatic(GL20.class);
        // Mock GL calls to simulate successful shader creation and linking
        gl.when(() -> glCreateProgram()).thenReturn(1);
        gl.when(() -> glCreateShader(GL_VERTEX_SHADER)).thenReturn(2);
        gl.when(() -> glCreateShader(GL_FRAGMENT_SHADER)).thenReturn(3);
        gl.when(() -> glGetShaderi(anyInt(), eq(GL_COMPILE_STATUS))).thenReturn(GL_TRUE);
        gl.when(() -> glGetProgrami(anyInt(), eq(GL_LINK_STATUS))).thenReturn(GL_TRUE);
        gl.when(() -> glGetUniformLocation(anyInt(), anyString())).thenReturn(4);
    }

    @AfterEach
    void tearDown() {
        gl.close();
    }

    @Test
    void constructor_compilesAndLinksShaders() {
        // Act
        new Shader(vertexSource, fragmentSource);

        // Assert: Verify the entire shader creation and linking pipeline
        gl.verify(() -> glCreateProgram());
        gl.verify(() -> glCreateShader(GL_VERTEX_SHADER));
        gl.verify(() -> glShaderSource(2, vertexSource));
        gl.verify(() -> glCompileShader(2));
        gl.verify(() -> glAttachShader(1, 2));

        gl.verify(() -> glCreateShader(GL_FRAGMENT_SHADER));
        gl.verify(() -> glShaderSource(3, fragmentSource));
        gl.verify(() -> glCompileShader(3));
        gl.verify(() -> glAttachShader(1, 3));

        gl.verify(() -> glLinkProgram(1));
        gl.verify(() -> glValidateProgram(1));

        // Verify cleanup of individual shaders after linking
        gl.verify(() -> glDetachShader(1, 2));
        gl.verify(() -> glDetachShader(1, 3));
        gl.verify(() -> glDeleteShader(2));
        gl.verify(() -> glDeleteShader(3));
    }

    @Test
    void close_deletesShaderProgram() {
        // Arrange
        Shader shader = new Shader(vertexSource, fragmentSource);

        // Act
        shader.close();

        // Assert
        gl.verify(() -> glDeleteProgram(1));
    }

    @Test
    void setUniformMatrix4f_uploadsData() {
        // Arrange
        Shader shader = new Shader(vertexSource, fragmentSource);

        // Act
        shader.setUniform("testUniform", new Matrix4f());

        // Assert
        gl.verify(() -> glUniformMatrix4fv(eq(4), eq(false), any(FloatBuffer.class)));
    }

    @Test
    void setUniformVector3f_uploadsData() {
        // Arrange
        Shader shader = new Shader(vertexSource, fragmentSource);

        // Act
        shader.setUniform("testUniform", new Vector3f());

        // Assert
        gl.verify(() -> glUniform3f(4, 0.0f, 0.0f, 0.0f));
    }
}
