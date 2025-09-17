package september.engine.assets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.audio.AudioBuffer;
import september.engine.rendering.Mesh;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the ResourceManager, running against a live engine with a real OpenGL context.
 * Tests caching behavior, resource lifecycle management, and error handling.
 */
class ResourceManagerIT extends EngineTestHarness {

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
    @DisplayName("loadTexture should cache different textures with different handles")
    void loadTexture_withDifferentHandles_cachesSeperately() {
        // Act: Load different textures with different handles
        Texture texture1 = resourceManager.loadTexture("player", "textures/player.png");
        Texture texture2 = resourceManager.loadTexture("enemy", "textures/enemy.png");

        // Assert: Different instances should be returned for different handles
        assertThat(texture1).isNotNull();
        assertThat(texture2).isNotNull();
        assertThat(texture1).isNotSameAs(texture2);
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
    @DisplayName("loadShader should cache different shaders with different handles")
    void loadShader_withDifferentHandles_cachesSeperately() {
        // Act: Load different shader combinations with different handles
        Shader shader1 = resourceManager.loadShader("basic", "shaders/test.vert", "shaders/test.frag");
        Shader shader2 = resourceManager.loadShader("valid", "shaders/valid_vertex.vert", "shaders/valid_fragment.frag");

        // Assert: Different instances should be returned
        assertThat(shader1).isNotNull();
        assertThat(shader2).isNotNull();
        assertThat(shader1).isNotSameAs(shader2);
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
    @DisplayName("loadAudioBuffer should cache audio buffers correctly")
    void loadAudioBuffer_cachesResource() {
        // Skip this test if we're in headless audio mode where audio loading may not work
        if ("true".equals(System.getProperty("september.headless.audio", "false"))) {
            return; // Skip audio tests in headless mode
        }
        
        // Act: Load the same audio buffer twice
        AudioBuffer buffer1 = resourceManager.loadAudioBuffer("audio1", "audio/test-sound.ogg");
        AudioBuffer buffer2 = resourceManager.loadAudioBuffer("audio1", "audio/test-sound.ogg");

        // Assert: Should return the same cached instance
        assertThat(buffer1).isNotNull();
        assertThat(buffer2).isSameAs(buffer1);
    }

    @Test
    @DisplayName("loadAudioBuffer should cache different audio files separately")
    void loadAudioBuffer_withDifferentHandles_cachesSeperately() {
        // Skip this test if we're in headless audio mode where audio loading may not work
        if ("true".equals(System.getProperty("september.headless.audio", "false"))) {
            return; // Skip audio tests in headless mode
        }
        
        // Act: Load different audio files
        AudioBuffer buffer1 = resourceManager.loadAudioBuffer("sound", "audio/test-sound.ogg");
        AudioBuffer buffer2 = resourceManager.loadAudioBuffer("music", "audio/test-music.ogg");

        // Assert: Should be different instances
        assertThat(buffer1).isNotNull();
        assertThat(buffer2).isNotNull();
        assertThat(buffer1).isNotSameAs(buffer2);
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
        
        // Test audio buffer only if not in headless mode
        if (!"true".equals(System.getProperty("september.headless.audio", "false"))) {
            AudioBuffer loadedAudio = resourceManager.loadAudioBuffer("audio1", "audio/test-sound.ogg");
            assertThat(resourceManager.resolveAudioBufferHandle("audio1")).isSameAs(loadedAudio);
        }
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
        assertThatThrownBy(() -> resourceManager.resolveAudioBufferHandle("invalid"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("close() should clear all caches and make resources unavailable")
    void close_clearsCachesAndResources() {
        // Arrange: Load one of each resource type.
        resourceManager.loadTexture("tex1", "textures/player.png");
        resourceManager.loadShader("shader1", "shaders/test.vert", "shaders/test.frag");
        resourceManager.loadProceduralMesh("procMesh", new float[]{}, new int[]{});
        
        // Load audio only if not in headless mode
        boolean audioLoaded = false;
        if (!"true".equals(System.getProperty("september.headless.audio", "false"))) {
            resourceManager.loadAudioBuffer("audio1", "audio/test-sound.ogg");
            audioLoaded = true;
        }

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
        
        if (audioLoaded) {
            assertThatThrownBy(() -> resourceManager.resolveAudioBufferHandle("audio1"))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("Multiple loadProceduralMesh calls with same handle should properly clean up old meshes")
    void loadProceduralMesh_multipleCallsSameHandle_cleansUpProperly() {
        // Arrange: Create multiple meshes with the same handle to test cleanup
        resourceManager.loadProceduralMesh("testMesh", new float[]{1f, 2f, 3f}, new int[]{0, 1, 2});
        Mesh mesh1 = resourceManager.resolveMeshHandle("testMesh");

        // Act: Replace with a new mesh (this should close the old one)
        resourceManager.loadProceduralMesh("testMesh", new float[]{4f, 5f, 6f}, new int[]{0, 1, 2});
        Mesh mesh2 = resourceManager.resolveMeshHandle("testMesh");

        // Replace again
        resourceManager.loadProceduralMesh("testMesh", new float[]{7f, 8f, 9f}, new int[]{0, 1, 2});
        Mesh mesh3 = resourceManager.resolveMeshHandle("testMesh");

        // Assert: Each replacement should result in a different mesh instance
        assertThat(mesh1).isNotSameAs(mesh2);
        assertThat(mesh2).isNotSameAs(mesh3);
        assertThat(mesh1).isNotSameAs(mesh3);
        
        // The final mesh should be accessible
        assertThat(resourceManager.resolveMeshHandle("testMesh")).isSameAs(mesh3);
    }

    @Test
    @DisplayName("Resource loading should handle file loading errors gracefully")
    void loadResources_withInvalidFiles_throwsAppropriateExceptions() {
        // Test texture loading with invalid file
        assertThatThrownBy(() -> resourceManager.loadTexture("badTex", "non/existent/texture.png"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load texture resource");

        // Test shader loading with invalid file
        assertThatThrownBy(() -> resourceManager.loadShader("badShader", "non/existent/vertex.vert", "non/existent/fragment.frag"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to read resource");

        // Test audio buffer loading with invalid file only if audio is enabled
        if (!"true".equals(System.getProperty("september.headless.audio", "false"))) {
            assertThatThrownBy(() -> resourceManager.loadAudioBuffer("badAudio", "non/existent/audio.ogg"))
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
