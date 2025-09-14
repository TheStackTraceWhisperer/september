package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnemyComponentTest {

    @Test
    @DisplayName("EnemyComponent should be instantiable")
    void testInstantiation() {
        // Arrange & Act
        EnemyComponent component = new EnemyComponent();

        // Assert
        assertThat(component).as("The component should be successfully instantiated.").isNotNull();
    }
}
