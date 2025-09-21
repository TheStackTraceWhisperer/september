package september.engine.ui.components;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UITransformComponentTest {

  @Test
  @DisplayName("Default constructor should initialize with centered anchor and pivot")
  void defaultConstructor_initializesCorrectly() {
    // Act
    var transform = new UITransformComponent();

    // Assert
    assertThat(transform.anchor).isEqualTo(new Vector2f(0.5f, 0.5f));
    assertThat(transform.pivot).isEqualTo(new Vector2f(0.5f, 0.5f));
    assertThat(transform.size).isEqualTo(new Vector2f(100.0f, 100.0f));
    assertThat(transform.offset).isEqualTo(new Vector3f(0.0f, 0.0f, 0.0f));
  }

  @Test
  @DisplayName("Parameterized constructor should correctly assign all values")
  void parameterizedConstructor_assignsAllValues() {
    // Arrange
    var anchor = new Vector2f(1.0f, 1.0f);
    var pivot = new Vector2f(0.0f, 0.0f);
    var size = new Vector2f(200, 75);
    var offset = new Vector3f(-10, -10, 0);

    // Act
    var transform = new UITransformComponent(anchor, pivot, size, offset);

    // Assert
    assertThat(transform.anchor).isSameAs(anchor);
    assertThat(transform.pivot).isSameAs(pivot);
    assertThat(transform.size).isSameAs(size);
    assertThat(transform.offset).isSameAs(offset);
  }

  @Test
  @DisplayName("Parameterized constructor should handle null values gracefully")
  void parameterizedConstructor_handlesNulls() {
    // Act
    var transform = new UITransformComponent(null, null, null, null);

    // Assert
    assertThat(transform.anchor).isEqualTo(new Vector2f(0.5f, 0.5f));
    assertThat(transform.pivot).isEqualTo(new Vector2f(0.5f, 0.5f));
    assertThat(transform.size).isEqualTo(new Vector2f(100, 30));
    assertThat(transform.offset).isEqualTo(new Vector3f(0, 0, 0));
  }
}
