package september.engine.assets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for AssetLoader focusing on pure logic functionality that doesn't require OpenGL context.
 * Tests file reading, path handling, and error scenarios.
 */
class AssetLoaderTest {

  @Test
  @DisplayName("readResourceToString should read text file content correctly")
  void readResourceToString_withValidTextFile_returnsContent() {
    // Act: Read a known text file from test resources
    String content = AssetLoader.readResourceToString("textures/test.txt");

    // Assert: Content should match the expected file content
    assertThat(content).isEqualTo("dummy texture data\n");
  }

  @Test
  @DisplayName("readResourceToString should handle paths with leading slash")
  void readResourceToString_withLeadingSlash_readsFileCorrectly() {
    // Act: Read with leading slash (should be corrected internally)
    String content = AssetLoader.readResourceToString("/textures/test.txt");

    // Assert: Should read same content regardless of leading slash
    assertThat(content).isEqualTo("dummy texture data\n");
  }

  @Test
  @DisplayName("readResourceToString should throw runtime exception for non-existent file")
  void readResourceToString_withNonExistentFile_throwsRuntimeException() {
    // Act & Assert: Should throw RuntimeException for missing file
    assertThatThrownBy(() -> AssetLoader.readResourceToString("non/existent/file.txt"))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Failed to read resource")
      .hasMessageContaining("non/existent/file.txt");
  }

  @Test
  @DisplayName("readResourceToByteBuffer should read binary file data correctly")
  void readResourceToByteBuffer_withValidFile_returnsByteBuffer() throws Exception {
    // Act: Read a binary file (PNG image)
    ByteBuffer buffer = AssetLoader.readResourceToByteBuffer("textures/player.png");

    // Assert: Buffer should contain data and be ready for reading
    assertThat(buffer).isNotNull();
    assertThat(buffer.position()).isEqualTo(0); // Should be flipped and ready to read
    assertThat(buffer.hasRemaining()).isTrue(); // Should contain data
  }

  @Test
  @DisplayName("readResourceToByteBuffer should handle paths with leading slash")
  void readResourceToByteBuffer_withLeadingSlash_readsByteBuffer() throws Exception {
    // Act: Read with leading slash (should be corrected internally)
    ByteBuffer buffer = AssetLoader.readResourceToByteBuffer("/textures/player.png");

    // Assert: Should successfully read the file
    assertThat(buffer).isNotNull();
    assertThat(buffer.hasRemaining()).isTrue();
  }

  @Test
  @DisplayName("readResourceToByteBuffer should throw IOException for non-existent file")
  void readResourceToByteBuffer_withNonExistentFile_throwsIOException() {
    // Act & Assert: Should throw IOException for missing file
    assertThatThrownBy(() -> AssetLoader.readResourceToByteBuffer("non/existent/file.png"))
      .isInstanceOf(java.io.IOException.class)
      .hasMessageContaining("Resource not found")
      .hasMessageContaining("non/existent/file.png");
  }

  @Test
  @DisplayName("readResourceToString should handle shader files with proper content")
  void readResourceToString_withShaderFile_returnsShaderSource() {
    // Act: Read a valid shader file
    String shaderSource = AssetLoader.readResourceToString("shaders/test.vert");

    // Assert: Should contain expected GLSL content
    assertThat(shaderSource)
      .contains("#version 330 core")
      .contains("layout (location = 0) in vec3 aPos")
      .contains("void main()");
  }

  @Test
  @DisplayName("readResourceToString should handle small text files")
  void readResourceToString_withSmallFile_returnsContent() {
    // Act: Read a small text file
    String content = AssetLoader.readResourceToString("textures/invalid_format.txt");

    // Assert: Should handle small files gracefully
    assertThat(content).isNotNull().isEqualTo("This is a text file, not an image");
  }
}
