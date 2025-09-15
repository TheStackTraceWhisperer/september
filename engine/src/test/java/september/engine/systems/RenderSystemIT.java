package september.engine.systems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Integration smoke test for the RenderSystem.
 * This test verifies that the system can successfully render a scene with a renderable entity
 * using a live world, renderer, resource manager, and camera.
 */
class RenderSystemIT extends EngineTestHarness {

    private RenderSystem renderSystem;

    @BeforeEach
    void setupSystem() {
        // The harness provides a live world, renderer, resource manager, and camera.
        // We use them to construct the system under test.
        renderSystem = new RenderSystem(world, engine.getRenderer(), resourceManager, camera);
    }

    @Test
    @DisplayName("update() should render a sprite entity without throwing an exception")
    void update_rendersSpriteEntity_withoutError() {
        // --- Arrange ---
        // The harness already calls engine.init(), which loads the "quad" mesh and the "player_texture".

        // Create a renderable entity in the world.
        int entity = world.createEntity();
        world.addComponent(entity, new TransformComponent());
        world.addComponent(entity, new SpriteComponent("player_texture")); // Use a pre-loaded texture

        // --- Act & Assert ---
        // The test passes if the entire rendering pipeline executes without any exceptions.
        // This proves that the system can get entities, resolve real mesh and texture handles,
        // and submit them to the real renderer, which then makes real OpenGL calls.
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem update should execute without any errors")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should run without error when no renderable entities exist")
    void update_runsWithoutError_onEmptyScene() {
        // --- Arrange ---
        // The world is empty by default.

        // --- Act & Assert ---
        // The test passes if the system can handle an empty scene gracefully.
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem update on an empty scene should not throw any errors")
                .doesNotThrowAnyException();
    }
}
