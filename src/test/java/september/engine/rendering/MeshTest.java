package september.engine.rendering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Integration test for the Mesh class, verifying its behavior in a live OpenGL context.
 */
class MeshTest extends EngineTestHarness {

    @Test
    @DisplayName("Constructor should create a valid mesh with GPU resources")
    void constructor_createsValidMesh_onGpu() {
        // Arrange: The harness provides a live GL context.
        float[] vertices = {
                // Position           // UV Coords
                0.5f, 0.5f, 0.0f, 1.0f, 1.0f, // Top Right
                0.5f, -0.5f, 0.0f, 1.0f, 0.0f, // Bottom Right
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Bottom Left
                -0.5f, 0.5f, 0.0f, 0.0f, 1.0f  // Top Left
        };
        int[] indices = {0, 1, 3, 1, 2, 3};

        // Act: Create a real Mesh object, which will make real OpenGL calls.
        Mesh mesh = new Mesh(vertices, indices);

        // Assert: A successful creation will result in a non-zero VAO ID and the correct vertex count.
        assertThat(mesh.getVaoId()).as("VAO ID should be a positive integer, indicating GPU resource creation.").isPositive();
        assertThat(mesh.getVertexCount()).as("Vertex count should match the provided indices.").isEqualTo(indices.length);
    }

    @Test
    @DisplayName("close() should release GPU resources without error")
    void close_releasesGpuResources_withoutError() {
        // Arrange: Create a real mesh in the live GL context.
        Mesh mesh = new Mesh(new float[]{}, new int[]{});
        assertThat(mesh.getVaoId()).as("Precondition: VAO ID should be valid before closing.").isPositive();

        // Act & Assert: The close() method should execute without throwing any OpenGL errors.
        assertThatCode(mesh::close)
                .as("Closing the mesh should not throw any exceptions.")
                .doesNotThrowAnyException();
    }
}
