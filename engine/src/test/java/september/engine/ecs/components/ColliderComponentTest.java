package september.engine.ecs.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColliderComponentTest {

  /**
   * A test-local implementation of the ColliderType interface.
   * This ensures the engine test is self-contained and not dependent on any game-specific code.
   */
  private enum TestColliderType implements ColliderComponent.ColliderType {
    TYPE_A,
    TYPE_B
  }

  @Test
  @DisplayName("Constructor should correctly initialize all collider properties")
  void constructor_initializesAllProperties() {
    // Arrange: Define the properties for a test collider using the test-local enum.
    ColliderComponent.ColliderType expectedType = TestColliderType.TYPE_A;
    int expectedWidth = 32;
    int expectedHeight = 48;
    int expectedOffsetX = -4;
    int expectedOffsetY = 0;

    // Act: Create the component.
    ColliderComponent component = new ColliderComponent(expectedType, expectedWidth, expectedHeight, expectedOffsetX, expectedOffsetY);

    // Assert: Verify that each getter returns the value provided at construction.
    assertThat(component.getType()).as("Collider type").isEqualTo(expectedType);
    assertThat(component.getWidth()).as("Width").isEqualTo(expectedWidth);
    assertThat(component.getHeight()).as("Height").isEqualTo(expectedHeight);
    assertThat(component.getOffsetX()).as("X offset").isEqualTo(expectedOffsetX);
    assertThat(component.getOffsetY()).as("Y offset").isEqualTo(expectedOffsetY);
  }
}
