package september.engine.assets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.rendering.Texture;
import september.engine.rendering.gl.Shader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the AssetLoader, running against a live engine with a real OpenGL context.
 * Tests asset loading functionality that requires GPU resources.
 */
class AssetLoaderIT extends EngineTestHarness {

    @Test
    @DisplayName("loadShader should compile shader successfully from real files")
    void loadShader_constructsShaderSuccessfully_fromRealFiles() {
        // The EngineTestHarness provides a live GL context.
        // This test now loads and compiles real shader files from the test resources.
        Shader shader = AssetLoader.loadShader("shaders/test.vert", "shaders/test.frag");
        assertThat(shader).isNotNull();
        // The shader object will be cleaned up automatically when the GL context is destroyed by the harness.
    }

    @Test
    @DisplayName("loadShader should handle shader with includes successfully")
    void loadShader_withIncludeDirectives_compilesSuccessfully() {
        // Test shader include processing with OpenGL compilation
        // Use compatible vertex shader with includes and a compatible fragment shader
        Shader shader = AssetLoader.loadShader("shaders/with_includes.vert", "shaders/include_compatible.frag");
        assertThat(shader).isNotNull();
    }

    @Test
    @DisplayName("loadShader should throw runtime exception when resource not found")
    void loadShader_throwsRuntimeException_whenResourceNotFound() {
        // This test verifies that the correct exception is thrown for non-existent files.
        assertThatThrownBy(() -> AssetLoader.loadShader("non/existent/shader.vert", "non/existent/shader.frag"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to read resource");
    }

    @Test
    @DisplayName("loadShader should fail compilation with syntax error shader")
    void loadShader_withSyntaxError_throwsRuntimeException() {
        // Test that syntax errors in shaders are properly detected
        assertThatThrownBy(() -> AssetLoader.loadShader("shaders/test.vert", "shaders/syntax_error.frag"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error compiling shader");
    }

    @Test
    @DisplayName("loadTexture should create texture successfully from real PNG file")
    void loadTexture_constructsTextureSuccessfully_fromRealFile() {
        // This test now loads a real PNG file from the test resources into a GPU texture.
        Texture texture = AssetLoader.loadTexture("textures/enemy.png");
        assertThat(texture).isNotNull();
        // The texture object will be cleaned up automatically when the GL context is destroyed.
    }

    @Test
    @DisplayName("loadTexture should create texture from valid texture file")
    void loadTexture_withValidTexture_createsSuccessfully() {
        // Test loading our test valid texture
        Texture texture = AssetLoader.loadTexture("textures/valid_texture.png");
        assertThat(texture).isNotNull();
    }

    @Test
    @DisplayName("loadTexture should throw runtime exception when resource not found")
    void loadTexture_throwsRuntimeException_whenResourceNotFound() {
        // This test verifies that the correct exception is thrown for non-existent image files.
        assertThatThrownBy(() -> AssetLoader.loadTexture("non/existent/texture.png"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load texture resource");
    }

    @Test
    @DisplayName("loadTexture should handle corrupted texture file gracefully")
    void loadTexture_withCorruptedFile_throwsRuntimeException() {
        // Test that corrupted image files are handled appropriately
        assertThatThrownBy(() -> AssetLoader.loadTexture("textures/corrupted_texture.png"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load a texture from memory");
    }

    @Test
    @DisplayName("loadTexture should handle invalid format file gracefully")
    void loadTexture_withInvalidFormat_throwsRuntimeException() {
        // Test that non-image files are rejected
        assertThatThrownBy(() -> AssetLoader.loadTexture("textures/invalid_format.txt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load a texture from memory");
    }

    @Test
    @DisplayName("loadShader should handle valid vertex and fragment shaders")
    void loadShader_withValidShaders_compilesSuccessfully() {
        // Test using our explicitly named valid shader files
        Shader shader = AssetLoader.loadShader("shaders/valid_vertex.vert", "shaders/valid_fragment.frag");
        assertThat(shader).isNotNull();
    }

    @Test
    @DisplayName("loadShader should fail with invalid vertex shader")
    void loadShader_withInvalidVertexShader_throwsRuntimeException() {
        // Test with invalid vertex shader
        assertThatThrownBy(() -> AssetLoader.loadShader("shaders/invalid.vert", "shaders/test.frag"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error compiling shader");
    }
}
