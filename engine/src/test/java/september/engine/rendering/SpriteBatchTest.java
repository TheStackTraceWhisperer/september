package september.engine.rendering;

import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for SpriteBatch.
 * Tests batching logic without requiring OpenGL context.
 */
class SpriteBatchTest {

  private SpriteBatch spriteBatch;
  private Texture mockTexture1;
  private Texture mockTexture2;
  private Matrix4f transform1;
  private Matrix4f transform2;

  @BeforeEach
  void setUp() {
    spriteBatch = new SpriteBatch(100); // Max 100 instances per batch
    mockTexture1 = mock(Texture.class);
    mockTexture2 = mock(Texture.class);
    transform1 = new Matrix4f().identity().translate(1, 0, 0);
    transform2 = new Matrix4f().identity().translate(2, 0, 0);
  }

  @Test
  @DisplayName("New batch should be empty")
  void newBatch_shouldBeEmpty() {
    assertThat(spriteBatch.isEmpty()).isTrue();
    assertThat(spriteBatch.getTotalSpriteCount()).isEqualTo(0);
    assertThat(spriteBatch.getBatchCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Adding sprite should increase counts")
  void addSprite_shouldIncreaseCounts() {
    // Act
    spriteBatch.addSprite(mockTexture1, transform1);

    // Assert
    assertThat(spriteBatch.isEmpty()).isFalse();
    assertThat(spriteBatch.getTotalSpriteCount()).isEqualTo(1);
    assertThat(spriteBatch.getBatchCount()).isEqualTo(1);
    assertThat(spriteBatch.getSpriteCount(mockTexture1)).isEqualTo(1);
  }

  @Test
  @DisplayName("Adding sprites with same texture should group them")
  void addSprite_sameTexture_shouldGroupTogether() {
    // Act
    spriteBatch.addSprite(mockTexture1, transform1);
    spriteBatch.addSprite(mockTexture1, transform2);

    // Assert
    assertThat(spriteBatch.getTotalSpriteCount()).isEqualTo(2);
    assertThat(spriteBatch.getBatchCount()).isEqualTo(1); // Only one batch
    assertThat(spriteBatch.getSpriteCount(mockTexture1)).isEqualTo(2);

    var sprites = spriteBatch.getSpritesForTexture(mockTexture1);
    assertThat(sprites).hasSize(2);
    assertThat(sprites).containsExactly(transform1, transform2);
  }

  @Test
  @DisplayName("Adding sprites with different textures should create separate batches")
  void addSprite_differentTextures_shouldCreateSeparateBatches() {
    // Act
    spriteBatch.addSprite(mockTexture1, transform1);
    spriteBatch.addSprite(mockTexture2, transform2);

    // Assert
    assertThat(spriteBatch.getTotalSpriteCount()).isEqualTo(2);
    assertThat(spriteBatch.getBatchCount()).isEqualTo(2); // Two batches
    assertThat(spriteBatch.getSpriteCount(mockTexture1)).isEqualTo(1);
    assertThat(spriteBatch.getSpriteCount(mockTexture2)).isEqualTo(1);
  }

  @Test
  @DisplayName("getSpritesForTexture should return empty list for unknown texture")
  void getSpritesForTexture_unknownTexture_shouldReturnEmptyList() {
    // Arrange
    Texture unknownTexture = mock(Texture.class);

    // Act & Assert
    assertThat(spriteBatch.getSpritesForTexture(unknownTexture)).isEmpty();
    assertThat(spriteBatch.getSpriteCount(unknownTexture)).isEqualTo(0);
  }

  @Test
  @DisplayName("hasOversizedBatches should detect batches exceeding max instances")
  void hasOversizedBatches_shouldDetectOversizedBatches() {
    // Arrange - create a small batch size to test easily
    SpriteBatch smallBatch = new SpriteBatch(2);

    // Act - add more sprites than the max
    smallBatch.addSprite(mockTexture1, transform1);
    smallBatch.addSprite(mockTexture1, transform2);
    smallBatch.addSprite(mockTexture1, new Matrix4f().identity().translate(3, 0, 0));

    // Assert
    assertThat(smallBatch.hasOversizedBatches()).isTrue();
    assertThat(spriteBatch.hasOversizedBatches()).isFalse(); // Normal batch is fine
  }

  @Test
  @DisplayName("clear should reset all batches")
  void clear_shouldResetAllBatches() {
    // Arrange
    spriteBatch.addSprite(mockTexture1, transform1);
    spriteBatch.addSprite(mockTexture2, transform2);
    assertThat(spriteBatch.isEmpty()).isFalse();

    // Act
    spriteBatch.clear();

    // Assert
    assertThat(spriteBatch.isEmpty()).isTrue();
    assertThat(spriteBatch.getTotalSpriteCount()).isEqualTo(0);
    assertThat(spriteBatch.getBatchCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("getTextures should return all textures with sprites")
  void getTextures_shouldReturnAllTexturesWithSprites() {
    // Arrange
    spriteBatch.addSprite(mockTexture1, transform1);
    spriteBatch.addSprite(mockTexture2, transform2);

    // Act
    var textures = spriteBatch.getTextures();

    // Assert
    assertThat(textures).containsExactlyInAnyOrder(mockTexture1, mockTexture2);
  }

  @Test
  @DisplayName("getMaxInstancesPerBatch should return constructor value")
  void getMaxInstancesPerBatch_shouldReturnConstructorValue() {
    assertThat(spriteBatch.getMaxInstancesPerBatch()).isEqualTo(100);

    SpriteBatch customBatch = new SpriteBatch(500);
    assertThat(customBatch.getMaxInstancesPerBatch()).isEqualTo(500);
  }

  @Test
  @DisplayName("Default constructor should use default max instances")
  void defaultConstructor_shouldUseDefaultMaxInstances() {
    SpriteBatch defaultBatch = new SpriteBatch();
    assertThat(defaultBatch.getMaxInstancesPerBatch()).isEqualTo(1000);
  }
}
