package september.engine.systems;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import september.engine.EngineTestHarness;
import september.engine.ecs.components.SpriteComponent;
import september.engine.ecs.components.TransformComponent;

import static org.assertj.core.api.Assertions.assertThat;
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
        world.addComponent(entity, new SpriteComponent("player_texture", new Vector4f(1.0f, 0.0f, 0.0f, 1.0f))); // Use a pre-loaded texture

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

    @Test
    @DisplayName("update() should handle multiple sprite entities")
    void update_handlesMultipleSpriteEntities_withoutError() {
        // --- Arrange ---
        int entity1 = world.createEntity();
        TransformComponent transform1 = new TransformComponent();
        transform1.position.set(0.0f, 0.0f, 0.0f);
        world.addComponent(entity1, transform1);
        world.addComponent(entity1, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        int entity2 = world.createEntity();
        TransformComponent transform2 = new TransformComponent();
        transform2.position.set(5.0f, 3.0f, 0.0f);
        world.addComponent(entity2, transform2);
        world.addComponent(entity2, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        int entity3 = world.createEntity();
        TransformComponent transform3 = new TransformComponent();
        transform3.position.set(-2.0f, -1.0f, 0.0f);
        world.addComponent(entity3, transform3);
        world.addComponent(entity3, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // --- Act & Assert ---
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem should handle multiple entities without errors")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should handle entities with different transform matrices")
    void update_handlesTransformedSprites_withoutError() {
        // --- Arrange ---
        int entity = world.createEntity();
        TransformComponent transform = new TransformComponent();

        // Set up a complex transform
        transform.position.set(2.0f, 3.0f, 1.0f);
        transform.rotation.rotateZ((float) Math.toRadians(45)); // 45 degree rotation
        transform.scale.set(1.5f, 2.0f, 1.0f); // Non-uniform scaling

        world.addComponent(entity, transform);
        world.addComponent(entity, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // --- Act & Assert ---
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem should handle transformed sprites without errors")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should handle entities with only TransformComponent")
    void update_handlesEntitiesWithoutSprites_gracefully() {
        // --- Arrange ---
        int entityWithoutSprite = world.createEntity();
        world.addComponent(entityWithoutSprite, new TransformComponent());

        int entityWithSprite = world.createEntity();
        world.addComponent(entityWithSprite, new TransformComponent());
        world.addComponent(entityWithSprite, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // --- Act & Assert ---
        // The system should only render entities that have both components
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem should ignore entities without sprite components")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should handle entities with only SpriteComponent")
    void update_handlesEntitiesWithoutTransforms_gracefully() {
        // --- Arrange ---
        int entityWithoutTransform = world.createEntity();
        world.addComponent(entityWithoutTransform, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        int entityWithBoth = world.createEntity();
        world.addComponent(entityWithBoth, new TransformComponent());
        world.addComponent(entityWithBoth, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // --- Act & Assert ---
        // The system should only render entities that have both components
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem should ignore entities without transform components")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should work with camera transformations")
    void update_worksWithCameraTransformations_withoutError() {
        // --- Arrange ---
        int entity = world.createEntity();
        world.addComponent(entity, new TransformComponent());
        world.addComponent(entity, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // Move the camera to a different position
        camera.setPosition(new Vector3f(10.0f, 5.0f, 3.0f));

        // --- Act & Assert ---
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem should work with camera transformations")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should handle rapid successive calls")
    void update_handlesRapidSuccessiveCalls_withoutError() {
        // --- Arrange ---
        int entity = world.createEntity();
        world.addComponent(entity, new TransformComponent());
        world.addComponent(entity, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // --- Act & Assert ---
        // Simulate multiple rapid frame updates
        assertThatCode(() -> {
            for (int i = 0; i < 10; i++) {
                renderSystem.update(0.016f);
            }
        }).as("RenderSystem should handle rapid successive calls without errors")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should handle entity addition and removal during rendering")
    void update_handlesEntityChanges_gracefully() {
        // --- Arrange ---
        int permanentEntity = world.createEntity();
        world.addComponent(permanentEntity, new TransformComponent());
        world.addComponent(permanentEntity, new SpriteComponent("player_texture", new Vector4f(1.0f)));

        // --- Act & Assert ---
        assertThatCode(() -> {
            // Render with initial entity
            renderSystem.update(0.016f);

            // Add new entity
            int newEntity = world.createEntity();
            world.addComponent(newEntity, new TransformComponent());
            world.addComponent(newEntity, new SpriteComponent("player_texture", new Vector4f(1.0f)));

            // Render with both entities
            renderSystem.update(0.016f);

            // Remove the new entity
            world.destroyEntity(newEntity);

            // Render with just the permanent entity
            renderSystem.update(0.016f);

        }).as("RenderSystem should handle entity changes gracefully")
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("update() should work correctly with transform components")
    void update_worksWithTransformComponents_withoutError() {
        // --- Arrange ---
        int entity = world.createEntity();
        TransformComponent transform = new TransformComponent();

        // Set up a complex transform
        transform.position.set(2.0f, 3.0f, 1.0f);
        transform.rotation.rotateZ((float) Math.toRadians(45)); // 45 degree rotation
        transform.scale.set(1.5f, 2.0f, 1.0f); // Non-uniform scaling

        world.addComponent(entity, transform);
        world.addComponent(entity, new SpriteComponent("player_texture", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f)));

        // --- Act & Assert ---
        // The key test is that the system can handle complex transforms without errors
        assertThatCode(() -> renderSystem.update(0.016f))
                .as("RenderSystem should handle complex transforms without errors")
                .doesNotThrowAnyException();

        // Verify the transform matrix can be calculated
        Matrix4f transformMatrix = transform.getTransformMatrix();
        assertThat(transformMatrix).as("Transform matrix should not be null").isNotNull();
    }
}
