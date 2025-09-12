package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerComponentTest {

    @Test
    @DisplayName("PlayerComponent should be instantiable")
    void testInstantiation() {
        // Arrange & Act
        PlayerComponent component = new PlayerComponent();

        // Assert
        assertThat(component).as("The component should be successfully instantiated.").isNotNull();
    }
}
