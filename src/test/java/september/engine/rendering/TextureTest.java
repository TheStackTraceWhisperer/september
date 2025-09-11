package september.engine.rendering;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.opengl.GL11.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextureTest {

    private MockedStatic<GL11> gl11;
    private MockedStatic<GL13> gl13;
    private MockedStatic<GL30> gl30;
    private MockedStatic<STBImage> stb;

    @BeforeEach
    void setUp() {
        gl11 = mockStatic(GL11.class);
        gl13 = mockStatic(GL13.class);
        gl30 = mockStatic(GL30.class);
        stb = mockStatic(STBImage.class);

        gl11.when(GL11::glGenTextures).thenReturn(1);

        stb.when(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), anyInt()))
                .thenAnswer(invocation -> {
                    invocation.getArgument(1, IntBuffer.class).put(0, 16);
                    invocation.getArgument(2, IntBuffer.class).put(0, 16);
                    return ByteBuffer.allocateDirect(16 * 16 * 4);
                });

        stb.when(() -> STBImage.stbi_image_free(any(ByteBuffer.class))).thenAnswer(invocation -> null);
    }

    @AfterEach
    void tearDown() {
        gl11.close();
        gl13.close();
        gl30.close();
        stb.close();
    }

    @Test
    @DisplayName("Constructor successfully creates and configures a complete OpenGL texture")
    void constructor_createsAndConfiguresTexture() {
        new Texture(ByteBuffer.allocate(1));

        stb.verify(() -> STBImage.stbi_set_flip_vertically_on_load(true));
        stb.verify(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), eq(4)));
        gl11.verify(GL11::glGenTextures);
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 1));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST));
        gl11.verify(() -> GL11.glTexImage2D(eq(GL_TEXTURE_2D), eq(0), eq(GL_RGBA), eq(16), eq(16), eq(0), eq(GL_RGBA), eq(GL_UNSIGNED_BYTE), any(ByteBuffer.class)));
        gl30.verify(() -> GL30.glGenerateMipmap(GL_TEXTURE_2D));
        stb.verify(() -> STBImage.stbi_image_free(any(ByteBuffer.class)));
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 0));
    }

    @Test
    @DisplayName("Constructor throws a RuntimeException when STB image loading fails")
    void constructor_throwsRuntimeException_whenStbFailsToLoad() {
        stb.when(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), anyInt())).thenReturn(null);
        String expectedFailureReason = "corrupted image data";
        stb.when(STBImage::stbi_failure_reason).thenReturn(expectedFailureReason);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> new Texture(ByteBuffer.allocate(1)));

        assertTrue(ex.getMessage().contains(expectedFailureReason));
    }

    @Test
    @DisplayName("close() correctly deletes the texture from the GPU")
    void close_deletesTexture() {
        Texture texture = new Texture(ByteBuffer.allocate(1));
        texture.close();
        gl11.verify(() -> GL11.glDeleteTextures(1));
    }

    @Test
    @DisplayName("bind() activates the correct texture unit and binds the texture")
    void bind_activatesAndBindsTexture() {
        Texture texture = new Texture(ByteBuffer.allocate(1));
        int textureUnit = 5;

        texture.bind(textureUnit);

        gl13.verify(() -> GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit));
        // Verify bind is called TWICE: once in constructor, once in bind()
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 1), times(2));
    }

    @Test
    @DisplayName("unbind() unbinds the texture from the current unit")
    void unbind_unbindsTexture() {
        Texture texture = new Texture(ByteBuffer.allocate(1));

        texture.unbind();

        // Verify unbind is called TWICE: once in constructor, once in unbind()
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 0), times(2));
    }
}
