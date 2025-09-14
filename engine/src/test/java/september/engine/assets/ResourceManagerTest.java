package september.engine.assets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the ResourceManager, running against a live engine with a real OpenGL context.
 */
class ResourceManagerTest extends EngineTestHarness {

    // The 'resourceManager' field is provided by the EngineTestHarness.
    // We will re-initialize it here to ensure it's clean for each test,
    // separate from the one used by the engine's own startup.
    @BeforeEach
    void localSetup() {
        this.resourceManager = new ResourceManager();
    }

    @Test
    @DisplayName("loadTexture should cache the texture after the first load")
    void loadTexture_cachesResource() {
        // Act: Load the same texture twice using its handle.
        // This now calls the real AssetLoader, which loads a real file into the GPU.
        Texture texture1 = resourceManager.loadTexture("tex1", "textures/player.png");
        Texture texture2 = resourceManager.loadTexture("tex1", "textures/player.png");

        // Assert: The returned instances should be the same.
        assertThat(texture1).isNotNull();
        assertThat(texture2).isSameAs(texture1);
    }

    @Test
    @DisplayName("loadShader should cache the shader after the first load")
    void loadShader_cachesResource() {
        // Act: Load the same shader twice.
        Shader shader1 = resourceManager.loadShader("shader1", "shaders/test.vert", "shaders/test.frag");
        Shader shader2 = resourceManager.loadShader("shader1", "shaders/test.vert", "shaders/test.frag");

        // Assert: The instances should be the same.
        assertThat(shader1).isNotNull();
        assertThat(shader2).isSameAs(shader1);
    }

    @Test
    @DisplayName("loadProceduralMesh should create and store a mesh")
    void loadProceduralMesh_loadsAndStoresMesh() {
        // Act: Load a procedural mesh. This creates a real VAO on the GPU.
        resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});
        Mesh loadedMesh = resourceManager.resolveMeshHandle("procMesh");

        // Assert: A new mesh should have been constructed and be retrievable.
        assertThat(loadedMesh).isNotNull();
    }

    @Test
    @DisplayName("loadProceduralMesh should replace an existing mesh with the same handle")
    void loadProceduralMesh_replacesOldMesh() {
        // Arrange: Load an initial mesh.
        resourceManager.loadProceduralMesh("procMesh", new float[]{1f}, new int[]{1});
        Mesh oldMesh = resourceManager.resolveMeshHandle("procMesh");

        // Act: Load a new mesh with the same handle.
        resourceManager.loadProceduralMesh("procMesh", new float[]{2f}, new int[]{2});
        Mesh newMesh = resourceManager.resolveMeshHandle("procMesh");

        // Assert: The newly resolved mesh should be a different instance from the old one.
        // This verifies that a replacement occurred.
        assertThat(newMesh).isNotNull();
        assertThat(newMesh).isNotSameAs(oldMesh);
    }

    @Test
    @DisplayName("resolveHandle methods should return the correct loaded resources")
    void resolveMethods_returnCorrectResources() {
        // Arrange: Load one of each type of resource.
        Texture loadedTexture = resourceManager.loadTexture("tex1", "textures/player.png");
        Shader loadedShader = resourceManager.loadShader("shader1", "shaders/test.vert", "shaders/test.frag");
        resourceManager.loadProceduralMesh("mesh1", new float[]{}, new int[]{});
        Mesh loadedMesh = resourceManager.resolveMeshHandle("mesh1");


        // Act & Assert: Verify that resolving each handle returns the correct object.
        assertThat(resourceManager.resolveTextureHandle("tex1")).isSameAs(loadedTexture);
        assertThat(resourceManager.resolveShaderHandle("shader1")).isSameAs(loadedShader);
        assertThat(resourceManager.resolveMeshHandle("mesh1")).isSameAs(loadedMesh);
    }

    @Test
    @DisplayName("resolveHandle methods should throw for invalid handles")
    void resolveMethods_throwForInvalidHandles() {
        assertThatThrownBy(() -> resourceManager.resolveTextureHandle("invalid"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> resourceManager.resolveShaderHandle("invalid"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> resourceManager.resolveMeshHandle("invalid"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("close() should clear all caches and make resources unavailable")
    void close_clearsCachesAndResources() {
        // Arrange: Load one of each resource type.
        resourceManager.loadTexture("tex1", "textures/player.png");
        resourceManager.loadShader("shader1", "shaders/test.vert", "shaders/test.frag");
        resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});

        // Act: Close the resource manager.
        resourceManager.close();

        // Assert: Verify that caches are cleared by trying to resolve handles again.
        // This is the observable behavior of closing the manager.
        assertThatThrownBy(() -> resourceManager.resolveTextureHandle("tex1"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> resourceManager.resolveShaderHandle("shader1"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> resourceManager.resolveMeshHandle("procMesh"))
                .isInstanceOf(NullPointerException.class);
    }
}
