package september.engine.ecs.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ControllableComponentTest {

  @Test
  @DisplayName("Component should have a predictable default state upon creation")
  void testDefaultState() {
    // Arrange & Act
    ControllableComponent component = new ControllableComponent();

    // Assert
    assertThat(component.playerId).as("Default player ID").isZero();
    assertThat(component.wantsToMoveUp).as("Default wantsToMoveUp").isFalse();
    assertThat(component.wantsToMoveDown).as("Default wantsToMoveDown").isFalse();
    assertThat(component.wantsToMoveLeft).as("Default wantsToMoveLeft").isFalse();
    assertThat(component.wantsToMoveRight).as("Default wantsToMoveRight").isFalse();
    assertThat(component.wantsToAttack).as("Default wantsToAttack").isFalse();
  }
}
