package september.engine.ecs.components;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeshComponentTest {

  @Test
  void meshHandle_returnsConstructorValue() {
    // Arrange
    String expectedHandle = "player_mesh";

    // Act
    MeshComponent component = new MeshComponent(expectedHandle);

    // Assert
    assertThat(component.meshHandle()).isEqualTo(expectedHandle);
  }
}
