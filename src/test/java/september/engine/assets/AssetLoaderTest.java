package september.engine.assets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetLoaderTest {

    private MockedStatic<GL11> gl11;
    private MockedStatic<GL13> gl13;
    private MockedStatic<GL20> gl20;
    private MockedStatic<GL30> gl30;
    private MockedStatic<STBImage> stb;

    @BeforeEach
    void setUp() {
        // Mock all static native dependencies for Shader and Texture classes
        gl11 = mockStatic(GL11.class);
        gl13 = mockStatic(GL13.class);
        gl20 = mockStatic(GL20.class);
        gl30 = mockStatic(GL30.class);
        stb = mockStatic(STBImage.class);

        // Stubbing for Shader constructor
        gl20.when(() -> GL20.glCreateShader(anyInt())).thenReturn(1);
        gl20.when(() -> GL20.glGetShaderi(anyInt(), eq(GL20.GL_COMPILE_STATUS))).thenReturn(GL_TRUE);
        gl20.when(GL20::glCreateProgram).thenReturn(1);
        gl20.when(() -> GL20.glGetProgrami(anyInt(), eq(GL20.GL_LINK_STATUS))).thenReturn(GL_TRUE);

        // Stubbing for Texture constructor
        gl11.when(GL11::glGenTextures).thenReturn(10);
        stb.when(() -> STBImage.stbi_set_flip_vertically_on_load(anyBoolean())).thenAnswer(invocation -> null);
        stb.when(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), anyInt()))
                .thenAnswer(invocation -> {
                    // Simulate STB loading an image: fill width/height and return a dummy buffer
                    invocation.getArgument(1, IntBuffer.class).put(0, 16);
                    invocation.getArgument(2, IntBuffer.class).put(0, 16);
                    return ByteBuffer.allocateDirect(16 * 16 * 4);
                });
    }

    @AfterEach
    void tearDown() {
        gl11.close();
        gl13.close();
        gl20.close();
        gl30.close();
        stb.close();
    }

    @Test
    void loadShader_constructsShaderSuccessfully() {
        // The real Shader constructor is called, but all its internal GL calls are mocked
        Shader shader = AssetLoader.loadShader("shaders/test.vert", "shaders/test.frag");
        assertNotNull(shader, "Shader should be constructed without errors.");
    }

    @Test
    void loadShader_throwsRuntimeException_whenResourceNotFound() {
        Exception e = assertThrows(RuntimeException.class, () -> {
            AssetLoader.loadShader("non/existent/shader.vert", "non/existent/shader.frag");
        });
        assertTrue(e.getMessage().contains("Failed to read resource"), "Exception message should indicate a resource failure.");
    }

    @Test
    void loadTexture_constructsTextureSuccessfully() {
        // The real Texture constructor is called, but its STB/GL calls are mocked
        Texture texture = AssetLoader.loadTexture("textures/test.txt");
        assertNotNull(texture, "Texture should be constructed without errors.");
    }

    @Test
    void loadTexture_throwsRuntimeException_whenResourceNotFound() {
        // This test will now pass because the NullPointerException in AssetLoader is fixed.
        Exception e = assertThrows(RuntimeException.class, () -> {
            AssetLoader.loadTexture("non/existent/texture.png");
        });
        assertTrue(e.getMessage().contains("Failed to load texture resource"), "Exception message should indicate a resource failure.");
    }
}
