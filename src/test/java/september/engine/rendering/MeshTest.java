package september.engine.rendering;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeshTest {

    private static final int VAO_ID = 1;
    private static final int VBO_ID = 2;
    private static final int EBO_ID = 3;

    private MockedStatic<GL15> gl15;
    private MockedStatic<GL20> gl20;
    private MockedStatic<GL30> gl30;

    @BeforeEach
    void setUp() {
        // Mock all the static GL classes needed by the Mesh constructor
        gl15 = mockStatic(GL15.class);
        gl20 = mockStatic(GL20.class);
        gl30 = mockStatic(GL30.class);

        // Stub the generation calls to return predictable IDs
        gl30.when(GL30::glGenVertexArrays).thenReturn(VAO_ID);
        gl15.when(GL15::glGenBuffers).thenReturn(VBO_ID, EBO_ID);
    }

    @AfterEach
    void tearDown() {
        gl15.close();
        gl20.close();
        gl30.close();
    }

    @Test
    void constructor_createsAndBindsVaoAndBuffers() {
        // Arrange: Define simple vertex and index data
        float[] vertices = { 0.5f, 0.5f, 0.0f, 1.0f, 1.0f };
        int[] indices = { 0 };

        // Act: The constructor makes all the GL calls that we need to verify
        new Mesh(vertices, indices);

        // Assert: Verify the sequence of GL calls for a complete mesh setup
        gl30.verify(GL30::glGenVertexArrays);
        gl30.verify(() -> GL30.glBindVertexArray(VAO_ID));

        // Verify VBO and EBO creation and data buffering
        gl15.verify(() -> GL15.glGenBuffers(), times(2));
        gl15.verify(() -> GL15.glBindBuffer(GL_ARRAY_BUFFER, VBO_ID));
        gl15.verify(() -> GL15.glBufferData(eq(GL_ARRAY_BUFFER), any(FloatBuffer.class), eq(GL_STATIC_DRAW)));
        gl15.verify(() -> GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO_ID));
        gl15.verify(() -> GL15.glBufferData(eq(GL_ELEMENT_ARRAY_BUFFER), any(IntBuffer.class), eq(GL_STATIC_DRAW)));

        // Verify vertex attribute pointers with correct stride and offset
        final int expectedStride = 5 * Float.BYTES;
        final long expectedTexCoordOffset = 3 * Float.BYTES;
        gl20.verify(() -> GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, expectedStride, 0L));
        gl20.verify(() -> GL20.glEnableVertexAttribArray(0));
        gl20.verify(() -> GL20.glVertexAttribPointer(1, 2, GL_FLOAT, false, expectedStride, expectedTexCoordOffset));
        gl20.verify(() -> GL20.glEnableVertexAttribArray(1));

        // Verify that the VAO is unbound at the end
        gl30.verify(() -> GL30.glBindVertexArray(0));
    }

    @Test
    void close_deletesArraysAndBuffers() {
        // Arrange: Create a mesh to get the generated IDs
        Mesh mesh = new Mesh(new float[]{}, new int[]{});

        // Act: Call the close method
        mesh.close();

        // Assert: Verify that the correct delete methods are called with the correct IDs
        gl15.verify(() -> GL15.glDeleteBuffers(VBO_ID));
        gl15.verify(() -> GL15.glDeleteBuffers(EBO_ID));
        gl30.verify(() -> GL30.glDeleteVertexArrays(VAO_ID));
    }
}
