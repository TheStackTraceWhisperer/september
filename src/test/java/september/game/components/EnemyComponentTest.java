package september.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnemyComponentTest {

    @Test
    @DisplayName("EnemyComponent should be instantiable")
    void testInstantiation() {
        // Arrange & Act: Attempt to create an instance of the marker component.
        EnemyComponent component = new EnemyComponent();

        // Assert: If no exception is thrown, the test passes. We can add a simple not-null check for clarity.
        assertNotNull(component, "The component should be successfully instantiated.");
    }
}
