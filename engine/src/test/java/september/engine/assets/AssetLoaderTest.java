package september.engine.assets;

import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the AssetLoader, running against a live engine with a real OpenGL context.
 */
class AssetLoaderTest extends EngineTestHarness {

    @Test
    void loadShader_constructsShaderSuccessfully_fromRealFiles() {
        // The EngineTestHarness provides a live GL context.
        // This test now loads and compiles real shader files from the test resources.
        Shader shader = AssetLoader.loadShader("shaders/test.vert", "shaders/test.frag");
        assertThat(shader).isNotNull();
        // The shader object will be cleaned up automatically when the GL context is destroyed by the harness.
    }

    @Test
    void loadShader_throwsRuntimeException_whenResourceNotFound() {
        // This test verifies that the correct exception is thrown for non-existent files.
        assertThatThrownBy(() -> AssetLoader.loadShader("non/existent/shader.vert", "non/existent/shader.frag"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to read resource");
    }

    @Test
    void loadTexture_constructsTextureSuccessfully_fromRealFile() {
        // This test now loads a real PNG file from the test resources into a GPU texture.
        Texture texture = AssetLoader.loadTexture("textures/enemy.png");
        assertThat(texture).isNotNull();
        // The texture object will be cleaned up automatically when the GL context is destroyed.
    }

    @Test
    void loadTexture_throwsRuntimeException_whenResourceNotFound() {
        // This test verifies that the correct exception is thrown for non-existent image files.
        assertThatThrownBy(() -> AssetLoader.loadTexture("non/existent/texture.png"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load texture resource");
    }
}
