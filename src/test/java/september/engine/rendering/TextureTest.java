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

/**
 * # Test Strategy for `Texture.java`
 * <p>
 * ## The Challenge
 * The `Texture` class is a high-level abstraction over low-level native OpenGL and STB library calls.
 * Its constructor and methods directly interact with these native libraries, which are not available
 * in a standard JUnit test environment. Attempting to instantiate a `Texture` directly would result
 * in an `UnsatisfiedLinkError` because the underlying native code cannot be found.
 * <p>
 * ## The Solution: Static Mocking
 * To solve this, we must isolate the `Texture` class from its native dependencies. As per the project's
 * `TESTING.md` guidelines, the correct strategy is to use Mockito's `mockStatic` feature.
 * This allows us to intercept static calls to native classes (like `GL11`, `GL30`, `STBImage`)
 * and provide fake, predictable responses. This ensures our test is fast, reliable, and can run
 * on any platform without a real graphics card.
 * <p>
 * ## What This Test Verifies
 * This test class ensures that the `Texture` class correctly orchestrates the native calls required
 * to create, configure, bind, and destroy a texture on the GPU. We are not testing OpenGL itself;
 * we are testing that our `Texture` class uses the OpenGL API correctly.
 * We verify:
 *   1.  **Constructor Logic**: That the constructor follows the correct sequence of STB and OpenGL calls
 *       to load image data and upload it to the GPU.
 *   2.  **Error Handling**: That the constructor properly handles failures from the native libraries.
 *   3.  **Resource Management**: That the `close()` method correctly calls `glDeleteTextures`.
 *   4.  **Binding/Unbinding**: That `bind()` and `unbind()` correctly manage the texture state.
 */
@ExtendWith(MockitoExtension.class)
class TextureTest {

    // These fields hold the static mocks for the native libraries. They are managed by the setUp/tearDown methods.
    private MockedStatic<GL11> gl11;
    private MockedStatic<GL13> gl13;
    private MockedStatic<GL30> gl30;
    private MockedStatic<STBImage> stb;

