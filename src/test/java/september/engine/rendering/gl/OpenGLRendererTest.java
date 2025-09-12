package september.engine.rendering.gl;

import org.joml.Matrix4f;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * This class is commented out as it relies on static mocking of OpenGL, which is obsolete under the new integration testing strategy.
 * The functionality of the renderer will be tested at a higher level by a RenderSystem integration test.
 */
/*
@ExtendWith(MockitoExtension.class)
class OpenGLRendererTest {

    private static final int SHADER_PROGRAM_ID = 100;

    @Mock
    private Camera camera;
    @Mock
    private Mesh mesh;
    @Mock
    private Texture texture;

    private OpenGLRenderer renderer;

    // Mocks for all required native OpenGL classes
    private MockedStatic<GL11> gl11;
    private MockedStatic<GL13> gl13;
    private MockedStatic<GL20> gl20;
    private MockedStatic<GL30> gl30;

    @BeforeEach
    void setUp() {
        // Mock all static native dependencies to run without a real graphics context
        gl11 = mockStatic(GL11.class);
        gl13 = mockStatic(GL13.class);
        gl20 = mockStatic(GL20.class);
        gl30 = mockStatic(GL30.class);

        // Stubbing for Shader constructor to succeed. This is needed for the renderer's constructor.
        gl20.when(() -> GL20.glCreateShader(anyInt())).thenReturn(1);
        gl20.when(() -> GL20.glGetShaderi(anyInt(), eq(GL_COMPILE_STATUS))).thenReturn(GL_TRUE);
        gl20.when(GL20::glCreateProgram).thenReturn(SHADER_PROGRAM_ID);
        gl20.when(() -> GL20.glGetProgrami(anyInt(), eq(GL_LINK_STATUS))).thenReturn(GL_TRUE);
        gl20.when(() -> GL20.glGetUniformLocation(anyInt(), anyString())).thenReturn(5);

        // The renderer will now use the mocked GL calls during its initialization
        renderer = new OpenGLRenderer();
    }

    @AfterEach
    void tearDown() {
        // Close the static mocks to clean up
        gl11.close();
        gl13.close();
        gl20.close();
        gl30.close();
    }

    @Test
    void beginScene_clearsAndUsesProgram() {
        // Arrange: Stub camera matrices specifically for this test
        when(camera.getProjectionMatrix()).thenReturn(new Matrix4f());
        when(camera.getViewMatrix()).thenReturn(new Matrix4f());

        // Act
        renderer.beginScene(camera);

        // Assert
        gl11.verify(() -> GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f));
        gl11.verify(() -> GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT));
        gl20.verify(() -> GL20.glUseProgram(SHADER_PROGRAM_ID));
    }

    @Test
    void submit_bindsAndDrawsElements() {
        // Arrange: Stub camera and mesh properties
        when(camera.getProjectionMatrix()).thenReturn(new Matrix4f());
        when(camera.getViewMatrix()).thenReturn(new Matrix4f());
        when(mesh.getVaoId()).thenReturn(7);
        when(mesh.getVertexCount()).thenReturn(6);

        // Act
        renderer.beginScene(camera);
        renderer.submit(mesh, texture, new Matrix4f());

        // Assert: Verify that the renderer interacts with its dependencies correctly
        // We verify that texture.bind() is called. We don't care about the internal
        // implementation of texture.bind() here; that is TextureTest's responsibility.
        verify(texture).bind(0);

        // Verify VAO is bound, drawn, and then unbound
        gl30.verify(() -> GL30.glBindVertexArray(7));
        gl11.verify(() -> GL11.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0L));
        gl30.verify(() -> GL30.glBindVertexArray(0));
    }

    @Test
    void endScene_unbindsProgram() {
        // Act
        renderer.endScene();

        // Assert: Verify that the shader program is unbound (use program 0)
        // This test no longer causes an UnnecessaryStubbingException because the
        // camera stubs were moved out of setUp().
        gl20.verify(() -> GL20.glUseProgram(0));
    }
}
*/
