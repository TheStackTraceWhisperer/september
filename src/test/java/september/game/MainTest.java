package september.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import september.engine.core.Engine;
import september.engine.core.MainLoopPolicy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit test for the Main class, focusing on its responsibility as the application's
 * "composition root".
 */
class MainTest {

    @Test
    @DisplayName("Main.run() should construct and run the Engine")
    void run_constructsAndRunsEngine() {
        // This test verifies that Main correctly instantiates all services and passes them
        // to the Engine. We mock the Engine's construction to isolate the test to Main's logic.
        assertDoesNotThrow(() -> {
            // Arrange: Mock the construction of the Engine itself.
            try (MockedConstruction<Engine> engineConstruction = mockConstruction(Engine.class)) {

                // Act: Run the main class with a policy that only initializes.
                // This is sufficient to test the wiring logic.
                new Main(MainLoopPolicy.initializeOnly()).run();

                // Assert
                // 1. Verify that exactly one Engine instance was created.
                assertEquals(1, engineConstruction.constructed().size());
                Engine constructedEngine = engineConstruction.constructed().get(0);

                // 2. Verify that the engine's run method was called on the constructed instance.
                verify(constructedEngine).run();
            }
        });
    }
}
