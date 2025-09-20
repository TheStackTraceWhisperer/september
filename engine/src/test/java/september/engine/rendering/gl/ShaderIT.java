package september.engine.rendering.gl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.assets.AssetLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for the Shader class, verifying its compilation and lifecycle in a live OpenGL context.
 */
class ShaderIT extends EngineTestHarness {

  @Test
  @DisplayName("AssetLoader.loadShader should create a valid shader from real GLSL files")
  void loadShader_createsValidShader_fromRealFiles() {
    // Arrange: The harness provides a live GL context.

    // Act & Assert: Use the official AssetLoader API. If this completes without an exception,
    // it means the shader was successfully compiled and linked on the GPU.
    assertThatCode(() -> {
      Shader shader = AssetLoader.loadShader("shaders/test.vert", "shaders/test.frag");
      assertThat(shader).isNotNull();
    }).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("AssetLoader.loadShader should throw RuntimeException for invalid shader source")
  void loadShader_throwsRuntimeException_forInvalidSource() {
    // Arrange: The harness provides a live GL context.

    // Act & Assert: Attempting to load an invalid shader file should fail with a descriptive exception.
    // We check for the actual error message produced by the Shader class.
    assertThatThrownBy(() -> AssetLoader.loadShader("shaders/invalid.vert", "shaders/test.frag"))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Error compiling shader code");
  }

  @Test
  @DisplayName("Lifecycle methods should execute without error on a valid shader")
  void lifecycleMethods_executeWithoutError() {
    // Arrange: Create a real, valid shader using the AssetLoader.
    Shader shader = AssetLoader.loadShader("shaders/test.vert", "shaders/test.frag");
    assertThat(shader).isNotNull();

    // Act & Assert: The core lifecycle methods should execute without throwing any OpenGL errors,
    // which proves the internal program ID is valid.
    assertThatCode(shader::bind).as("bind() should not throw").doesNotThrowAnyException();
    assertThatCode(shader::unbind).as("unbind() should not throw").doesNotThrowAnyException();
    assertThatCode(shader::close).as("close() should not throw").doesNotThrowAnyException();
  }
}
