package september.engine.systems;

import org.joml.Matrix4f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import september.engine.assets.ResourceManager;
import september.engine.ecs.IWorld;
import september.engine.ecs.components.TransformComponent;
import september.engine.rendering.Camera;
import september.engine.rendering.Mesh;
import september.engine.rendering.Renderer;
import september.engine.rendering.Texture;
import september.game.components.SpriteComponent;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RenderSystemTest {

    @Mock private IWorld world;
    @Mock private Renderer renderer;
    @Mock private Camera camera;
    @Mock private ResourceManager resourceManager;
    @Mock private Mesh quadMesh;
    @Mock private Texture texture1;
    @Mock private Texture texture2;

    private RenderSystem renderSystem;

    @BeforeEach
    void setUp() {
        renderSystem = new RenderSystem(world, renderer, resourceManager, camera);
        // Common arrangement: The system will always try to resolve the quad mesh.
        when(resourceManager.resolveMeshHandle("quad")).thenReturn(quadMesh);
    }

    @Test
    @DisplayName("update() should correctly render all sprite entities in order")
    void update_rendersAllSpriteEntities() {
        // --- Arrange ---
        // Create two entities with transform and sprite components.
        int entity1 = 1;
        int entity2 = 2;
        TransformComponent transform1 = new TransformComponent();
        TransformComponent transform2 = new TransformComponent();
        SpriteComponent sprite1 = new SpriteComponent("tex1");
        SpriteComponent sprite2 = new SpriteComponent("tex2");

        // Mock the world to return these entities and their components.
        when(world.getEntitiesWith(TransformComponent.class, SpriteComponent.class)).thenReturn(List.of(entity1, entity2));
        when(world.getComponent(entity1, TransformComponent.class)).thenReturn(transform1);
        when(world.getComponent(entity1, SpriteComponent.class)).thenReturn(sprite1);
        when(world.getComponent(entity2, TransformComponent.class)).thenReturn(transform2);
        when(world.getComponent(entity2, SpriteComponent.class)).thenReturn(sprite2);

        // Mock the resource manager to resolve the texture handles.
        when(resourceManager.resolveTextureHandle("tex1")).thenReturn(texture1);
        when(resourceManager.resolveTextureHandle("tex2")).thenReturn(texture2);

        // --- Act ---
        renderSystem.update(0.016f);

        // --- Assert ---
        // Use InOrder to verify the sequence of rendering operations.
        InOrder inOrder = inOrder(renderer);

        // 1. The scene must be started.
        inOrder.verify(renderer).beginScene(camera);

        // 2. The first entity should be submitted with its correct texture and transform.
        inOrder.verify(renderer).submit(quadMesh, texture1, transform1.getTransformMatrix());

        // 3. The second entity should be submitted with its correct texture and transform.
        inOrder.verify(renderer).submit(quadMesh, texture2, transform2.getTransformMatrix());

        // 4. The scene must be ended.
        inOrder.verify(renderer).endScene();
    }

    @Test
    @DisplayName("update() should not submit anything if no renderable entities exist")
    void update_doesNothing_whenNoRenderableEntitiesExist() {
        // --- Arrange ---
        // Mock the world to return an empty list of entities.
        when(world.getEntitiesWith(TransformComponent.class, SpriteComponent.class)).thenReturn(Collections.emptyList());

        // --- Act ---
        renderSystem.update(0.016f);

        // --- Assert ---
        // Verify that the scene is still begun and ended.
        verify(renderer).beginScene(camera);
        verify(renderer).endScene();

        // Crucially, verify that no draw calls were ever submitted.
        verify(renderer, never()).submit(any(Mesh.class), any(Texture.class), any(Matrix4f.class));
    }
}
