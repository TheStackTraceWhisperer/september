package september.engine.ecs.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeshComponentTest {

    @Test
    void meshHandle_returnsConstructorValue() {
        // Arrange: Define a handle that the component will hold.
        String expectedHandle = "player_mesh";

        // Act: Create the component with the handle.
        MeshComponent component = new MeshComponent(expectedHandle);

        // Assert: Verify that the accessor method returns the exact handle provided at construction.
        assertEquals(expectedHandle, component.meshHandle());
    }
}
