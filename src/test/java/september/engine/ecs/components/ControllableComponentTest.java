package september.engine.ecs.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllableComponentTest {

    @Test
    @DisplayName("Component should have a predictable default state upon creation")
    void testDefaultState() {
        // Arrange & Act: Create a new instance of the component.
        ControllableComponent component = new ControllableComponent();

        // Assert: Verify that all fields are initialized to their expected default values.
        assertAll("Default state verification",
                () -> assertEquals(0, component.playerId, "Default player ID should be 0."),
                () -> assertFalse(component.wantsToMoveUp, "Default wantsToMoveUp should be false."),
                () -> assertFalse(component.wantsToMoveDown, "Default wantsToMoveDown should be false."),
                () -> assertFalse(component.wantsToMoveLeft, "Default wantsToMoveLeft should be false."),
                () -> assertFalse(component.wantsToMoveRight, "Default wantsToMoveRight should be false."),
                () -> assertFalse(component.wantsToAttack, "Default wantsToAttack should be false.")
        );
    }
}
