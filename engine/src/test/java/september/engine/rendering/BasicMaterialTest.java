package september.engine.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.rendering.gl.Shader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for BasicMaterial.
 * Uses mocked dependencies to test material behavior without requiring OpenGL context.
 */
class BasicMaterialTest {

  private Shader mockShader;
  private Texture mockTexture;

  @BeforeEach
  void setUp() {
    mockShader = mock(Shader.class);
    mockTexture = mock(Texture.class);
  }

  @Test
  @DisplayName("Constructor should create material with all required fields")
  void constructor_createsValidMaterial() {
    // Act
    BasicMaterial material = new BasicMaterial("TestMaterial", mockShader, mockTexture);

    // Assert
    assertThat(material.getName()).isEqualTo("TestMaterial");
    assertThat(material.getShader()).isEqualTo(mockShader);
    assertThat(material.getTexture()).isEqualTo(mockTexture);
  }

  @Test
  @DisplayName("Constructor should allow null texture")
  void constructor_allowsNullTexture() {
    // Act
    BasicMaterial material = new BasicMaterial("TestMaterial", mockShader, null);

    // Assert
    assertThat(material.getName()).isEqualTo("TestMaterial");
    assertThat(material.getShader()).isEqualTo(mockShader);
    assertThat(material.getTexture()).isNull();
  }

  @Test
  @DisplayName("Constructor should throw exception for null name")
  void constructor_throwsExceptionForNullName() {
    // Act & Assert
    assertThatThrownBy(() -> new BasicMaterial(null, mockShader, mockTexture))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("Material name cannot be null");
  }

  @Test
  @DisplayName("Constructor should throw exception for null shader")
  void constructor_throwsExceptionForNullShader() {
    // Act & Assert
    assertThatThrownBy(() -> new BasicMaterial("TestMaterial", null, mockTexture))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("Shader cannot be null");
  }

  @Test
  @DisplayName("bind should bind shader and texture when texture is present")
  void bind_bindsShaderAndTexture_whenTexturePresent() {
    // Arrange
    BasicMaterial material = new BasicMaterial("TestMaterial", mockShader, mockTexture);

    // Act
    material.bind();

    // Assert
    verify(mockShader).bind();
    verify(mockTexture).bind(0);
    verify(mockShader).setUniform("uTextureSampler", 0);
  }

  @Test
  @DisplayName("bind should only bind shader when texture is null")
  void bind_onlyBindsShader_whenTextureIsNull() {
    // Arrange
    BasicMaterial material = new BasicMaterial("TestMaterial", mockShader, null);

    // Act
    material.bind();

    // Assert
    verify(mockShader).bind();
    // No texture operations should be performed
  }

  @Test
  @DisplayName("unbind should unbind shader")
  void unbind_unbindsShader() {
    // Arrange
    BasicMaterial material = new BasicMaterial("TestMaterial", mockShader, mockTexture);

    // Act
    material.unbind();

    // Assert
    verify(mockShader).unbind();
  }

  @Test
  @DisplayName("toString should include material name and texture status")
  void toString_includesNameAndTextureStatus() {
    // Arrange
    BasicMaterial materialWithTexture = new BasicMaterial("WithTexture", mockShader, mockTexture);
    BasicMaterial materialWithoutTexture = new BasicMaterial("WithoutTexture", mockShader, null);

    // Act & Assert
    assertThat(materialWithTexture.toString())
      .contains("WithTexture")
      .contains("hasTexture=true");

    assertThat(materialWithoutTexture.toString())
      .contains("WithoutTexture")
      .contains("hasTexture=false");
  }
}