    /**
     * ## Test Setup (`@BeforeEach`)
     * <p>
     * ### Purpose
     * This method runs before each test case. Its primary job is to create and configure the mocks
     * for all the native libraries that the `Texture` class depends on.
     * <p>
     * ### Mock Initialization
     * We initialize static mocks for:
     *   - `GL11`, `GL13`, `GL30`: For core OpenGL functions (generating IDs, setting parameters, uploading data).
     *   - `STBImage`: For the `stb_image` library functions used to load image data from memory.
     * <p>
     * ### Stubbing for Success
     * We then "stub" the native functions to simulate a successful execution path. This allows the
     * `Texture` constructor to run without crashing. Key stubs include:
     *   - `stbi_load_from_memory`: We simulate it successfully "loading" a 16x16 RGBA image.
     *     Crucially, we make the mock populate the `IntBuffer` arguments for width, height, and channels,
     *     just as the real native function would. This prevents `NullPointerException`s in the constructor.
     *   - `glGenTextures`: We make it return a predictable texture ID (e.g., 1) so we can verify it later.
     */
    @BeforeEach
    void setUp() {
        // Initialize static mocks for all required native classes.
        gl11 = mockStatic(GL11.class);
        gl13 = mockStatic(GL13.class);
        gl30 = mockStatic(GL30.class);
        stb = mockStatic(STBImage.class);

        // --- Stubbing for a successful constructor run ---

        // When the constructor asks to generate a texture ID, return a predictable ID of 1.
        gl11.when(GL11::glGenTextures).thenReturn(1);

        // When the constructor tries to load image data from a memory buffer...
        stb.when(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), anyInt()))
                .thenAnswer(invocation -> {
                    // ...simulate the native function's behavior by populating the output parameters.
                    invocation.getArgument(1, IntBuffer.class).put(0, 16); // width = 16
                    invocation.getArgument(2, IntBuffer.class).put(0, 16); // height = 16
                    // And return a dummy, non-null ByteBuffer to represent the decoded image data.
                    return ByteBuffer.allocateDirect(16 * 16 * 4);
                });

        // The constructor frees the STB image buffer after uploading it to the GPU. We must mock this to avoid errors.
        stb.when(() -> STBImage.stbi_image_free(any(ByteBuffer.class))).thenAnswer(invocation -> null);
    }

    /**
     * ## Test Teardown (`@AfterEach`)
     *
     * ### Purpose
     * This method runs after each test case. It is critical to close the static mocks.
     * If we don't, the mocks will "leak" into other tests (even in other files),
     * causing unpredictable behavior and hard-to-diagnose failures.
     */
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
        // --- Arrange ---
        // We just need a non-null ByteBuffer. Its content doesn't matter because stbi_load_from_memory is mocked.
        ByteBuffer dummyImageFileBuffer = ByteBuffer.allocate(1);

        // --- Act ---
        // This is the action under test. We create a new Texture, which will trigger all the mocked native calls.
        new Texture(dummyImageFileBuffer);

        // --- Assert ---
        // Now, we meticulously verify that the constructor called all the native functions in the correct order and with the correct parameters.

        // 1. Verify the STB image loading process.
        stb.verify(() -> STBImage.stbi_set_flip_vertically_on_load(true));
        stb.verify(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), eq(4)));

        // 2. Verify the core OpenGL texture generation and binding.
        gl11.verify(GL11::glGenTextures);
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 1));

        // 3. Verify the texture parameters are set correctly.
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST));
        gl11.verify(() -> GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST));

        // 4. Verify that the image data is uploaded to the GPU.
        gl11.verify(() -> GL11.glTexImage2D(eq(GL_TEXTURE_2D), eq(0), eq(GL_RGBA), eq(16), eq(16), eq(0), eq(GL_RGBA), eq(GL_UNSIGNED_BYTE), any(ByteBuffer.class)));

        // 5. Verify that mipmaps are generated.
        gl30.verify(() -> GL30.glGenerateMipmap(GL_TEXTURE_2D));

        // 6. Verify cleanup and final state.
        stb.verify(() -> STBImage.stbi_image_free(any(ByteBuffer.class)));
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 0));
    }

    @Test
    @DisplayName("Constructor throws a RuntimeException when STB image loading fails")
    void constructor_throwsRuntimeException_whenStbFailsToLoad() {
        // --- Arrange ---
        // For this test, we override the default successful stubbing from setUp().
        // We force stbi_load_from_memory to return null, simulating a corrupted image file.
        stb.when(() -> STBImage.stbi_load_from_memory(any(ByteBuffer.class), any(IntBuffer.class), any(IntBuffer.class), any(IntBuffer.class), anyInt())).thenReturn(null);

        // We also mock the failure reason function to return a specific error message.
        String expectedFailureReason = "corrupted image data";
        stb.when(STBImage::stbi_failure_reason).thenReturn(expectedFailureReason);

        // --- Act & Assert ---
        // We assert that calling the constructor now throws a RuntimeException.
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            new Texture(ByteBuffer.allocate(1));
        });

        // Finally, we verify that the exception message contains the specific failure reason we mocked.
        // This confirms our constructor correctly reports errors from the native library.
        assertTrue(ex.getMessage().contains(expectedFailureReason), "The exception message should include the STB failure reason.");
    }

    @Test
    @DisplayName("close() correctly deletes the texture from the GPU")
    void close_deletesTexture() {
        // --- Arrange ---
        // First, create a texture instance. The constructor will run successfully thanks to the mocks in setUp().
        Texture texture = new Texture(ByteBuffer.allocate(1));

        // --- Act ---
        // Call the method under test.
        texture.close();

        // --- Assert ---
        // Verify that glDeleteTextures was called with the correct texture ID (which we stubbed to be 1).
        // This confirms that we are properly cleaning up GPU resources.
        gl11.verify(() -> GL11.glDeleteTextures(1));
    }

    @Test
    @DisplayName("bind() activates the correct texture unit and binds the texture")
    void bind_activatesAndBindsTexture() {
        // --- Arrange ---
        Texture texture = new Texture(ByteBuffer.allocate(1));
        // Clear invocations on gl11 mock to only verify calls made by the bind() method itself.
        gl11.clearInvocations();
        int textureUnit = 5; // Use a non-zero texture unit for a more robust test.

        // --- Act ---
        texture.bind(textureUnit);

        // --- Assert ---
        // Verify that the correct texture unit was activated.
        gl13.verify(() -> GL13.glActiveTexture(GL13.GL_TEXTURE0 + textureUnit));
        // Verify that our texture was bound to that unit.
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 1));
    }

    @Test
    @DisplayName("unbind() unbinds the texture from the current unit")
    void unbind_unbindsTexture() {
        // --- Arrange ---
        Texture texture = new Texture(ByteBuffer.allocate(1));
        // Clear invocations on gl11 mock to only verify calls made by the unbind() method itself.
        gl11.clearInvocations();

        // --- Act ---
        texture.unbind();

        // --- Assert ---
        // Verify that glBindTexture was called with 0, which unbinds any texture from the current target.
        gl11.verify(() -> GL11.glBindTexture(GL_TEXTURE_2D, 0));
    }
}
