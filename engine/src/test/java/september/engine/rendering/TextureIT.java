package september.engine.rendering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.assets.AssetLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the Texture class, verifying its creation via the AssetLoader in a live OpenGL context.
 */
class TextureIT extends EngineTestHarness {

  @Test
  @DisplayName("AssetLoader.loadTexture should create a valid texture from a real image file")
  void loadTexture_createsValidTexture_fromRealFile() {
    // Arrange: The harness provides a live GL context.

    // Act & Assert: Use the official AssetLoader API. If this completes without an exception,
    // it means the texture was successfully loaded and uploaded to the GPU.
    assertThatCode(() -> {
      Texture texture = AssetLoader.loadTexture("textures/player.png");
      assertThat(texture).isNotNull();
    }).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AssetLoader.loadTexture should throw RuntimeException for an invalid image file")
  void loadTexture_throwsRuntimeException_forInvalidFile() {
    // Arrange: The harness provides a live GL context.
    // We point to a file that is not a valid image format.
    String invalidImagePath = "shaders/test.vert";

    // Act & Assert: Attempting to load a non-image file should fail with a descriptive exception.
    assertThatThrownBy(() -> AssetLoader.loadTexture(invalidImagePath))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Failed to load a texture");
  }

  @Test
  @DisplayName("Lifecycle methods should execute without error on a valid texture")
  void lifecycleMethods_executeWithoutError() {
    // Arrange: Create a real texture using the AssetLoader.
    Texture texture = AssetLoader.loadTexture("textures/enemy.png");
    assertThat(texture).isNotNull();

    // Act & Assert: The core lifecycle methods should execute without throwing any OpenGL errors,
    // which proves the internal texture ID is valid.
    assertThatCode(() -> texture.bind(0)).as("bind() should not throw").doesNotThrowAnyException();
    assertThatCode(texture::unbind).as("unbind() should not throw").doesNotThrowAnyException();
    assertThatCode(texture::close).as("close() should not throw").doesNotThrowAnyException();
  }
}
