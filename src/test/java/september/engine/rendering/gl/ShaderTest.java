package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL20;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.FloatBuffer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lwjgl.opengl.GL20.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaderTest {

    private MockedStatic<GL20> gl;
    private final String vertexSource = "vertex code";
    private final String fragmentSource = "fragment code";

    // Define constants for mock IDs to make tests clearer
    private static final int PROGRAM_ID = 1;
    private static final int VERTEX_SHADER_ID = 2;
    private static final int FRAGMENT_SHADER_ID = 3;
    private static final int UNIFORM_LOCATION = 4;

    @BeforeEach
    void setUp() {
        gl = mockStatic(GL20.class);
        // Default "happy path" stubbing: all GL calls succeed.
        // Individual tests can override these stubs to test failure cases.
        gl.when(GL20::glCreateProgram).thenReturn(PROGRAM_ID);
        gl.when(() -> glCreateShader(GL_VERTEX_SHADER)).thenReturn(VERTEX_SHADER_ID);
        gl.when(() -> glCreateShader(GL_FRAGMENT_SHADER)).thenReturn(FRAGMENT_SHADER_ID);
        gl.when(() -> glGetShaderi(anyInt(), eq(GL_COMPILE_STATUS))).thenReturn(GL_TRUE);
        gl.when(() -> glGetProgrami(anyInt(), eq(GL_LINK_STATUS))).thenReturn(GL_TRUE);
        gl.when(() -> glGetUniformLocation(anyInt(), anyString())).thenReturn(UNIFORM_LOCATION);
    }

    @AfterEach
    void tearDown() {
        gl.close();
    }

    @Test
    @DisplayName("Constructor correctly compiles, links, and cleans up shaders")
    void constructor_compilesAndLinksShaders() {
        new Shader(vertexSource, fragmentSource);

        // Verify the entire shader creation and linking pipeline
        gl.verify(GL20::glCreateProgram);
        gl.verify(() -> glCreateShader(GL_VERTEX_SHADER));
        gl.verify(() -> glShaderSource(VERTEX_SHADER_ID, vertexSource));
        gl.verify(() -> glCompileShader(VERTEX_SHADER_ID));
        gl.verify(() -> glAttachShader(PROGRAM_ID, VERTEX_SHADER_ID));

        gl.verify(() -> glCreateShader(GL_FRAGMENT_SHADER));
        gl.verify(() -> glShaderSource(FRAGMENT_SHADER_ID, fragmentSource));
        gl.verify(() -> glCompileShader(FRAGMENT_SHADER_ID));
        gl.verify(() -> glAttachShader(PROGRAM_ID, FRAGMENT_SHADER_ID));

        gl.verify(() -> glLinkProgram(PROGRAM_ID));
        gl.verify(() -> glValidateProgram(PROGRAM_ID));

        // Verify cleanup of individual shaders after linking
        gl.verify(() -> glDetachShader(PROGRAM_ID, VERTEX_SHADER_ID));
        gl.verify(() -> glDetachShader(PROGRAM_ID, FRAGMENT_SHADER_ID));
        gl.verify(() -> glDeleteShader(VERTEX_SHADER_ID));
        gl.verify(() -> glDeleteShader(FRAGMENT_SHADER_ID));
    }

    @Test
    @DisplayName("Constructor throws RuntimeException on shader compile failure")
    void constructor_throwsException_onCompileFailure() {
        // Arrange: Force a compile failure for one of the shaders
        gl.when(() -> glGetShaderi(VERTEX_SHADER_ID, GL_COMPILE_STATUS)).thenReturn(GL_FALSE);
        // Act & Assert
        assertThrows(RuntimeException.class, () -> new Shader(vertexSource, fragmentSource));
    }

    @Test
    @DisplayName("Constructor throws RuntimeException on program link failure")
    void constructor_throwsException_onLinkFailure() {
        // Arrange: Force a link failure
        gl.when(() -> glGetProgrami(PROGRAM_ID, GL_LINK_STATUS)).thenReturn(GL_FALSE);
        // Act & Assert
        assertThrows(RuntimeException.class, () -> new Shader(vertexSource, fragmentSource));
    }

    @Test
    @DisplayName("Constructor throws RuntimeException on shader creation failure")
    void constructor_throwsException_onCreateShaderFailure() {
        // Arrange: Force glCreateShader to return 0, indicating failure
        gl.when(() -> glCreateShader(GL_VERTEX_SHADER)).thenReturn(0);
        // Act & Assert
        assertThrows(RuntimeException.class, () -> new Shader(vertexSource, fragmentSource));
    }

    @Test
    @DisplayName("bind() uses the correct shader program")
    void bind_usesCorrectProgram() {
        Shader shader = new Shader(vertexSource, fragmentSource);
        shader.bind();
        gl.verify(() -> glUseProgram(PROGRAM_ID));
    }

    @Test
    @DisplayName("unbind() uses program 0")
    void unbind_usesProgramZero() {
        Shader shader = new Shader(vertexSource, fragmentSource);
        shader.unbind();
        gl.verify(() -> glUseProgram(0));
    }

    @Test
    @DisplayName("close() unbinds and deletes the shader program")
    void close_deletesShaderProgram() {
        Shader shader = new Shader(vertexSource, fragmentSource);
        shader.close();
        // The close method should first unbind the program, then delete it.
        gl.verify(() -> glUseProgram(0));
        gl.verify(() -> glDeleteProgram(PROGRAM_ID));
    }

    @Test
    @DisplayName("setUniform(Matrix4f) uploads data correctly")
    void setUniformMatrix4f_uploadsData() {
        Shader shader = new Shader(vertexSource, fragmentSource);
        shader.setUniform("testUniform", new Matrix4f());
        gl.verify(() -> glUniformMatrix4fv(eq(UNIFORM_LOCATION), eq(false), any(FloatBuffer.class)));
    }

    @Test
    @DisplayName("setUniform(Vector3f) uploads data correctly")
    void setUniformVector3f_uploadsData() {
        Shader shader = new Shader(vertexSource, fragmentSource);
        shader.setUniform("testUniform", new Vector3f(1, 2, 3));
        gl.verify(() -> glUniform3f(UNIFORM_LOCATION, 1.0f, 2.0f, 3.0f));
    }

    @Test
    @DisplayName("setUniform(int) uploads data correctly")
    void setUniformInt_uploadsData() {
        Shader shader = new Shader(vertexSource, fragmentSource);
        shader.setUniform("testSampler", 5);
        gl.verify(() -> glUniform1i(UNIFORM_LOCATION, 5));
    }

    @Test
    @DisplayName("getUniformLocation caches the result and is only called once per uniform")
    void getUniformLocation_isCached() {
        Shader shader = new Shader(vertexSource, fragmentSource);

        // Act: Set the same uniform multiple times
        shader.setUniform("testUniform", new Vector3f());
        shader.setUniform("testUniform", new Vector3f());
        shader.setUniform("testUniform", new Vector3f());

        // Assert: Verify that the native call to get the location was only made once.
        gl.verify(() -> glGetUniformLocation(PROGRAM_ID, "testUniform"), times(1));
    }
}
